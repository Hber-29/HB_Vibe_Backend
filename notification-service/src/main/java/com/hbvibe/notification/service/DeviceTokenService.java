package com.hbvibe.notification.service;


import com.hbvibe.notification.dto.request.push.DeviceTokenRequest;
import com.hbvibe.notification.dto.response.DeviceTokenResponse;
import com.hbvibe.notification.entity.push.DeviceTokenInfo;
import com.hbvibe.notification.entity.push.UserDevice;
import com.hbvibe.notification.exception.AppException;
import com.hbvibe.notification.exception.ErrorCode;
import com.hbvibe.notification.mapper.DeviceTokenMapper;
import com.hbvibe.notification.repository.DeviceTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceTokenService {
    DeviceTokenRepository deviceTokenRepository;
    DeviceTokenMapper deviceTokenMapper;
    public DeviceTokenResponse createDeviceToken(DeviceTokenRequest deviceTokenRequest) {
        log.info("DeviceTokenService: {}" , deviceTokenRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId= authentication.getName();
        String username= null;
        if(authentication instanceof JwtAuthenticationToken jwtAuthToken){
            Map<String,Object> tokenattributes = jwtAuthToken.getTokenAttributes();
            username = tokenattributes.get("preferred_username").toString();
        }
        try {
            var userDevice = deviceTokenRepository.findByUserId(userId).orElse(
                UserDevice.builder()
                    .userId(userId)
                    .username(username)
                        .fcmTokens(new ArrayList<>())
                    .build());
            // nếu lặp lại token nó sẽ trả fasle
            boolean tokenExists = userDevice.getFcmTokens().stream()
                    .anyMatch(t -> Objects.equals(t.getToken(),deviceTokenRequest.getFcmToken()));
            if(!tokenExists){
                userDevice.getFcmTokens().add(
                        DeviceTokenInfo.builder()
                                .token(deviceTokenRequest.getFcmToken())
                                .deviceType(deviceTokenRequest.getDeviceType())
                                .createdAt(Instant.now())
                                .build()
                );
            }
            deviceTokenRepository.save(userDevice);
            return deviceTokenMapper.toDeviceTokenResponse(userDevice);
        }catch (Exception e){
            log.error("Lỗi khi lưu Device Token: ", e);
            throw new AppException(ErrorCode.INVALID_KEY);
        }
    }

    public void deleteDeviceToken(String fcmToken) {
        try {
            deviceTokenRepository.deleteFcmToken(fcmToken);
        }catch (Exception e){
            throw new AppException(ErrorCode.CANNOT_DELETE_FCMTOKEN);
        }
    }
}
