package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh_tokens")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@CompoundIndex(name = "accountId_hashedToken_idx", def = "{'accountId': 1, 'hashedToken': 1}")
public class RefreshToken {
    @Id
    String id;

    String accountId;
    String hashedToken;

    @Indexed(expireAfterSeconds = 0)
    Instant expiryTime;
}
