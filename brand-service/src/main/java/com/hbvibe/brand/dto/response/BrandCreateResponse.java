package com.hbvibe.brand.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandCreateResponse {
    String brandId;
    String name;
    String description;
    String country;
    String logo;
}
