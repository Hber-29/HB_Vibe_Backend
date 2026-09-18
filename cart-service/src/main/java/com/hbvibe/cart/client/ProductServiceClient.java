package com.hbvibe.cart.client;

import com.hbvibe.cart.dto.ApiResponse;
import com.hbvibe.cart.dto.response.VariantForCartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8083")
public interface ProductServiceClient {

    @GetMapping("/product/api/v1/variants/{id}/cart-info")
    ApiResponse<VariantForCartResponse> getVariantInfo(@PathVariable("id") Long variantId);
    @PostMapping("/product/api/v1/variants/cart-info-bulk")
    ApiResponse<List<VariantForCartResponse>> getVariantForBulk(@RequestBody List<Long> Ids);
}
