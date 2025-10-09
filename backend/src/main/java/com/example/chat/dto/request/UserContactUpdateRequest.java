package com.example.chat.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserContactUpdateRequest {
    @NotBlank(message = "NOT_BLANK")
    @Pattern(regexp = "^(\\+84|0)(3|5|7|8|9)\\d{8}$", message = "PHONE_INVALID")
    String phone;

    @NotBlank(message = "NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;
}