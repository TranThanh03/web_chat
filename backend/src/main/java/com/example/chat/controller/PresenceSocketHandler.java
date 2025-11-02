package com.example.chat.controller;

import com.corundumstudio.socketio.*;
import com.example.chat.enums.PresenceEvent;
import com.example.chat.service.PresenceService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PresenceSocketHandler {
    PresenceService presenceService;

    @NonFinal
    SocketIONamespace presenceNamespace;

    private static final String PRESENCE_ROOM_PREFIX = "presence:";

    public void registerEvents(SocketIONamespace namespace) {
        this.presenceNamespace = namespace;
        presenceService.init(namespace, PRESENCE_ROOM_PREFIX);
        namespace.addEventListener(PresenceEvent.PRESENCE_HEARTBEAT.getEvent(), String.class, this::handleHeartbeat);
    }

    public void handleHeartbeat(SocketIOClient client, String data, AckRequest ackSender) {
        String socketId = client.getSessionId().toString();
        presenceService.heartbeat(socketId);
    }
}
