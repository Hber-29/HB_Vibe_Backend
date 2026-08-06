package com.hbvibe.user.controller;

import com.hbvibe.user.dto.ApiResponse;
import com.hbvibe.user.dto.request.RegistrationRequest;
import com.hbvibe.user.dto.response.ProfileResponse;
import com.hbvibe.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProfileController {
    ProfileService profileService;
    @PostMapping("/register")
    public ApiResponse<ProfileResponse> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.register(registrationRequest))
                .build();


    }
    @GetMapping("/allUsers")
    public ApiResponse<List<ProfileResponse>> getAllUsers(){
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.getAllUsers())
                .build();

    }
    @GetMapping("/myUser")
    public ApiResponse<ProfileResponse> getMyUserProfile(){
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getMyUserProfile())
                .build();
    }
}
