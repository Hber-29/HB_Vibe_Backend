package com.hbvibe.product.dto.category.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    String name;

    String description;

    String image;

    Integer sortOrder;

    Long parentId;

    String metaTitle;

    String metaDescription;
}
