package com.hbvibe.user.dto.response;

import com.hbvibe.user.entity.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserProfileResponse {
    UUID id;
    String email;
    String firstName;
    String lastName;
    Gender gender;
    LocalDate birthDate;
    BigDecimal heightCm;
    BigDecimal weightKg;
    String bodyShape;
    String skinTone;
    List<String> favoriteStyles;
    List<String> preferredColors;
}
