package com.hbvibe.product.dto.request;

import com.hbvibe.product.entity.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequest {
    Long categoryId;
    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name;
    String shortDescription;
    String description;
    String thumbnail;
    @Min(value = 0, message = "Giá không được âm")
    BigDecimal price;
    @Min(value = 0, message = "Giá khuyến mãi không được âm")
    BigDecimal salePrice;
    Status status;
    Boolean isFeatured;
    String metaTitle;
    String metaDescription;


    List<VariantDto> variants;
    List<ImageDto> images;

    @Data
    public static class VariantDto {
        Long id;
        String size;
        String color;
        String sku;
        @Min(value = 0, message = "Số lượng tồn kho không hợp lệ")
        Integer stockQuantity;
        BigDecimal price;
        Status status;
        BigDecimal salePrice;
        BigDecimal weight;
        String image;
    }

    @Data
    public static class ImageDto {
        Long id;
        String imageUrl;
        Integer sortOrder;
        String altText;
    }
}
