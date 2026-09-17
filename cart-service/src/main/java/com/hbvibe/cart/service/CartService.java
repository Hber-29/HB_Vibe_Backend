package com.hbvibe.cart.service;

import com.hbvibe.cart.client.ProductServiceClient;
import com.hbvibe.cart.dto.ApiResponse;
import com.hbvibe.cart.dto.request.AddToCartRequest;
import com.hbvibe.cart.dto.response.CartItemResponse;
import com.hbvibe.cart.dto.response.VariantForCartResponse;
import com.hbvibe.cart.entity.Cart;
import com.hbvibe.cart.entity.CartItem;
import com.hbvibe.cart.repository.CartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

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
}
