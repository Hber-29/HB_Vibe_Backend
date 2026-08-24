package com.hbvibe.brand.dto.request;

import com.hbvibe.brand.entity.BrandRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoleRequest {
    BrandRole brandRole;
}
