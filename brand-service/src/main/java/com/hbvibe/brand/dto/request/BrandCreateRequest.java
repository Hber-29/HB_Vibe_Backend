package com.hbvibe.brand.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandCreateRequest {
    String name;
    String description;
    String country;
    String logo;
}
