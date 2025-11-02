package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirectNotification {
    String actionName;
    String actionAvatar;
    String conversationId;
    String conversationName;
    String conversationAvatar;
    String content;
    String type;
}
