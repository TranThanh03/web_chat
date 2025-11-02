package com.example.chat.controller;

import com.corundumstudio.socketio.*;
import com.example.chat.service.RedisService;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class  AppSocketHandler {
    SocketIONamespace appNamespace;
    RedisService redisService;
    ChatSocketHandler chatSocketHandler;
    NotificationSocketHandler notificationSocketHandler;
    PresenceSocketHandler presenceSocketHandler;

    @PostConstruct
    public void init() {
        appNamespace.addConnectListener(this::handleConnect);
        appNamespace.addDisconnectListener(this::handleDisconnect);

        chatSocketHandler.registerEvents(appNamespace);
        notificationSocketHandler.registerEvents(appNamespace);
        presenceSocketHandler.registerEvents(appNamespace);
    }

    private void handleConnect(SocketIOClient client) {
        try {
            String userId = client.getHandshakeData().getHttpHeaders().get("userId");
            String socketId = client.getSessionId().toString();
            client.set("userId", userId);

            redisService.addSocketForUser(userId, socketId);
            redisService.markUserOnline(userId);

            client.joinRoom("notification:" + userId);
        } catch (Exception e) {
            client.disconnect();
        }
    }

    private void handleDisconnect(SocketIOClient client) {
        String socketId = client.getSessionId().toString();
        String userId = redisService.getUserBySocket(socketId);

        chatSocketHandler.handleExitChat(client, null, null);

        if (userId != null) {
            redisService.removeSocketForUser(userId, socketId);
            if (!redisService.hasActiveSockets(userId)) {
                redisService.markUserOffline(userId);
            }
            redisService.deleteSocketMapping(socketId);
        }
    }
}
