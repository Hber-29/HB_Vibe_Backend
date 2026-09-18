package com.hbvibe.cart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    // Danh sách chi tiết các món hàng trong giỏ (đã bao gồm ảnh, tên, và đối chiếu giá)
    private List<CartItemDetailResponse> items;
    // Tổng số tiền khách hàng cần thanh toán
    // (Chỉ cộng tiền những món isSelected = true VÀ isAvailable = true)
    private BigDecimal totalSelectedPrice;
}
