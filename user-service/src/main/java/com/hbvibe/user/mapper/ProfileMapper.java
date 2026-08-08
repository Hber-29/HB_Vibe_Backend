package com.hbvibe.user.mapper;

import com.hbvibe.user.dto.request.RegistrationRequest;
import com.hbvibe.user.dto.response.ProfileResponse;
import com.hbvibe.user.dto.response.UpdateUserProfileResponse;
import com.hbvibe.user.entity.UserProfile;
import com.hbvibe.user.entity.UserStyleProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    UserProfile toUserProfile(RegistrationRequest registrationRequest);
    ProfileResponse toProfileResponse(UserProfile userProfile);
    @Mapping(target = "id", expression = "java(userProfile.getId())")
    UpdateUserProfileResponse toProfileStyleResponse(UserProfile userProfile
            , UserStyleProfile userStyleProfile);
}
