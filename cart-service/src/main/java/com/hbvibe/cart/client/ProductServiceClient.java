package com.hbvibe.cart.client;

import com.hbvibe.cart.dto.ApiResponse;
import com.hbvibe.cart.dto.response.VariantForCartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8083")
public interface ProductServiceClient {

    @GetMapping("/product/api/v1/{id}/cart-info")
    ApiResponse<VariantForCartResponse> getVariantInfo(@PathVariable("id") Long variantId);
}
