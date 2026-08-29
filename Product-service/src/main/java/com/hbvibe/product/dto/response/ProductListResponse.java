package com.hbvibe.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductListResponse implements Serializable {
    // Thêm serialVersionUID là best practice của Java khi implement Serializable
    private static final long serialVersionUID = 1L;
    Long id;
    String slug;
    String name;
    String thumbnail;
    BigDecimal price;
    BigDecimal salePrice;
    Integer viewCount;
}
