package com.hbvibe.user.dto.request;

import com.hbvibe.user.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserProfileRequest {
    String email;
    String firstName;
    String lastName;
    @NotNull(message = "GENDER_IS_REQUIRED")
    Gender gender;
    @NotBlank(message = "PHONE_NUMBER_IS_REQUIRED")
    @NotNull(message = "BIRTHDATE_IS_REQUIRED")
    @Past(message = "INVALID_BIRTHDATE")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate birthDate;
    @DecimalMin(value = "50.0", message = "HEIGHT_MUST_BE_GREATER_THAN_50CM")
    @DecimalMax(value = "250.0", message = "HEIGHT_MUST_BE_LESS_THAN_250CM")
    @Digits(integer = 3, fraction = 2, message = "INVALID_HEIGHT_FORMAT")
    BigDecimal heightCm;
    @DecimalMin(value = "20.0", message = "WEIGHT_MUST_BE_GREATER_THAN_20KG")
    @DecimalMax(value = "200.0", message = "WEIGHT_MUST_BE_LESS_THAN_200KG")
    @Digits(integer = 3, fraction = 2, message = "INVALID_WEIGHT_FORMAT")
    BigDecimal weightKg;
    @Size(max = 50, message = "BODY_SHAPE_TOO_LONG")
    String bodyShape;
    @Size(max = 50, message = "SKIN_TONE_TOO_LONG")
    String skinTone;
    @Size(max = 10, message = "MAXIMUM_10_FAVORITE_STYLES_ALLOWED")
    List<String> favoriteStyles;
    @Size(max = 10, message = "MAXIMUM_10_PREFERRED_COLORS_ALLOWED")
    List<String> preferredColors;
}
