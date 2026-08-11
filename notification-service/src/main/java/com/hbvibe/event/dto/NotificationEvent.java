package com.hbvibe.event.dto;

import com.hbvibe.notification.dto.request.email.Recepient;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    // 1. Thông tin Metadata (Theo dõi hệ thống)
     String eventId;     // VD: "uuid-1234-..."
     Long timestamp;     // Thời gian bắn event

    // 2. Thông tin Định tuyến
     Channel channel;     // "EMAIL", "SMS", "ZALO", "PUSH"
     List<Recepient> recipient;   // "an@yopmail.com" hoặc "0987654321"

    // 3. Thông tin Nội dung linh hoạt (Ưu tiên dùng Template)
     Integer templateCode;         // VD: "ORDER_CONFIRMATION"
     Map<String, Object> param;   // VD: {"orderId": "HB123", "total": "500000"}

    // 4. Thông tin Nội dung tĩnh (Dùng làm fallback hoặc tin nhắn đơn giản)
     String subject;
     String body;

    // 5. Mở rộng cho E-commerce
     List<String> attachments; // Danh sách URL file đính kèm (hóa đơn PDF)
}

