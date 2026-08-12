package com.hbvibe.notification.service.channel;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.hbvibe.notification.client.FirebaseClient;
import com.hbvibe.notification.dto.request.push.PushNotificationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FirebasePushService {

     FirebaseClient firebaseClient;

    public void processPushNotification(PushNotificationRequest request) {
        try {
            String messageId = firebaseClient.sendNotification(request);
            log.info("Gửi Push thành công tới token {}. Message ID: {}", request.getTargetToken(), messageId);
        } catch (FirebaseMessagingException e) {
            log.error("Gửi Push thất bại: {}", e.getMessage());
            // Ở đây bạn có thể throw AppException để trigger retry nếu cần
        }
    }
}