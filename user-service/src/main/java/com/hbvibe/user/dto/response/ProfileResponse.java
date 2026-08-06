package com.hbvibe.user.dto.response;

import com.hbvibe.user.entity.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    UUID id;
    String keycloakId;
    String email;
    String username;
    String firstName;
    String lastName;
    Gender gender;
    String phoneNumber;
    LocalDate birthDate;
}
