package com.hbvibe.cart.service;

import com.hbvibe.cart.client.ProductServiceClient;
import com.hbvibe.cart.dto.ApiResponse;
import com.hbvibe.cart.dto.request.AddToCartRequest;
import com.hbvibe.cart.dto.response.*;
import com.hbvibe.cart.entity.Cart;
import com.hbvibe.cart.entity.CartItem;
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
}
