package com.hbvibe.product.controller.category;

import com.hbvibe.product.dto.ApiResponse;
import com.hbvibe.product.dto.category.request.CategoryCreateRequest;
import com.hbvibe.product.dto.category.response.CategoryCreateResponse;
import com.hbvibe.product.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
