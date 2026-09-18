package com.hbvibe.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariantForCartResponse {
    // ID của biến thể sản phẩm (SKU)
    private Long variantId;

    // Mức giá cuối cùng sẽ áp dụng (Service bên Product đã tự check ưu tiên lấy giá salePrice nếu có)
    private BigDecimal price;

    // Số lượng tồn kho thực tế hiện tại
    private Integer stockQuantity;

    // Trạng thái kinh doanh: true (ACTIVE), false (INACTIVE hoặc hết hàng)
    private Boolean isActive;

    private String image;
    private String productName;
    private String size;
    private String color;
}
