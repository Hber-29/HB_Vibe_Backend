package com.hbvibe.product.mapper;



import com.hbvibe.product.dto.response.ProductResponse;
import com.hbvibe.product.dto.response.UpdateProductResponse;
import com.hbvibe.product.entity.Category;
import com.hbvibe.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;


@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "categories", target = "categoryId", qualifiedByName = "mapSetToCategoryId")
    ProductResponse toProductResponse(Product product);
    UpdateProductResponse toUpdateProductResponse(Product product);
    //  Viết hàm hướng dẫn MapStruct cách lấy ID từ Set<Category>
    @Named("mapSetToCategoryId")
    default Long mapSetToCategoryId(Set<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        // Vì hiện tại 1 sản phẩm chỉ có 1 category, ta lấy ID của phần tử đầu tiên trong Set
        return categories.iterator().next().getId();
    }
}
