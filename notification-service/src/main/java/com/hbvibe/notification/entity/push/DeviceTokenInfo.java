package com.hbvibe.notification.entity.push;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceTokenInfo {
    String token;
    DeviceType deviceType;
    Instant createdAt;
}
