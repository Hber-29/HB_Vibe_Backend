package com.hbvibe.notification.client;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.hbvibe.notification.dto.request.push.PushNotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class FirebaseClient {

    public String sendNotification(PushNotificationRequest request) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();

        Message.Builder messageBuilder = Message.builder()
                .setToken(request.getTargetToken())
                .setNotification(notification);

        if (request.getData() != null) {
            messageBuilder.putAllData(request.getData());
        }

        Message message = messageBuilder.build();
        return FirebaseMessaging.getInstance().send(message);
    }
}