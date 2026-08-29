package com.hbvibe.product.controller;

import com.hbvibe.product.dto.ApiResponse;
import com.hbvibe.product.dto.request.ProductRequest;
import com.hbvibe.product.dto.response.PageResponse;
import com.hbvibe.product.dto.response.ProductListResponse;
import com.hbvibe.product.dto.response.ProductResponse;
import com.hbvibe.product.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping("/create-product/{brandId}")
    public ApiResponse<ProductResponse> createProduct(
            @RequestBody ProductRequest productRequest,
            @PathVariable String brandId,
            JwtAuthenticationToken jwt
    ) {

        System.out.println("=== KIỂM TRA DỮ LIỆU TỪ POSTMAN GỬI LÊN ===");
        System.out.println("Biến thể nhận được: " + productRequest.getVariants());
        String userId = jwt.getName();
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(userId,brandId,productRequest))
                .build();
    }
    @GetMapping("/getall-products")
    public ApiResponse<PageResponse<ProductListResponse>> getAllProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ProductListResponse> pageResponse = productService.getAllProducts(page, size);
        return ApiResponse.<PageResponse<ProductListResponse>>builder()
                .message("Lấy sản phẩm thành công ")
                .result(pageResponse)
                .build();
    }

    @GetMapping("/getbrand-products/{brandId}")
    public ApiResponse<PageResponse<ProductListResponse>> getAllProductsByBrand(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String brandId

    ) {
        PageResponse<ProductListResponse> pageResponse = productService.getAllProductsByBrand(page,size,brandId);
        return ApiResponse.<PageResponse<ProductListResponse>>builder()
                .message("Lấy sản phẩm của brand thành công ")
                .result(pageResponse)
                .build();
    }
    @GetMapping("/detail-product/{slug}")
    public ApiResponse<ProductResponse> getProductDetail(@PathVariable String slug){
        return ApiResponse.<ProductResponse>builder()
                .message("Lấy Thông tin chi tiết sản phẩm thành công")
                .result(productService.getProductDetails(slug))
                .build();
    }
}
