package com.hbvibe.notification.entity.push;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_device_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDevice {
    @Id
    String userId;
    String username;
    @Builder.Default
    List<DeviceTokenInfo> fcmTokens = new ArrayList<>();
}
