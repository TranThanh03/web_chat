package com.example.chat.dto.request.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalAccountRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 1, max = 30, message = "FIRSTNAME_LENGTH_INVALID")
    @Pattern(regexp = "^[\\p{L}]+(\\s[\\p{L}]+)*$", message = "FIRSTNAME_INVALID")
    String firstName;

    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 1, max = 50, message = "LASTNAME_LENGTH_INVALID")
    @Pattern(regexp = "^[\\p{L}]+(\\s[\\p{L}]+)*$", message = "LASTNAME_INVALID")
    String lastName;

    @NotNull(message = "FIELD_NOT_NULL")
    @PastOrPresent(message = "DATE_OF_BIRTH_INVALID")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;

    @NotBlank(message = "FIELD_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 8, max = 32, message = "PASSWORD_LENGTH_INVALID")
    String password;

    @NotBlank(message = "FIELD_NOT_BLANK")
    String recaptcha;
}
