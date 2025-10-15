package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_conversations")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@CompoundIndex(name = "userId_conversationId_idx", def = "{'userId': 1, 'conversationId': 1}")
public class UserConversation {
    @Id
    String id;
    String userId;
    String conversationId;
    Long deletedAt;
}
