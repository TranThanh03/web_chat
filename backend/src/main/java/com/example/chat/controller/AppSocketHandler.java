package com.example.chat.controller;

import com.corundumstudio.socketio.*;
import com.example.chat.service.ChatService;
import com.example.chat.service.PresenceService;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class  AppSocketHandler {
    SocketIONamespace appNamespace;
    ChatSocketHandler chatSocketHandler;
    DirectNotificationSocketHandler directNotificationSocketHandler;
    PresenceSocketHandler presenceSocketHandler;
    ChatService chatService;
    PresenceService presenceService;

    @PostConstruct
    public void init() {
        appNamespace.addConnectListener(this::handleConnect);
        appNamespace.addDisconnectListener(this::handleDisconnect);

        chatSocketHandler.registerEvents(appNamespace);
        directNotificationSocketHandler.registerEvents(appNamespace);
        presenceSocketHandler.registerEvents(appNamespace);
    }

    private void handleConnect(SocketIOClient client) {
        try {
            String userId = client.getHandshakeData().getHttpHeaders().get("userId");
            String socketId = client.getSessionId().toString();
            client.set("userId", userId);

            presenceService.register(userId, socketId);

            client.joinRoom("notification:" + userId);
        } catch (Exception e) {
            client.disconnect();
        }
    }

    private void handleDisconnect(SocketIOClient client) {
        String userId = client.get("userId");
        String socketId = client.getSessionId().toString();

        presenceService.remove(userId, socketId);
        chatService.exitChat(client, null);
    }
}
