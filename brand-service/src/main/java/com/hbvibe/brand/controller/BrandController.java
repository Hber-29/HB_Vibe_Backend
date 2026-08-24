package com.hbvibe.brand.controller;

import com.hbvibe.brand.dto.ApiResponse;
import com.hbvibe.brand.dto.request.AddMemberRequest;
import com.hbvibe.brand.dto.request.BrandCreateRequest;
import com.hbvibe.brand.dto.request.UpdateBrandRequest;
import com.hbvibe.brand.dto.request.UpdateRoleRequest;
import com.hbvibe.brand.dto.response.BrandCreateResponse;
import com.hbvibe.brand.dto.response.UpdateBrandResponse;
import com.hbvibe.brand.entity.BrandRole;
import com.hbvibe.brand.service.BrandService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)


public class BrandController {
    BrandService brandService;
    @PostMapping("/create_brand")
    public ApiResponse<BrandCreateResponse> createBrand(@RequestBody BrandCreateRequest brandCreateRequest) {
        return ApiResponse.<BrandCreateResponse>builder()
                .message("Tạo Brand thành công!")
                .result(brandService.createBrand(brandCreateRequest))
                .build();
    }
    @PostMapping("/add_member")
    public ResponseEntity<?> addMemberBrand(@RequestBody AddMemberRequest addMemberRequest) {
        brandService.addMemberBrand(addMemberRequest);
        return ResponseEntity.ok().build();
    }

    // update infprmation brand
    @PutMapping("/edit_brand/{brandId}")
    public ApiResponse<UpdateBrandResponse> updateBrand(
            @PathVariable String brandId,
            JwtAuthenticationToken jwt,
             @RequestBody UpdateBrandRequest request

    ){
        String userId = jwt.getName();
        return ApiResponse.<UpdateBrandResponse>builder()
                .message("Cập nhật thông tin brand thành công")
                .result(brandService.updateBrand(userId, brandId, request))
                .build();

    }

    // update role member
    @PutMapping("/{brandId}/members/{targetUserId}/role")
    public ResponseEntity<?> updateRoleMember(
            @PathVariable String brandId,
            @PathVariable String targetUserId,
            @Valid @RequestBody UpdateRoleRequest newRole,
            JwtAuthenticationToken jwt
            ){
        String requestUserId = jwt.getName();
        brandService.updateMemberRole(brandId,requestUserId,targetUserId,newRole);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{brandId}/delete/{targetUserId}/role")
    public ResponseEntity<?> deleteMemberBrand(
            @PathVariable String brandId,
            @PathVariable String targetUserId,
            JwtAuthenticationToken jwt
    ){
        String requestUserId = jwt.getName();
        brandService.deleteMemberBrand(brandId,requestUserId,targetUserId);
        return ResponseEntity.ok().build();
    }
}
