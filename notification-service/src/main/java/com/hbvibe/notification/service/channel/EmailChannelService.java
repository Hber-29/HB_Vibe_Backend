package com.hbvibe.notification.service.channel;

import com.hbvibe.event.dto.NotificationEvent;
import com.hbvibe.notification.client.BrevoClient;
import com.hbvibe.notification.dto.request.email.EmailRequest;
import com.hbvibe.notification.dto.request.email.Recepient;
import com.hbvibe.notification.dto.request.email.Sender;
import com.hbvibe.notification.dto.response.EmailResponse;
import com.hbvibe.notification.exception.AppException;
import com.hbvibe.notification.exception.ErrorCode;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailChannelService {
     BrevoClient brevoClient;
     @Value("${spring.notification.email.brevo-apikey}")
     @NonFinal
     String apiKey;

    public EmailResponse sendEmail(NotificationEvent event) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("HberNguyen")
                        .email("hungbeu292005@gmail.com")
                        .build())
                .to(List.of(Recepient.builder()
                        .email(event.getRecipient().get(0).getEmail())
                        .name(event.getRecipient().get(0).getName())
                        .build()))
                .templateCode(event.getTemplateCode())
                .param(event.getParam())
                .subject(event.getSubject())
                .htmlContent(event.getBody())
                .build();
        try {
            return brevoClient.sendEmail(apiKey, emailRequest);
        }catch (FeignException e){
            log.error("Brevo error: status={}, body={}",
                    e.status(),
                    e.contentUTF8(),
                    e);
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }


    }
}
