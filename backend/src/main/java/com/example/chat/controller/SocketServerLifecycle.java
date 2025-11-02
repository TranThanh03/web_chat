package com.example.chat.controller;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketServerLifecycle {
    SocketIOServer server;

    @PostConstruct
    public void startServer() {
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
    }
}
