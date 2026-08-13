package com.hbvibe.notification.dto.request.push;

import com.hbvibe.notification.entity.push.DeviceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceTokenRequest {
     String fcmToken;
     DeviceType deviceType;
}
