package com.hbvibe.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name="user_address")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    UserProfile userProfile;
    @Column(name = "receiver_name", nullable = false, length = 100)
    String receiverName;
    @Column(name = "receiver_phone", nullable = false, length = 20)
    String receiverPhone;
    @Column(name = "city_province", nullable = false, length = 100)
    String cityProvince;
    @Column(nullable = false, length = 100)
    String district;
    @Column(nullable = false, length = 100)
    String ward;
    @Column(name = "street_address", nullable = false, columnDefinition = "TEXT")
    String streetAddress;
    @Column(name = "is_default", nullable = false)
    boolean isDefault = false;
}
