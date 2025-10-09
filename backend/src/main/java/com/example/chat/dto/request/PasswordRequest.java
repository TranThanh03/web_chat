package com.example.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordRequest {
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 8, max = 40, message = "PASSWORD_INVALID")
    String password;
}