package com.hbvibe.brand.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddMemberRequest {
    @NotBlank(message = "Email không được để trống ")
    String staffEmail;
    String brandId;

}
