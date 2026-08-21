package com.hbvibe.brand.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id ;
    @Column(nullable = false, unique = true, length = 255)
    String name;

    @Column(nullable = false, unique = true, length = 255)
    String slug; // Dùng làm URL thân thiện (vd: /brands/nike)

    @Column(length = 500)
    String logo; // Chứa link ảnh trả về từ MinIO

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(length = 100)
    String country;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    BrandStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt; // xóa mềm
}
