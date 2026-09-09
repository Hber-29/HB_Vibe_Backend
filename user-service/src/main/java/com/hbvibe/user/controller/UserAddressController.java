package com.hbvibe.user.controller;

import com.hbvibe.user.dto.ApiResponse;
import com.hbvibe.user.dto.request.UserAddressRequest;
import com.hbvibe.user.dto.response.UserAddressResponse;
import com.hbvibe.user.service.UserAddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/api/v1")
public class UserAddressController {
    UserAddressService userAddressService;

    @PostMapping("/addresses/me")
    public ApiResponse<UserAddressResponse> createUserAddress(@RequestBody UserAddressRequest userAddressRequest) {
        return ApiResponse.<UserAddressResponse>builder()
                .result(userAddressService.createUserAddress(userAddressRequest))
                .build();
    }

    @PutMapping("/addresses/{id}")
    public ApiResponse<UserAddressResponse> updateUserAddress(@RequestBody UserAddressRequest userAddressRequest,@PathVariable UUID id) {
        return ApiResponse.<UserAddressResponse>builder()
                .result(userAddressService.updateUserAddress(userAddressRequest,id))
                .build();
    }

    @DeleteMapping("/addresses/{id}")
    public void deleteUserAddress(@PathVariable UUID id) {
         userAddressService.deleteUserAddress(id);
    }

    @GetMapping("/addresses/me")
    public ApiResponse<List<UserAddressResponse>> getAllUserAddresses(){
        return ApiResponse.<List<UserAddressResponse>>builder()
                .result(userAddressService.getAllUserAddresses())
                .build();
    }

}
