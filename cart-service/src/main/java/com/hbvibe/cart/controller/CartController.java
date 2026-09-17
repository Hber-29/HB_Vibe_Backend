package com.hbvibe.cart.controller;


import com.hbvibe.cart.dto.ApiResponse;
import com.hbvibe.cart.dto.request.AddToCartRequest;
import com.hbvibe.cart.dto.response.CartItemResponse;
import com.hbvibe.cart.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/api/v1/carts")
public class CartController {
    CartService cartService;

    @PostMapping("/item")
    public ApiResponse<CartItemResponse> addToCart (
            @RequestBody AddToCartRequest request,
            JwtAuthenticationToken jwt
    ){
        String userId = jwt.getName();
        return ApiResponse.<CartItemResponse>builder()
                .message("Đã thêm sản phẩm vào giỏ hàng thành công")
                .result(cartService.addToCart(userId, request))
                .build();
    }
}
