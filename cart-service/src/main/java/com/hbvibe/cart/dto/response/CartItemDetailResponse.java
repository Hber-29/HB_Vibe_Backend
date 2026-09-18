package com.hbvibe.cart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemDetailResponse {
     Long id;                 // ID của chính bản ghi CartItem này trong DB
     Long productVariantId;   // Mã phân loại sản phẩm (Mã SKU/Biến thể)
     Integer quantity;        // Số lượng khách định mua
     Boolean isSelected;      // Trạng thái: Khách có đang tick chọn món này để thanh toán không?
     BigDecimal savedPrice;   // Giá gốc lúc khách bấm "Thêm vào giỏ" (Giá lưu ở DB)

    // --- 2. THÔNG TIN HIỂN THỊ (Được kéo từ Product Service sang) ---
     String image;            // Link ảnh đại diện của sản phẩm/biến thể
     String productName;      // Tên sản phẩm (VD: Áo Hoodie Nam Nữ Oversized)
     String size;             // Kích cỡ (VD: M, L, XL)
     String color;            // Màu sắc (VD: Be, Đen)

    // --- 3. THÔNG TIN THỜI GIAN THỰC (Đối chiếu động ngay lúc gọi API) ---
     BigDecimal currentPrice; // Giá thực tế của sản phẩm ngay tại thời điểm khách mở giỏ hàng
     PriceChangeType priceChangeType; // Enum báo hiệu cờ: TĂNG GIÁ, GIẢM GIÁ, hay GIỮ NGUYÊN
     Boolean isAvailable;     // Cờ báo hiệu món hàng này còn bán hay không (True = Còn bán, False = Ngừng bán/Ẩn)
     Integer currentStock;    // Số lượng tồn kho thực tế hiện tại (Để Frontend biết đường khóa nút "+" nếu khách mua lố)
}
