package com.example.chat.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordChangeRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 8, max = 32, message = "PASSWORD_INVALID")
    String currentPassword;

    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 8, max = 32, message = "PASSWORD_INVALID")
    String newPassword;
}