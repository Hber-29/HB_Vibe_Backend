package com.hbvibe.user.mapper;

import com.hbvibe.user.dto.response.UserAddressResponse;
import com.hbvibe.user.entity.UserAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {
    UserAddressResponse toUserAddressResponse(UserAddress userAddress);
}
