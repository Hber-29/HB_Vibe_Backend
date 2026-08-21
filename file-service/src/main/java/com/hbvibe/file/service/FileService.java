package com.hbvibe.file.service;


import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
     MinioClient minioClient;



    /**
     * 1. Hàm cấp 1 giấy phép (Presigned URL) cho 1 file lẻ
     */
    public FileUploadResponse generatePresignedUrl(String originalFileName,String module) {
        String targetBucket;
        switch (module.toLowerCase()) {
            case "brand":
                targetBucket = "hb-vibe-brands-logo";
                break;
            case "product":
                targetBucket = "hb-vibe-products";
                break;
            case "avatar":
                targetBucket = "hb-vibe-avatars";
                break;
            default:
                throw new RuntimeException("Module không hợp lệ! Chỉ hỗ trợ: brand, product, avatar");
        }
        try {
            // Lấy phần mở rộng của file (vd: .jpg, .png)
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // Tạo tên file độc nhất bằng UUID để tránh bị ghi đè trùng lặp
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            // ĐẢM BẢO BUCKET TỒN TẠI TRƯỚC KHI TẠO LINK
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(targetBucket).build());
            if (!found) {
                // Tự động tạo bucket nếu chưa có
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(targetBucket).build());
            }
            // Cấu hình giấy phép: Cho phép HTTP PUT, thời hạn 5 phút
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(targetBucket)
                    .object(uniqueFileName)
                    .expiry(5, TimeUnit.MINUTES)
                    .build();

            // Xin link từ MinIO
            String presignedUrl = minioClient.getPresignedObjectUrl(args);

            log.info("Đã cấp Presigned URL thành công cho file: {}", uniqueFileName);
            return new FileUploadResponse(uniqueFileName, presignedUrl);

        } catch (Exception e) {
            log.error("Lỗi khi tạo Presigned URL cho file: {}", originalFileName, e);
            throw new RuntimeException("Không thể cấp phép upload file lúc này");
        }
    }

    /**
     * 2. Hàm cấp hàng loạt giấy phép (Batch Presigned URLs) cho nhiều ảnh cùng lúc
     */
    public List<FileUploadResponse> generateMultiplePresignedUrls(List<String> originalFileNames,String module) {
        if (originalFileNames == null || originalFileNames.isEmpty()) {
            throw new IllegalArgumentException("Danh sách tên file không được để trống");
        }

        // Sử dụng Java Stream để duyệt qua danh sách tên file và tạo link tương ứng cho từng file
        return originalFileNames.stream()
                .map(fileName  -> generatePresignedUrl(fileName,module))
                .collect(Collectors.toList());
    }

    /**
     * DTO nội bộ chứa tên file mới và đường dẫn upload tương ứng
     */
    public record FileUploadResponse(String fileName, String uploadUrl) {}
}
