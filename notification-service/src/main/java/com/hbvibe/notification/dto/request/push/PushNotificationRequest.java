package com.hbvibe.notification.dto.request.push;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushNotificationRequest {
    List<String> targetToken; // FCM Token của thiết bị
    String title;             // Tiêu đề thông báo
    String body;              // Nội dung chi tiết
    Map<String, String> data; // Dữ liệu ẩn (ví dụ: { "orderId": "123" } để click vào mở đúng đơn)
}
