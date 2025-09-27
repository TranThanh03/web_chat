package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    @Id
    String id;

    String conversationId;
    String senderId;
    String content;
    String type;
    String actionType;
    String actorId;
    String targetId;
    String extraData;
    Long timeStamp;
    String status;
}
