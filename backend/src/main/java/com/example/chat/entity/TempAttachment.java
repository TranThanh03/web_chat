package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "temp_attachments")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TempAttachment {
    @Id
    String id;

    @Indexed
    String publicId;

    String userId;
    String conversationId;
    Instant createdAt;

    @Indexed(expireAfterSeconds = 0)
    Instant expireAt;
}
