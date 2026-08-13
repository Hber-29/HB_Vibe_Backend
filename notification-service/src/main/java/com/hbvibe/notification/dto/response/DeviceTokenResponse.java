package com.hbvibe.notification.dto.response;

import com.hbvibe.notification.entity.push.DeviceTokenInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceTokenResponse {
    String userId;
    String username;
    List<DeviceTokenInfo> fcmTokens;
}
