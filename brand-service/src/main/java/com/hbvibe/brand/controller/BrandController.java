package com.hbvibe.brand.controller;

import com.hbvibe.brand.dto.ApiResponse;
import com.hbvibe.brand.dto.request.AddMemberRequest;
import com.hbvibe.brand.dto.request.BrandCreateRequest;
import com.hbvibe.brand.dto.request.UpdateBrandRequest;
import com.hbvibe.brand.dto.response.BrandCreateResponse;
import com.hbvibe.brand.dto.response.UpdateBrandResponse;
import com.hbvibe.brand.service.BrandService;
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
}
