package com.example.chat.service;

import com.corundumstudio.socketio.SocketIONamespace;
import com.example.chat.dto.response.notification.FriendNotificationResponse;
import com.example.chat.dto.response.notification.MessageNotificationResponse;
import com.example.chat.enums.NotificationEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DirectNotificationService {
    @NonFinal
    SocketIONamespace notifiNamespace;

    @NonFinal
    String NOTIFI_ROOM_PREFIX;

    public void init(SocketIONamespace notifiNamespace, String notifiRoomPrefix) {
        this.notifiNamespace = notifiNamespace;
        this.NOTIFI_ROOM_PREFIX = notifiRoomPrefix;
    }

    public void sendMessageNotification(String userId, MessageNotificationResponse response) {
        String room = NOTIFI_ROOM_PREFIX + userId;
        notifiNamespace.getRoomOperations(room).sendEvent(NotificationEvent.NOTIFICATION_DIRECT.getEvent(), response);
    }

    public void sendFriendNotification(String userId, FriendNotificationResponse response) {
        String room = NOTIFI_ROOM_PREFIX + userId;
        notifiNamespace.getRoomOperations(room).sendEvent(NotificationEvent.NOTIFICATION_DIRECT.getEvent(), response);
    }
}