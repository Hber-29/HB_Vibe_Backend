package com.hbvibe.product.mapper.category;


import com.hbvibe.product.dto.category.response.CategoryCreateResponse;
import com.hbvibe.product.entity.Category;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryCreateResponse toCategoryCreateResponse(Category category);
}
