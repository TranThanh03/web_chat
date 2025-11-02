package com.example.chat.dto.response.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendNotificationResponse {
    String actionName;
    String actionAvatar;
    String type;
    Long createAt;
}
