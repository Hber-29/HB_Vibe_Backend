package com.hbvibe.notification.mapper;

import com.hbvibe.notification.dto.response.DeviceTokenResponse;
import com.hbvibe.notification.entity.push.UserDevice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceTokenMapper {
    DeviceTokenResponse toDeviceTokenResponse(UserDevice userDevice);
}
