package com.example.chat.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuthUserRequest {
    String accountId;
    String firstName;
    String lastName;
    String fullName;
    String avatar;
}