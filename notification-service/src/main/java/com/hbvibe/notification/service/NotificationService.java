package com.hbvibe.notification.service;

import com.hbvibe.event.dto.NotificationEvent;
import com.hbvibe.notification.dto.request.email.Recepient;
import com.hbvibe.notification.dto.request.push.PushNotificationRequest;
import com.hbvibe.notification.entity.Notification;
import com.hbvibe.notification.entity.Status;
import com.hbvibe.notification.repository.NotificationRepository;
import com.hbvibe.notification.service.channel.EmailChannelService;
import com.hbvibe.notification.service.channel.FirebasePushService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    NotificationRepository notificationRepository;
    EmailChannelService emailChannelService;
    FirebasePushService firebasePushService;

    public void processNotification(NotificationEvent event) {
        try {
            String channel = String.valueOf(event.getChannel()).toUpperCase();
            Notification notification = Notification.builder()
                    // Nhóm định danh
                    .eventId(event.getEventId())
                    .userId(event.getUserId())
                    .channel(event.getChannel())
                    .templateCode(event.getTemplateCode())
                    // Nhóm nội dung
                    .subject(event.getSubject())
                    .body(event.getBody())
                    .param(event.getParam())
                    // Xử lý danh sách người nhận (nếu có)
                    .recipient(event.getRecipient() != null && !event.getRecipient().isEmpty() ?
                            List.of(Recepient.builder()
                                    .email(event.getRecipient().get(0).getEmail())
                                    .name(event.getRecipient().get(0).getName())
                                    .build())
                            : null)

                    // Nhóm trạng thái (Mặc định khi mới tạo)
                    .status(Status.PENDING)
                    .isRead(false)
                    // Nhóm vết thời gian (Audit time)
                    .timestamp(event.getTimestamp())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Lưu lịch sử vào cơ sở dữ liệu
            notificationRepository.save(notification);
            log.info("Đã lưu thông báo vào DB thành công cho User: {} - EventID: {}", event.getUserId(), event.getEventId());

            // 2. BỘ ĐỊNH TUYẾN (ROUTER) CHUYỂN MẠCH GỬI ĐI
            switch (channel) {
                case "EMAIL":
                    emailChannelService.sendEmail(event);
                    log.info("Đã điều phối gửi EMAIL thành công. EventID: {}", event.getEventId());

                    // (Tuỳ chọn: Cập nhật lại status của notification thành SENT tại đây hoặc bên trong emailChannelService)
                    break;

                case "PUSH":
                    // Lấy FCM Token từ Map param (do các service khác truyền sang)
                    String targetToken = (event.getParam() != null && event.getParam().containsKey("fcmToken"))
                            ? event.getParam().get("fcmToken").toString()
                            : null;

                    if (targetToken == null || targetToken.trim().isEmpty()) {
                        log.warn("Bỏ qua gửi PUSH vì không tìm thấy fcmToken cho User: {}", event.getUserId());
                        break;
                    }

                    // Đóng gói DTO dành riêng cho Firebase
                    PushNotificationRequest pushReq = PushNotificationRequest.builder()
                            .targetToken(targetToken)
                            .title(event.getSubject())
                            .body(event.getBody())
                            .data(null) // Có thể truyền thêm deep link vào đây nếu cần
                            .build();

                    // Chuyển lệnh cho dịch vụ Firebase xử lý
                    firebasePushService.processPushNotification(pushReq);
                    log.info("Đã điều phối gửi PUSH thành công tới thiết bị. EventID: {}", event.getEventId());
                    break;

                default:
                    log.warn("Hệ thống chưa hỗ trợ xử lý kênh thông báo: {}", channel);
                    break;
            }

        } catch (Exception e) {
            log.error("Lỗi nghiêm trọng khi xử lý lưu hoặc gửi thông báo. EventID: {} - Chi tiết: {}",
                    event != null ? event.getEventId() : "UNKNOWN", e.getMessage(), e);
            // Ném lỗi ra để Kafka Listener biết là xử lý thất bại (có thể kích hoạt cơ chế retry của Kafka)
            throw new RuntimeException("Lỗi xử lý notification event", e);
        }
    }
}