package com.hbvibe.brand.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "brand_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    Brand brand;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(nullable = false, length = 50)
    BrandRole role;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    LocalDateTime joinedAt;
}
