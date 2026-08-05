package com.hbvibe.user.mapper;

import com.hbvibe.user.dto.request.RegistrationRequest;
import com.hbvibe.user.dto.response.ProfileResponse;
import com.hbvibe.user.entity.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    UserProfile toUserProfile(RegistrationRequest registrationRequest);
    ProfileResponse toProfileResponse(UserProfile userProfile);
}
