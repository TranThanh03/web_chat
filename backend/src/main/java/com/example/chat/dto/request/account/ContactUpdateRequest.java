package com.example.chat.dto.request.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactUpdateRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;
}