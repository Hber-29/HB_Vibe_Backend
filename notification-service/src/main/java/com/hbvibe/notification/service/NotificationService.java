package com.hbvibe.notification.service;

import com.hbvibe.event.dto.NotificationEvent;
import com.hbvibe.notification.dto.request.Recepient;
import com.hbvibe.notification.entity.Notification;
import com.hbvibe.notification.entity.Status;
import com.hbvibe.notification.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;


    public ResponseEntity<?> notificcation(NotificationEvent event){
        try {


            Notification notification = Notification.builder()
                    .templateCode(event.getTemplateCode())
                    .param(event.getParam())
                    .timestamp(event.getTimestamp())
                    .eventId(event.getEventId())
                    .recipient(List.of(Recepient.builder()
                            .email(event.getRecipient().get(0).getEmail())
                            .name(event.getRecipient().get(0).getName())
                            .build()))
                    .subject(event.getSubject())
                    .body(event.getBody())
                    .status(Status.PENDING)
                    .isRead(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().build();

    }
}
