package com.hbvibe.cart.service;

import com.hbvibe.cart.client.ProductServiceClient;
import com.hbvibe.cart.dto.ApiResponse;
import com.hbvibe.cart.dto.request.AddToCartRequest;
import com.hbvibe.cart.dto.response.*;
import com.hbvibe.cart.entity.Cart;
import com.hbvibe.cart.entity.CartItem;
import com.hbvibe.cart.exception.AppException;
import com.hbvibe.cart.exception.ErrorCode;
import com.hbvibe.cart.repository.CartItemRepository;
import com.hbvibe.cart.repository.CartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)


public class CartService {
    ProductServiceClient productServiceClient;
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    @Transactional
    public CartItemResponse addToCart(String userId, AddToCartRequest request) {
        ApiResponse<VariantForCartResponse> apiResponse = productServiceClient
                .getVariantInfo(request.getProductVariantId());
        VariantForCartResponse variantInfo = apiResponse.getResult();
        if(variantInfo==null || !variantInfo.getIsActive()){
           throw new RuntimeException("Sản phẩm không tồn tại hoặc đã ngừng bán");
        }
        if (variantInfo.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Số lượng hàng tồn kho không đủ");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(()->{
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductVariantId().equals(request.getProductVariantId()))
                .findFirst();
        CartItem itemToSave ;
        if(existingItem.isPresent()){
            itemToSave = existingItem.get();
            itemToSave.setQuantity(itemToSave.getQuantity()+request.getQuantity());
            itemToSave.setPrice(variantInfo.getPrice());
            itemToSave.setIsSelected(true);
        }else {
            itemToSave = CartItem.builder()
                    .productVariantId(request.getProductVariantId())
                    .quantity(request.getQuantity())
                    .price(variantInfo.getPrice())
                    .isSelected(true)
                    .build();
            cart.addItem(itemToSave);
        }

        Cart saveCart = cartRepository.save(cart);

        CartItem saveItem = saveCart.getItems().stream()
                .filter(item -> item.getProductVariantId().equals(request.getProductVariantId()))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Lỗi chưa lưu giở hàng"));

        return CartItemResponse.builder()
                .id(saveItem.getId())
                .productVariantId(saveItem.getProductVariantId())
                .quantity(saveItem.getQuantity())
                .price(saveItem.getPrice())
                .isSelected(saveItem.getIsSelected())
                .build();
    }
    // hàm thưucj hiện chức năng xem giửo hàng
    public CartResponse getCart(String userId){

        // lấy giở hàng từ db
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if(cart==null || cart.getItems().isEmpty()){
            return CartResponse.builder()
                    .items(List.of())
                    .totalSelectedPrice(BigDecimal.ZERO)
                    .build();
        }

        List<Long> variantIds= cart.getItems().stream()
                .map(CartItem::getProductVariantId)
                .collect(Collectors.toList());
        List<VariantForCartResponse> latestInfoList = productServiceClient
                .getVariantForBulk(variantIds).getResult();
        Map<Long,VariantForCartResponse> variantInfoMap = latestInfoList.stream()
                .collect(Collectors.toMap(VariantForCartResponse::getVariantId,info->info));

        BigDecimal totalSelectedPrice = BigDecimal.ZERO;
        List<CartItemDetailResponse> itemResponses = new ArrayList<>();

        for(CartItem cartItem : cart.getItems()){
            VariantForCartResponse latestInfo = variantInfoMap.get(cartItem.getProductVariantId());
            boolean isAvailable = false;
            BigDecimal currentPrice = latestInfo.getPrice();
            PriceChangeType priceChange = PriceChangeType.NONE;
            Integer currenttStock = 0;
            String image = null, productName = null, size = null,
                    color = null;

            if(latestInfo != null){
                isAvailable = Boolean.TRUE
                        .equals(latestInfo.getIsActive()) && latestInfo.getStockQuantity()>0;
                currentPrice = latestInfo.getPrice();
                currenttStock = latestInfo.getStockQuantity();
                image = latestInfo.getImage();
                productName = latestInfo.getProductName();
                size = latestInfo.getSize();
                color = latestInfo.getColor();
                // xử lý trạng thái giá
                int priceCompare = currentPrice.compareTo(cartItem.getPrice());
                if(priceCompare<0) priceChange = PriceChangeType.DECREASED;
                else if(priceCompare>0) priceChange = PriceChangeType.INCREASED;

            }

            // tinhs tiền món đang được tick và còn hàng(tính theo dạng lệnh chữ ,chứ không dùng =,-*)
            if(cartItem.getIsSelected() && isAvailable){
                totalSelectedPrice = totalSelectedPrice.add(
                        currentPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                );
            }

            itemResponses.add(CartItemDetailResponse.builder()
                    .id(cartItem.getId())
                    .productVariantId(cartItem.getProductVariantId())
                    .quantity(cartItem.getQuantity())
                    .image(image)
                    .productName(productName)
                    .size(size)
                    .color(color)
                    .savedPrice(cartItem.getPrice())
                    .currentPrice(currentPrice)
                    .priceChangeType(priceChange)
                    .isAvailable(isAvailable)
                    .currentStock(currenttStock)
                    .isSelected(cartItem.getIsSelected())
                    .build());
        }

        return CartResponse.builder()
                .items(itemResponses)
                .totalSelectedPrice(totalSelectedPrice)
                .build();

    }
    // Các Hàm xử lý chức năng cập nhật lại giỏ hàng
    // hàm này là hàm kiểm tra tính hợp lệ của sản phẩm trong giỏ hàng (dùng chung)
    private CartItem getValidCartItem(String userId , Long itemId){
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(()-> new RuntimeException("không tìm thấy sản phẩm trong giỏ hàng"));
        if(!item.getCart().getUserId().equals(userId)){
            throw new RuntimeException("Sản phẩm không thuộc giỏ hàng !");
        }
        return item;

    }
    // update số lượng
    @Transactional
    public void updateItemQuantity (String userId, Long itemId , Integer newQuantity){
        if(newQuantity < 1 ){
            throw new RuntimeException("Số lượng không hợp lệ !");
        }
        CartItem item = getValidCartItem(userId, itemId);
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
    }
    // update trạng thái có mua hàng hay không
    @Transactional
    public void updateItemSelection (String userId, Long itemId, boolean isSelected){
        CartItem item = getValidCartItem(userId, itemId);
        item.setIsSelected(isSelected);
        cartItemRepository.save(item);
    }
    // hàm chỉnh sửa size và color(nó là thay thế biến thể sản phẩm )
    @Transactional
    public void updateItemVariant (String userId , Long itemId, Long newVariantId){
        // lấy ra sản phẩm cần sửa
        CartItem currentItem = getValidCartItem(userId, itemId);
        // nếu sửa mà vẫn giữ nguyên sẽ không thay đổi gì cả
        if(currentItem.getProductVariantId().equals(newVariantId)){
            return;
        }

        Long cartId = currentItem.getCart().getId();
        // tìm xem trong giở hàng có tồn tại sản phẩm giống sản phẩm hiện tại sau khi sửa size và color không ?
        // nếu có sẽ gộp số lượng của cái mới này vào cái đã tồn tại luôn
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductVariantId(cartId, newVariantId);
        if(existingItemOpt.isPresent()){
            CartItem targetItem = existingItemOpt.get();
            // gôp số lượng của cả 2 để gộp thành 1 giair quyết vẫn đề trùng sản phẩm
            targetItem.setQuantity(targetItem.getQuantity() + currentItem.getQuantity());
            // nếu sản phẩm cũ đã được tích chọn thì lấy luôn
            if(currentItem.getIsSelected()){
                targetItem.setIsSelected(true);
            }
            // xóa cái cũ đi
            cartItemRepository.delete(currentItem);
            cartItemRepository.save(targetItem);

        }else {
            // nếu không có sản phẩm nào tồn tại thì sẽ thay newVariantId vào sản phẩm đó
            currentItem.setProductVariantId(newVariantId);
            cartItemRepository.save(currentItem);
        }
    }

    // Chức năng xóa sản phẩm khỏi giỏ hàng
    public void removeCratItem(String userId, Long itemId){
        CartItem cartItem = getValidCartItem(userId, itemId);
        cartItemRepository.delete(cartItem);
    }
}
