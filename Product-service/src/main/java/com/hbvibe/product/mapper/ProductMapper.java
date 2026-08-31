package com.hbvibe.product.mapper;



import com.hbvibe.product.dto.response.ProductResponse;
import com.hbvibe.product.dto.response.UpdateProductResponse;
import com.hbvibe.product.entity.Product;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);
    UpdateProductResponse toUpdateProductResponse(Product product);

}
