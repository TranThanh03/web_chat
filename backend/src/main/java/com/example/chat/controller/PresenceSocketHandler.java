package com.example.chat.controller;

import com.corundumstudio.socketio.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PresenceSocketHandler {
    public void registerEvents(SocketIONamespace namespace) {
        namespace.addEventListener("presence:update", String.class, this::handlePresenceUpdate);
    }

    private void handlePresenceUpdate(SocketIOClient client, String status, AckRequest ackSender) {
        String userId = client.get("userId");
        log.info("Presence update from {}: {}", userId, status);
        client.getNamespace().getBroadcastOperations().sendEvent("presence:update", userId, status);
    }
}
