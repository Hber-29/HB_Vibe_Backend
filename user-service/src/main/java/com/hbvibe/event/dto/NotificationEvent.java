package com.hbvibe.event.dto;

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
     String eventId;
     Long timestamp;
     Channel channel;
     List <Recepient>recipient;
     Integer templateCode;
     Map<String, Object> param;
     String subject;
     String body;
     List<String> attachments;
}

