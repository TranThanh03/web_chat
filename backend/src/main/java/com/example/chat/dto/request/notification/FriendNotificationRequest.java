package com.example.chat.dto.request.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendNotificationRequest {
    String actorId;
    String userId;
    String type;
    Long createAt;
}
