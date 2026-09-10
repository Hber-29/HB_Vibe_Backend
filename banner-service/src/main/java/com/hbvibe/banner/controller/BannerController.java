package com.hbvibe.banner.controller;

import com.hbvibe.banner.dto.ApiResponse;
import com.hbvibe.banner.dto.request.BannerCreateRequest;
import com.hbvibe.banner.dto.request.BannerUpdateResquest;
import com.hbvibe.banner.dto.response.BannerResponse;
import com.hbvibe.banner.entity.BannerPosition;
import com.hbvibe.banner.service.BannerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/api/v1")
public class BannerController {
    BannerService bannerService;

    @PostMapping
    public ApiResponse<BannerResponse> createBanner (@RequestBody BannerCreateRequest request){
        return ApiResponse.<BannerResponse>builder()
                .message("Tạo banner thành công !")
                .result(bannerService.createBanner(request))
                .build();

    }
    @PutMapping("/{id}")
    public ApiResponse<BannerResponse> updateBanner(
            @PathVariable Long id,
            @RequestBody BannerUpdateResquest request){
        return ApiResponse.<BannerResponse>builder()
                .message("Cập nhật banner thành công !")
                .result(bannerService.updateBanner(request,id))
                .build();
    }
    // lấy banner theo id (dngf để lấy banner theo id để admin có thể chỉnh sửa )
    @GetMapping("/{id}")
    public ApiResponse<BannerResponse> getBannerById(@PathVariable Long id){
        return ApiResponse.<BannerResponse>builder()
                .result(bannerService.getBannerById(id))
                .build();
    }
    // lấy banner theo số lương
    @GetMapping
    public ApiResponse<List<BannerResponse>> getPublicBanners(
            @RequestParam BannerPosition position,
            @RequestParam(defaultValue = "3") Integer limit
    ){
        return ApiResponse.<List<BannerResponse>>builder()
                .message("Lấy banner thành công !")
                .result(bannerService.getPublicBanners(position,limit))
                .build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBanner(@PathVariable Long id){
        bannerService.deleteBanner(id);
        return ResponseEntity.ok().build();
    }
}
