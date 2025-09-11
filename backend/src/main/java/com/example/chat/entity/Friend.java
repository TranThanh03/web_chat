package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "friends")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Friend {
    @Id
    String id;

    String userId;
    String friendId;
    String status;
    String actionUserId;
    Long createdAt;
    Long updatedAt;

    @Indexed(expireAfterSeconds = 0)
    Instant expireAt;
}
