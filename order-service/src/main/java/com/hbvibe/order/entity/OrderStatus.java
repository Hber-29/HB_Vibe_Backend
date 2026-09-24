package com.hbvibe.order.entity;

public enum OrderStatus {
    PENDING,      // Chờ xử lý
    PROCESSING,   // Đang chuẩn bị hàng
    SHIPPING,     // Đang giao hàng
    DELIVERED,    // Đã giao đến nơi
    COMPLETED,    // Hoàn tất
    CANCELLED,    // Đã hủy
    RETURNED      // Trả hàng/Hoàn tiền
}
