package com.example.chat.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    @NotNull(message = "NOT_NULL")
    @Size(min = 5, max = 100, message = "FULLNAME_LENGTH_INVALID")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "FULLNAME_INVALID")
    String fullName;

    @NotNull(message = "NOT_NULL")
    String avatar;

    @NotNull(message = "NOT_NULL")
    @Pattern(regexp = "^(\\+84|0)(3|5|7|8|9)\\d{8}$", message = "PHONE_INVALID")
    String phone;

    @NotNull(message = "NOT_NULL")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotNull(message = "NOT_NULL")
    String password;
}