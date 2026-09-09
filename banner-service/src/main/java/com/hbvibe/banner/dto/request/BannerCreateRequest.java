package com.hbvibe.banner.dto.request;


import com.hbvibe.banner.entity.BannerPosition;
import com.hbvibe.banner.entity.BannerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerCreateRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    String title;

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    String image;

    String link;

    Integer sortOrder = 1; // Mặc định là 1 nếu không truyền

    @NotNull(message = "Trạng thái không được để trống")
    BannerStatus status;

    @NotNull(message = "Vị trí không được để trống")
    BannerPosition position;

}
