package com.hbvibe.notification.controller;

import com.hbvibe.event.dto.NotificationEvent;
import com.hbvibe.notification.service.NotificationService;
import com.hbvibe.notification.service.channel.EmailChannelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @KafkaListener(topics = "notification-delivery"
            ,groupId = "notification-group-test")
    public void listen(NotificationEvent event){
       log.info("Message received: {}", event);

       notificationService.processNotification(event);


    }

}
