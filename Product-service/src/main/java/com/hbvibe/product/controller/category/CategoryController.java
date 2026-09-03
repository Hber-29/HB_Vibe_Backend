package com.hbvibe.product.controller.category;

import com.hbvibe.product.dto.ApiResponse;
import com.hbvibe.product.dto.category.request.CategoryCreateRequest;
import com.hbvibe.product.dto.category.request.CategoryUpdateRequest;
import com.hbvibe.product.dto.category.response.CategoryCreateResponse;
import com.hbvibe.product.dto.category.response.CategoryTreeResponse;
import com.hbvibe.product.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping("/create-category")
    public ApiResponse<CategoryCreateResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return ApiResponse.<CategoryCreateResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    // chức năng lấy tất cả các danh mục
    @GetMapping("/categories-tree")
    public ApiResponse<List<CategoryTreeResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryTreeResponse>>builder()
                .message("Lấy tất cả các danh mục thành công !")
                .result(categoryService.getCategoryTree())
                .build();
    }

    @PutMapping("/edit-category/{id}")
    public ApiResponse<CategoryCreateResponse> updateCategory (
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request
    ){
        return ApiResponse.<CategoryCreateResponse>builder()
                .message("Đã cập nhật danh mục thành công !")
                .result(categoryService.updateCategory(request,id))
                .build();

    }
}
