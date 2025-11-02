package com.example.chat.dto.request.notification;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEventToUserRequest {
    String conversationId;
    String userId;
    Set<SocketIOClient> sockets;
    String event;
}
