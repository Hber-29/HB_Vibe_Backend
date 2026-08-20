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
public class ProductResponse {
    private Long id;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private String thumbnail;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String status;
    private Boolean isFeatured;
    private String metaTitle;
    private String metaDescription;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;    // Danh sách biến thể và ảnh
    private List<VariantResponse> variants;
    private List<ImageResponse> images;
    @Data
    @Builder
    public static class VariantResponse {
        private Long id;
        private String sku;
        private String size;
        private String color;
        private Integer stockQuantity;
        private BigDecimal price;
        private BigDecimal salePrice;
        private BigDecimal weight;
        private String status;
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
