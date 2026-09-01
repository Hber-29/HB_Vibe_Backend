package com.hbvibe.product.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE categories SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 255)
    String name;

    @Column(nullable = false, unique = true, length = 255)
    String slug;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(length = 500)
    String image;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    Status status; // ACTIVE / INACTIVE

    @Column(name = "sort_order")
    @Builder.Default
    Integer sortOrder = 0; // Số càng nhỏ càng xếp lên đầu menu

    @Column(name = "is_featured")
    @Builder.Default
    Boolean isFeatured = false;

    @Column(name = "level")
    @Builder.Default
    Integer level = 0; // 0: Gốc, 1: Con cấp 1, 2: Con cấp 2...

    /* --- CÁC TRƯỜNG BỔ SUNG CHO SEO --- */
    @Column(name = "meta_title", length = 255)
    String metaTitle;

    @Column(name = "meta_description", length = 500)
    String metaDescription;

    /* --- AUDIT & SOFT DELETE --- */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    /* --- MỐI QUAN HỆ (RELATIONSHIPS) --- */

    // 1. TỰ THAM CHIẾU: Trỏ lên Danh mục cha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    Category parent;

    // 2. TỰ THAM CHIẾU: Chứa danh sách Danh mục con
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, name ASC")  // để cho hibernate sắp sắp các danh mục con theo thứ tự ưu tiên.
    @Builder.Default
    Set<Category> children = new LinkedHashSet<>();

    // 3. QUAN HỆ NHIỀU-NHIỀU VỚI SẢN PHẨM (Ánh xạ ngược từ bảng Product)
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @Builder.Default
    Set<Product> products = new LinkedHashSet<>();

}
