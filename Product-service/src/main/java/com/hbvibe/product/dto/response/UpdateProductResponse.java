package com.hbvibe.product.dto.response;

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
public class UpdateProductResponse {
    Long id;
    Long categoryId;
    String name;
    String slug;
    String shortDescription;
    String description;
    String thumbnail;
    BigDecimal price;
    BigDecimal salePrice;
    String status;
    Boolean isFeatured;
    String metaTitle;
    String metaDescription;
    Integer viewCount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<ProductResponse.VariantResponse> variants;
    List<ProductResponse.ImageResponse> images;
    @Data
    @Builder
    public static class VariantResponse {
        Long id;
        String sku;
        String size;
        String color;
        Integer stockQuantity;
        BigDecimal price;
        BigDecimal salePrice;
        BigDecimal weight;
        String status;
    }
    @Data
    @Builder
    public static class ImageResponse {
        private Long id;
        private String imageUrl;
        private Integer sortOrder;
        private String altText;
    }
}
