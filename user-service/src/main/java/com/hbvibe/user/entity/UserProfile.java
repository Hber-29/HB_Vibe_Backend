package com.hbvibe.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name="user_profile")


public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(name = "keycloak_id", length = 36, unique = true, nullable = false)
    String keycloakId;
    @Column(unique = true, nullable = false,length = 50)
    String username;
    @Column(unique = true, nullable = false,length = 100)
    String email;
    @Column(name="first_name", nullable = false,length = 50)
    String firstName;
    @Column(name="last_name", nullable = false,length = 50)
    String lastName;
    @Column(name="phone_number", nullable = false,unique = true,length = 20)
    String phoneNumber;
    // ép kiểu từ String 256vkys tự thành TEXT HƠN 65 NGHÌN KÝ TỰ.
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    String avatarUrl;
    // @Enumrated dùng để lưu đúng cái String chứ không lưu theoo dạng index
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    Gender gender;
    @Column(name = "date_of_birth")
    LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    UserStatus userStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    // 1 user có nhiều địa chỉ
    @OneToMany(mappedBy = "userProfile",cascade = CascadeType.ALL,orphanRemoval = true)
    List<UserAddress> addresses;

    // 1 user cố 1 hồ sơ sở thích phong cách
    @OneToOne(mappedBy = "userProfile",cascade = CascadeType.ALL, orphanRemoval = true)
    // dùng để share khóa chính (không cần dùng đến khóa ngoại)
    @PrimaryKeyJoinColumn
    UserStyleProfile userStyleProfile;


}
