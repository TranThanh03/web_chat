package com.example.chat.dto.response.message;

import com.example.chat.entity.Attachment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    String id;
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