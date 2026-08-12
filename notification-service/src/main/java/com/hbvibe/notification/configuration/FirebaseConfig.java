package com.hbvibe.notification.configuration; // Cập nhật đúng tên thư mục của bạn

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Đọc file json chứa khóa bảo mật từ thư mục resources
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

        if (serviceAccount == null) {
            throw new RuntimeException("Không tìm thấy file firebase-service-account.json trong resources!");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        // Tránh lỗi khởi tạo lại Firebase nhiều lần nếu Spring Boot reload context
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}