package com.hbvibe.product.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Ghi đè lệnh DELETE mặc định thành lệnh UPDATE để làm Soft Delete
@SQLDelete(sql = "UPDATE products SET deleted_at = NOW() WHERE id = ?")
// Chỉ lấy ra những sản phẩm chưa bị xóa
@Where(clause = "deleted_at IS NULL")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "brand_id")
    private String brandId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String thumbnail;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Column(length = 20)
    private Status status; // ACTIVE / INACTIVE

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    /* --- CÁC TRƯỜNG BỔ SUNG CHO SEO --- */
    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    /* --- AUDIT & SOFT DELETE --- */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /* --- MỐI QUAN HỆ (RELATIONSHIPS) --- */
    // Một sản phẩm có nhiều ảnh. CascadeType.ALL giúp lưu/xóa sản phẩm thì lưu/xóa luôn ảnh
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductImage> images = new LinkedHashSet<>();

    // Một sản phẩm có nhiều biến thể
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductVariant> variants = new LinkedHashSet<>();

    // hai hàm ép bảng con nhận bảng cha
//    public void addVariant(ProductVariant variant) {
//        if (this.variants == null) {
//            this.variants = new ArrayList<>(); // Khởi tạo nếu bị null
//        }
//        this.variants.add(variant);
//        variant.setProduct(this);
//    }
//
//    public void addImage(ProductImage image) {
//        if (this.images == null) {
//            this.images = new ArrayList<>();
//        }
//        this.images.add(image);
//        image.setProduct(this);
//    }
}
