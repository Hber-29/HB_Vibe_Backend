package com.hbvibe.notification.client;

import com.google.firebase.messaging.*;
import com.hbvibe.notification.dto.request.push.PushNotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class FirebaseClient {

    public BatchResponse sendNotification(PushNotificationRequest request)
            throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();

        MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                .addAllTokens(request.getTargetToken())
                .setNotification(notification);

        if (request.getData() != null) {
            messageBuilder.putAllData(request.getData());
        }

        MulticastMessage message = messageBuilder.build();
        return FirebaseMessaging.getInstance().sendEachForMulticast(message);
    }
}