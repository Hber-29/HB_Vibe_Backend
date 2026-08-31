package com.hbvibe.product.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product_images SET deleted_at = NOW() WHERE id = ?") // dùng để xóa mềm
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl; // Sẽ chứa link ảnh từ MinIO

    @Column(name = "sort_order")
    private Integer sortOrder; // Để sắp xếp ảnh nào hiện trước, ảnh nào hiện sau

    @Column(name = "alt_text", length = 255)
    private String altText; // Bổ sung cho SEO

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
