package com.example.chat.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InfoAccessTokenResponse {
    String token;
    String accountId;
    String userId;
}
