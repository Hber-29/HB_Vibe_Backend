package com.hbvibe.banner.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Cơ chế Xóa mềm: Khi gọi hàm delete(), Hibernate sẽ tự động chuyển thành câu lệnh UPDATE này
@SQLDelete(sql = "UPDATE banners SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// Cơ chế Lọc mặc định (Hibernate 6.3+): Luôn bỏ qua các bản ghi đã bị xóa mềm khi SELECT
@SQLRestriction("deleted_at IS NULL")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String title;
    @Column(nullable = false, length = 500)
    String image;
    @Column( length = 500)
    String link;
    @Column(name = "sort_order")
    Integer sortOrder;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    BannerStatus status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    BannerPosition position;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

}
