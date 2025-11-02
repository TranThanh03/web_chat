package com.example.chat.dto.request.user;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoUpdateRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 10, max = 90, message = "CODE_LENGTH_INVALID")
    @Pattern(regexp = "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$", message = "CODE_INVALID")
    String code;

    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 1, max = 30, message = "FIRSTNAME_LENGTH_INVALID")
    @Pattern(regexp = "^[\\p{L}]+(\\s[\\p{L}]+)*$", message = "FIRSTNAME_INVALID")
    String firstName;

    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 1, max = 50, message = "LASTNAME_LENGTH_INVALID")
    @Pattern(regexp = "^[\\p{L}]+(\\s[\\p{L}]+)*$", message = "LASTNAME_INVALID")
    String lastName;

    @NotNull(message = "FIELD_NOT_NULL")
    @Past(message = "DATE_OF_BIRTH_INVALID")
    LocalDate dateOfBirth;

    @NotBlank(message = "FIELD_NOT_BLANK")
    String avatar;
}
