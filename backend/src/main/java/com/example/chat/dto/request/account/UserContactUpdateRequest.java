package com.example.chat.dto.request.account;

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
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;
}