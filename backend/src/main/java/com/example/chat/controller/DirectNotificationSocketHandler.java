package com.example.chat.controller;

import com.corundumstudio.socketio.*;
import com.example.chat.service.DirectNotificationService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DirectNotificationSocketHandler {
    DirectNotificationService directNotificationService;

    @NonFinal
    SocketIONamespace notifiNamespace;

    private static final String NOTIFI_ROOM_PREFIX = "notification:";

    public void registerEvents(SocketIONamespace namespace) {
        this.notifiNamespace = namespace;
        directNotificationService.init(namespace, NOTIFI_ROOM_PREFIX);
    }
}
