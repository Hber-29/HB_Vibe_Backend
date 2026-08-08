package com.hbvibe.user.dto.response;

import com.hbvibe.user.entity.UserProfile;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressResponse {
    UUID id;
    String receiverName;
    String receiverPhone;
    String cityProvince;
    String district;
    String ward;
    String streetAddress;
    boolean isDefault;
}
