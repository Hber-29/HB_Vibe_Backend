package com.hbvibe.user.dto.request;

import com.hbvibe.user.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {
    @Size(min = 4 ,message = "INVALID_USERNAME")
    String username;
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;
    String email;
    String firstName;
    String lastName;
    @NotNull(message = "GENDER_IS_REQUIRED")
    Gender gender;
    @NotBlank(message = "PHONE_NUMBER_IS_REQUIRED")
    @Pattern(regexp = "^(0[35789])[0-9]{8}$", message = "INVALID_PHONE_NUMBER")
    String phoneNumber;
    @NotNull(message = "BIRTHDATE_IS_REQUIRED")
    @Past(message = "INVALID_BIRTHDATE")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate birthDate;
}
