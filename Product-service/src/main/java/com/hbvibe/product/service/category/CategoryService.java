package com.hbvibe.product.service.category;

import com.hbvibe.product.dto.category.request.CategoryCreateRequest;
import com.hbvibe.product.dto.category.response.CategoryCreateResponse;
import com.hbvibe.product.dto.category.response.CategoryTreeResponse;
import com.hbvibe.product.entity.Category;
import com.hbvibe.product.entity.Status;
import com.hbvibe.product.exception.AppException;
import com.hbvibe.product.exception.ErrorCode;
import com.hbvibe.product.mapper.category.CategoryMapper;
import com.hbvibe.product.repository.category.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Transactional
    public CategoryCreateResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        // lọc trùng slug
        String generatedSlug = generateSlug(categoryCreateRequest.getName());
        if (categoryRepository.existsBySlug(generatedSlug)) {
            throw new AppException(ErrorCode.NAME_CATEGORY_EXITED);
        }
        Category newCategory = Category.builder()
                .name(categoryCreateRequest.getName())
                .slug(generatedSlug)
                .description(categoryCreateRequest.getDescription())
                .image(categoryCreateRequest.getImage())
                .sortOrder(categoryCreateRequest.getSortOrder() != null ? categoryCreateRequest.getSortOrder() : 0)
                .status(Status.ACTIVE)
                .metaTitle(categoryCreateRequest.getMetaTitle())
                .metaDescription(categoryCreateRequest.getMetaDescription())
                .build();
        // xác định cha - con
        if(categoryCreateRequest.getParentId() != null){
            Category parentCategory = categoryRepository.findById(categoryCreateRequest.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CANNOT_CATEGORY));
            newCategory.setParent(parentCategory);
            newCategory.setLevel(parentCategory.getLevel() + 1);
        }else {
            newCategory.setLevel(0);
        }
        Category savedCategory = categoryRepository.save(newCategory);

        return categoryMapper.toCategoryCreateResponse(savedCategory);


    }

    // hàm lấy ra tất cả các danh mục để hiển thị ở FE
    public List<CategoryTreeResponse> getCategoryTree(){
        List<Category> rootCategories = categoryRepository.findByParentIsNullOrderBySortOrderAscNameAsc();
        return rootCategories.stream()
                .map(this::mapToTreeResponse)
                .collect(Collectors.toList());
    }
    // hàm đệ quy(tự gọi chính nó ) để đào sâu vào các lớp con của con
    private CategoryTreeResponse mapToTreeResponse(Category category){
        CategoryTreeResponse response = CategoryTreeResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .level(category.getLevel())
                .sortOrder(category.getSortOrder())
                .build();
        if(category.getChildren() !=null && !category.getChildren().isEmpty()){
            List<CategoryTreeResponse> childrenDTOs = category.getChildren().stream()
                    .map(this::mapToTreeResponse)
                    .collect(Collectors.toList());
            response.setChildren(childrenDTOs);
        }
        return response;
    }





    // hàm chuẩn hóa name thành slug
    private String generateSlug(String input) {
        if (input == null || input.isEmpty()) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("").toLowerCase();
        return slug.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
    }
}
