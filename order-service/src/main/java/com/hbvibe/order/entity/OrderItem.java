package com.hbvibe.order.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //quan hệ n-1 về bảng order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Column(name = "product_id", nullable = false)
    Long productId;

    @Column(name = "product_variant_id", nullable = false)
    Long productVariantId;

    @Column(name = "sku_code", length = 100)
    String skuCode;

    //các trường sẽ lấy dữ liệu từ product-service
    @Column(name = "product_name", nullable = false)
    String productName;

    @Column(name = "product_image", length = 500)
    String productImage;

    @Column(name = "size", length = 50)
    String size;

    @Column(name = "color", length = 50)
    String color;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "price", nullable = false)
    BigDecimal price;

    @Column(name = "total_price", nullable = false)
    BigDecimal totalPrice;

    @Column(name = "is_reviewed")
    @Builder.Default
    Boolean isReviewed = false;
}
