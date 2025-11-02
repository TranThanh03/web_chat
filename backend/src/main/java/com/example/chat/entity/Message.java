package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "messages")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@CompoundIndex(name = "conversationId_timestamp_idx", def = "{'conversationId': 1, 'timeStamp': -1}")
public class Message {
    @Id
    String id;

    @Indexed
    String conversationId;

    String senderId;
    String content;
    List<Attachment> attachments;
    String type;
    String actionType;
    String actorId;
    String targetId;
    String extraData;
    Long timeStamp;
    String status;
    List<String> readByUserIds;
}
