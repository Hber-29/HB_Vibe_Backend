package com.hbvibe.product.dto.category.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryTreeResponse {
    Long id;
    String name;
    String slug;
    Integer level;
    Integer sortOrder;
    List<CategoryTreeResponse> children;

}
