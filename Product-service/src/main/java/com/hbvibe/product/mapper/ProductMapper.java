package com.hbvibe.product.mapper;


import com.hbvibe.product.dto.response.ProductListResponse;
import com.hbvibe.product.dto.response.ProductResponse;
import com.hbvibe.product.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);

}
