package com.example.chat.dto.response.notification;

import com.example.chat.dto.response.attachment.AttachmentSummaryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageNotificationResponse {
    String actionName;
    String actionAvatar;
    String conversationId;
    String conversationName;
    String conversationAvatar;
    String content;
    List<AttachmentSummaryResponse> attachments;
    String type;
    Long createAt;
}
