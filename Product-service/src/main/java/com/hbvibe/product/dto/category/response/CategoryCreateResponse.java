package com.hbvibe.product.dto.category.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateResponse {
    Long id;
    String name;
    String slug;
    Integer level;
    Long parentId;
    Integer sortOrder;
}
