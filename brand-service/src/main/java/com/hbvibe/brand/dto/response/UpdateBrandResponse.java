package com.hbvibe.brand.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBrandResponse {
    String id;
    String name;
    String slug;
    String logo;
    String description;
    String country;
    String status;
    LocalDateTime updatedAt;
}
