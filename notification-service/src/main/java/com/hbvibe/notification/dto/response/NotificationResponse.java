package com.hbvibe.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hbvibe.event.dto.Channel;
import com.hbvibe.notification.entity.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {
    String eventId;
    Long timestamp;
    Channel channel;
    String recipient;
    Long templateCode;
    Map<String, Object> param;
    String subject;
    String body;
    Status status;
    String errorMessage;
    boolean isRead;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String messageId;
}
