package com.hbvibe.file.controller;


import com.hbvibe.file.dto.ApiResponse;
import com.hbvibe.file.service.FileService;
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
public class FileController {
    FileService fileService;

    // API lấy 1 link lẻ (Dùng khi cần up nhanh 1 ảnh đại diện, avatar...)
    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<FileService.FileUploadResponse>> getPresignedUrl(
            @RequestParam String fileName
            ,@RequestParam(defaultValue = "product") String module) {

        FileService.FileUploadResponse response = fileService.generatePresignedUrl(fileName,module);

        return ResponseEntity.ok(ApiResponse.<FileService.FileUploadResponse>builder()
                .code(1000)
                .message("Cấp giấy phép thành công")
                .result(response)
                .build());
    }

    // API lấy nhiều link cùng lúc (Dùng khi đăng sản phẩm có 5-10 ảnh)
    @PostMapping("/presigned-urls/batch")
    public ResponseEntity<ApiResponse<List<FileService.FileUploadResponse>>> getMultiplePresignedUrls(
            @RequestBody List<String> fileNames
            ,@RequestParam(defaultValue = "product") String module) {

        List<FileService.FileUploadResponse> responseList = fileService.generateMultiplePresignedUrls(fileNames, module);

        return ResponseEntity.ok(ApiResponse.<List<FileService.FileUploadResponse>>builder()
                .code(1000)
                .message("Cấp hàng loạt giấy phép upload thành công")
                .result(responseList)
                .build());
    }
}
