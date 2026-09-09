package com.hbvibe.user.controller;

import com.hbvibe.user.dto.ApiResponse;
import com.hbvibe.user.dto.request.RegistrationRequest;
import com.hbvibe.user.dto.request.UpdateUserProfileRequest;
import com.hbvibe.user.dto.request.UserAddressRequest;
import com.hbvibe.user.dto.response.ProfileResponse;
import com.hbvibe.user.dto.response.UpdateUserProfileResponse;
import com.hbvibe.user.dto.response.UserAddressResponse;
import com.hbvibe.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/api/v1")
public class ProfileController {
    ProfileService profileService;
    @PostMapping("/register")
    public ApiResponse<ProfileResponse> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.register(registrationRequest))
                .build();


    }
    // hàm lấy tất cả danh sách user(cho admin)
    @GetMapping
    public ApiResponse<List<ProfileResponse>> getAllUsers(){
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.getAllUsers())
                .build();

    }
    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyUserProfile(){
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getMyUserProfile())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UpdateUserProfileResponse> updateUserStyle(
            @RequestBody UpdateUserProfileRequest updateUserProfileRequest
            , @PathVariable UUID id){
        return ApiResponse.<UpdateUserProfileResponse>builder()
                .result(profileService.updateUserProfile(updateUserProfileRequest,id))
                .build();
    }

    @GetMapping("/{id}/styles")
    public ApiResponse<UpdateUserProfileResponse> getMyUserProfileStyle(@PathVariable UUID id){
        return ApiResponse.<UpdateUserProfileResponse>builder()
                .result(profileService.getMyUserProfileStyle(id))
                .build();
    }

    @GetMapping("/styles")
    public ApiResponse<List<UpdateUserProfileResponse>> getAllUserProfileStyle(){
        return ApiResponse.<List<UpdateUserProfileResponse>>builder()
                .result(profileService.getAllUserProfilesStyle())
                .build();

    }

    @DeleteMapping("/{id}")
    public void deleteUserProfile(@PathVariable UUID id){
        profileService.deleteUserProfile(id);
    }



}
