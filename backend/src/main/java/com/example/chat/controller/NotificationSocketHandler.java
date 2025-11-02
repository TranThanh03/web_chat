package com.example.chat.controller;

import com.corundumstudio.socketio.*;
import com.example.chat.dto.request.notification.MessageNotificationRequest;
import com.example.chat.dto.response.notification.FriendNotificationResponse;
import com.example.chat.dto.response.notification.MessageNotificationResponse;
import com.example.chat.enums.NotificationEvent;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationSocketHandler {
    @NonFinal
    SocketIONamespace notiNamespace;

    private static final String NOTI_ROOM_PREFIX = "notification:";

    public void registerEvents(SocketIONamespace namespace) {
        this.notiNamespace = namespace;
    }

    public void handleSendDirectMessageNotification(String userId, MessageNotificationResponse response) {
        String room = NOTI_ROOM_PREFIX + userId;
        notiNamespace.getRoomOperations(room).sendEvent(NotificationEvent.NOTIFICATION_DIRECT.getEvent(), response);
    }

    public void handleSendDirectFriendNotification(String userId, FriendNotificationResponse response) {
        String room = NOTI_ROOM_PREFIX + userId;
        notiNamespace.getRoomOperations(room).sendEvent(NotificationEvent.NOTIFICATION_DIRECT.getEvent(), response);
    }
}
