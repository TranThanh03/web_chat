package com.example.chat.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 5, max = 100, message = "FULLNAME_LENGTH_INVALID")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "FULLNAME_INVALID")
    String fullName;

    @NotBlank(message = "NOT_BLANK")
    String avatar;

    @NotBlank(message = "NOT_BLANK")
    @Pattern(regexp = "^(\\+84|0)(3|5|7|8|9)\\d{8}$", message = "PHONE_INVALID")
    String phone;

    @NotBlank(message = "NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
}