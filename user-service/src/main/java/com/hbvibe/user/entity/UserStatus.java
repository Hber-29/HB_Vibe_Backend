package com.hbvibe.user.entity;

public enum UserStatus {
    PENDING,  // Chờ xác thực
    ACTIVE,   // Đang hoạt động
    BLOCKED,  // Bị khóa (Nghiệp vụ)
    DELETED   // Đã xóa (Xóa mềm)
}
