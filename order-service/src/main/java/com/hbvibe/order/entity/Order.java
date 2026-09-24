package com.hbvibe.order.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    String userId; // Lấy từ Keycloak

    @Column(name = "order_code", unique = true, nullable = false, length = 100)
    String orderCode;

    //các trường thông tin giao hÀng
    @Column(name = "receiver_name", nullable = false)
    String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    String receiverPhone;

    @Column(name = "receiver_address", nullable = false, columnDefinition = "TEXT")
    String receiverAddress;

    @Column(name = "province_code", length = 50)
    String provinceCode;

    @Column(name = "district_code", length = 50)
    String districtCode;

    @Column(name = "ward_code", length = 50)
    String wardCode;

    //các trường liên quan đến tài chính tiền ,..
    @Column(name = "total_amount", nullable = false)
    BigDecimal totalAmount;

    @Column(name = "shipping_fee")
    @Builder.Default
    BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "discount_amount")
    @Builder.Default
    BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false)
    BigDecimal finalAmount;

    // các trường sử dụng ENUM trạng thái
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    OrderStatus orderStatus;

    //các trường bổ xung (làm sau)
    @Column(name = "shipping_provider", length = 100)
    String shippingProvider;

    @Column(name = "tracking_number", length = 100)
    String trackingNumber;

    @Column(name = "voucher_code", length = 100)
    String voucherCode;

    @Column(name = "note", columnDefinition = "TEXT")
    String note;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    String cancelReason;

    // các trường mốc thời gian
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "paid_at")
    LocalDateTime paidAt;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    LocalDateTime cancelledAt;

    //Quan hệ 1-n với bảng OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<OrderItem> orderItems = new ArrayList<>();
}
