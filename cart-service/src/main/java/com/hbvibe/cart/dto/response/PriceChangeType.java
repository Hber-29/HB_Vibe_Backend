package com.hbvibe.cart.dto.response;

public enum PriceChangeType {
    NONE,       // Giá giữ nguyên
    DECREASED,  // Giảm giá (Rẻ hơn so với lúc thêm vào giỏ)
    INCREASED   // Tăng giá (Đắt hơn so với lúc thêm vào giỏ)
}
