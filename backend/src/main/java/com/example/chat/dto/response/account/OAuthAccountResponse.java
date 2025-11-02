package com.example.chat.dto.response.account;

import com.example.chat.entity.Account;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuthAccountResponse {
    Account account;
    String userId;
}