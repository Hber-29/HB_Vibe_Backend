package com.hbvibe.notification.service.channel;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.SendResponse;
import com.hbvibe.notification.client.FirebaseClient;
import com.hbvibe.notification.dto.request.push.PushNotificationRequest;
import com.hbvibe.notification.repository.DeviceTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FirebasePushService {

    FirebaseClient firebaseClient;
    // Tạm comment dòng này lại, sau này bạn inject Repository hoặc DeviceTokenService vào để gọi hàm xóa
     DeviceTokenRepository deviceTokenRepository;

    public void processPushNotification(PushNotificationRequest request) {
        try {

            BatchResponse batchResponse = firebaseClient.sendNotification(request);
            log.info("Batch Response: {}", batchResponse);

            List<SendResponse> responses = batchResponse.getResponses();
            List<String> failedTokens = new ArrayList<>();
            List<String> sentTokens = request.getTargetToken();

            for (int i = 0; i < responses.size(); i++) {
                SendResponse response = responses.get(i);

                if (!response.isSuccessful()) {
                    String errorCode = response.getException().getMessagingErrorCode().name();
                    log.error("Gửi thất bại tới token [{}]. Lỗi: {}", sentTokens.get(i), errorCode);

                    // Nếu lỗi là do Token bị chết (UNREGISTERED)
                    if ("UNREGISTERED".equals(errorCode) || "INVALID_ARGUMENT".equals(errorCode)) {
                        failedTokens.add(sentTokens.get(i));
                    }
                }
            }

            if (!failedTokens.isEmpty()) {
                log.warn("Phát hiện {} token đã chết. Bắt đầu dọn dẹp Database...", failedTokens.size());
                // xóa các fcmtoken lỗi
                for (String token : failedTokens) {
                    deviceTokenRepository.deleteFcmToken(token);
                }
            }

            log.info("Hoàn tất Push Notification. Thành công: {}/{}", batchResponse.getSuccessCount(), sentTokens.size());

        } catch (FirebaseMessagingException e) {
            log.error("Lỗi nghiêm trọng khi gọi Firebase SDK: {}", e.getMessage());
        }
    }
}