package com.hbvibe.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product_variants SET deleted_at = NOW() WHERE id = ?") // dùng để xóa mềm
@Where(clause = "deleted_at IS NULL")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1: Nhiều biến thể thuộc về 1 sản phẩm
    // LAZY fetch giúp tối ưu hiệu năng, không tự động kéo data Product nếu không gọi tới
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Column(length = 100)
    private String sku;

    @Column(length = 50)
    private String size;

    @Column(length = 50)
    private String color;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Column(length = 500)
    private String image;

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(length = 20)
    private Status status; // ACTIVE / INACTIVE

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
