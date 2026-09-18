package com.hbvibe.product.controller;

import com.hbvibe.product.dto.ApiResponse;
import com.hbvibe.product.dto.request.ProductRequest;
import com.hbvibe.product.dto.request.UpdateProductRequest;
import com.hbvibe.product.dto.response.*;
import com.hbvibe.product.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/api/v1")
public class ProductController {
    ProductService productService;

    // chức năng tạo product của brand (quyền của brand)
    @PostMapping("/brands/{brandId}")
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
    // hàm lấy tất cả sản phẩm hiển thị lên trang
    @GetMapping
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
    // hàm lấy tất cả các sản phẩm của 1 brand hiển thị lên trang của brand
    @GetMapping("/brands/{brandId}")
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
    // lấy chi tiết sản phẩm
    @GetMapping("/{slug}")
    public ApiResponse<ProductResponse> getProductDetail(@PathVariable String slug){
        return ApiResponse.<ProductResponse>builder()
                .message("Lấy Thông tin chi tiết sản phẩm thành công")
                .result(productService.getProductDetails(slug))
                .build();
    }

    // chỉnh sửa thông tin sản phẩm
    @PutMapping("/{productId}")
    public ApiResponse<UpdateProductResponse> updateProductDetails(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest updateProductRequest
            ){
        return ApiResponse.<UpdateProductResponse>builder()
                .message("Cập nhật thông tin sản phẩm thành công ")
                .result(productService.updateProductDetails(productId,updateProductRequest))
                .build();
    }
    // chức năng xóa sản phẩm của admin
    @DeleteMapping("/admin/{productId}")
    public ApiResponse<Void> deleteProductByAdmin(@PathVariable Long productId){
        productService.deleteProductByAdmin(productId);
        return ApiResponse.<Void>builder()
                .message("Admin đã xóa sản phẩm thành công !")
                .build();
    }
    @DeleteMapping("/seller/brands/{brandId}/products/{productId}")
    public ApiResponse<Void> deleteProductByBrand(
            @PathVariable String brandId,
            @PathVariable Long productId,
            JwtAuthenticationToken jwt
    ){
        String userId = jwt.getName();
        productService.deleteProductByBrand(productId,brandId,userId);
        return ApiResponse.<Void>builder()
                .message("Gian hàng xóa sản phẩm thành công !")
                .build();
    }
    // CÁC API DÀNH RIÊNG CHO CART-SERVICE
    // api giups cart service gọi sang product để kấy thông tin sản phẩm
    @GetMapping("/variants/{id}/cart-info")
    public ApiResponse<VariantForCartResponse> getVariantForCart(@PathVariable Long id){
        return ApiResponse.<VariantForCartResponse>builder()
                .message("Lấy thông tin sản phẩm cho giỏ hàng thành công !")
                .result(productService.getVariantForCart(id))
                .build();
    }
    // lý do dùng post vì get chuẩn sẽ không được sử dụng gửi dữ liệu trong body ,vì vậy tôi phải dùng post(phá lệ hợp pháp)
    @PostMapping("/variants/cart-info-bulk")
    public ApiResponse<List<VariantForCartResponse>> getVariantForCartBulk(@RequestBody List<Long> ids){
        return ApiResponse.<List<VariantForCartResponse>>builder()
                .message("Lấy danh sách thông tin sản phẩm thành công !")
                .result(productService.getVariantForCartBulk(ids))
                .build();
    }
}
