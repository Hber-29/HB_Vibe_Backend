package com.hbvibe.brand.dto.request;

import com.hbvibe.brand.entity.BrandStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBrandRequest {
    @NotBlank(message = "Tên Không được để trống ")
    String name;
    String logo;
    String description;
    String country;
    BrandStatus Status;
}
