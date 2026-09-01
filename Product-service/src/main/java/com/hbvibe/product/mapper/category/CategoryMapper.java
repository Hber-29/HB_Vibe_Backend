package com.hbvibe.product.mapper.category;


import com.hbvibe.product.dto.category.response.CategoryCreateResponse;
import com.hbvibe.product.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source="parent.id",target="parentId")
    CategoryCreateResponse toCategoryCreateResponse(Category category);
}
