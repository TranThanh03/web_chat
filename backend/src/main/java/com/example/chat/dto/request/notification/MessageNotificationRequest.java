package com.example.chat.dto.request.notification;

import com.example.chat.entity.Attachment;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageNotificationRequest {
    String conversationId;
    String senderId;
    String content;
    List<Attachment> attachments;
    Long createAt;
}
