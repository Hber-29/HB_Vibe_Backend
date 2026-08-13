package com.hbvibe.notification.controller;


import com.hbvibe.notification.dto.ApiResponse;
import com.hbvibe.notification.dto.request.push.DeviceTokenRequest;
import com.hbvibe.notification.dto.response.DeviceTokenResponse;
import com.hbvibe.notification.service.DeviceTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class DeviceTokenController {
    DeviceTokenService deviceTokenService;

    @PostMapping("/device-token")
    public ApiResponse<DeviceTokenResponse> createDeviceToken(@RequestBody DeviceTokenRequest deviceTokenRequest){
        return ApiResponse.<DeviceTokenResponse>builder()
                .result(deviceTokenService
                        .createDeviceToken(deviceTokenRequest))
                .build();
    }

    @DeleteMapping("/delete-token/{fcmToken}")
    public ApiResponse<Void> deleteDeviceToken(@PathVariable  String fcmToken){
        deviceTokenService.deleteDeviceToken(fcmToken);
        return ApiResponse.<Void>builder()
                .message("Xóa token thành công")
                .build();
    }
}
