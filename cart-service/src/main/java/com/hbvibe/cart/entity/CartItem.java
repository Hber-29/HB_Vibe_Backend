package com.hbvibe.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    Cart cart;

    // Chỉ lưu ID của biến thể, giá trị thực tế gọi qua Product-Service
    @Column(name = "product_variant_id", nullable = false)
    Long productVariantId;

    @Column(nullable = false)
    @Builder.Default
    Integer quantity = 1;
    // Giá lưu cứng tại thời điểm bấm "Thêm vào giỏ"
    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal price;

    // Trạng thái tick/bỏ tick ở Frontend
    @Column(name = "is_selected", nullable = false)
    @Builder.Default
    Boolean isSelected = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
