package com.example.chat.service;

import com.corundumstudio.socketio.SocketIONamespace;
import com.example.chat.enums.PresenceStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PresenceService {
    RedisService redisService;

    @NonFinal
    SocketIONamespace presenceNamespace;

    @NonFinal
    String PRESENCE_ROOM_PREFIX;

    public void init(SocketIONamespace presenceNamespace, String presenceRoomPrefix) {
        this.presenceNamespace = presenceNamespace;
        this.PRESENCE_ROOM_PREFIX = presenceRoomPrefix;
    }

    public void register(String userId, String socketId) {
        redisService.registerPresence(userId, socketId);
    }

    public void remove(String userId, String socketId) {
        redisService.removePresence(userId, socketId);
    }

    public void heartbeat(String socketId) {
        redisService.heartbeatPresence(socketId);
    }

    public String status(String userId) {
        if (redisService.isOnline(userId)) {
            return PresenceStatus.ONLINE.name();
        }

        return PresenceStatus.OFFLINE.name();
    }
}