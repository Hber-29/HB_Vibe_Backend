package com.hbvibe.product.dto.request;

import com.hbvibe.product.entity.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    @NotNull(message = "Danh mục không được để trống ")
    Long categoryId;
    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name;
    String shortDescription;
    String description;
    String thumbnail;  // ảnh đại diện chính

    @Min(value = 0, message = "Giá gốc không được âm")
    BigDecimal price;
    BigDecimal salePrice; // Giá khuyến mãi chung (nếu có)
    Status status;
    Boolean isFeatured;
    // SEO
    String metaTitle;
    String metaDescription;
    // Danh sách biến thể và ảnh
    List<VariantDto> variants;
    List<ImageDto> images;


    @Data
    public static class VariantDto {
        String size;
        String color;
        String sku;
        @Min(value = 0, message = "Số lượng không được âm")
        Integer stockQuantity;
        BigDecimal price;
        Status status;
        BigDecimal salePrice; // Thêm giá sale cho riêng biến thể
        BigDecimal weight;    // Thêm khối lượng để tính ship
    }
    @Data
    public static class ImageDto {
        String imageUrl;
        Integer sortOrder;
        String altText; // Bổ sung altText cho ảnh
    }
}
