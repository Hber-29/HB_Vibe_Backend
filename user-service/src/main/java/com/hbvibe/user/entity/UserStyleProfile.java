package com.hbvibe.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name="user_style-profile")
public class UserStyleProfile {
    // không dùng gểnate mà gán luôn id của user_profile vào
    @Id
    UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // dùng để bơm id của user_profile vào id
    @JoinColumn(name="user_id")
    UserProfile userProfile;
    @Column(name = "height_cm", precision = 5, scale = 2)
    BigDecimal heightCm;
    @Column(name = "weight_kg", precision = 5, scale = 2)
    BigDecimal weightKg;
    @Column(name = "body_shape", length = 50)
    String bodyShape;

    @Column(name = "skin_tone", length = 50)
    String skinTone;

    // Ánh xạ thành kiểu JSONB trong PostgreSQL
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "favorite_styles", columnDefinition = "jsonb")
    List<String> favoriteStyles;

    // Ánh xạ thành kiểu JSONB trong PostgreSQL
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_colors", columnDefinition = "jsonb")
    List<String> preferredColors;

}
