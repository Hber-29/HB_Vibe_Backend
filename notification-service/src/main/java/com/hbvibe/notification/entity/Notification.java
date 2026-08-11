package com.hbvibe.notification.entity;

import com.hbvibe.event.dto.Channel;
import com.hbvibe.notification.dto.request.email.Recepient;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    @Id
    private String id;
    String eventId;
    Long timestamp;
    Channel channel;
    List<Recepient> recipient;
    Integer templateCode;
    Map<String, Object> param;
    String subject;
    String body;
    List<String> attachments;
    // --- Các trường dùng để Tracking hệ thống ---
    Status status; // PENDING, SUCCESS, FAILED
    String errorMessage;
    boolean isRead; // Dùng cho web (Quả chuông)
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

