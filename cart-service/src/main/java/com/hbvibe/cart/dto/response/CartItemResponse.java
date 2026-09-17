package com.hbvibe.cart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long id;
    Long productVariantId;
    private Integer quantity;
    private BigDecimal price;
    private Boolean isSelected;
}
