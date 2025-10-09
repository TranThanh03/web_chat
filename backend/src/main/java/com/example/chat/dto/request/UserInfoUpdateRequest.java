package com.example.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoUpdateRequest {
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 5, max = 40, message = "FULLNAME_LENGTH_INVALID")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "FULLNAME_INVALID")
    String fullName;

    @NotBlank(message = "NOT_BLANK")
    String avatar;
}