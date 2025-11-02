package com.example.chat.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    String username;

    @NotBlank(message = "FIELD_NOT_BLANK")
    String password;

    @NotBlank(message = "FIELD_NOT_BLANK")
    String recaptcha;
}