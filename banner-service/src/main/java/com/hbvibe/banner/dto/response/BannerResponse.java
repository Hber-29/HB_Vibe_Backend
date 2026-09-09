package com.hbvibe.banner.dto.response;


import com.hbvibe.banner.entity.BannerPosition;
import com.hbvibe.banner.entity.BannerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerResponse {
     Long id;
     String title;
     String image;
     String link;
     Integer sortOrder;
     BannerStatus status;
     BannerPosition position;
     LocalDateTime createdAt;
     LocalDateTime updatedAt;
}
