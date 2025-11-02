package com.example.chat.service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.example.chat.dto.request.message.MessageData;
import com.example.chat.dto.request.message.SendEventToConversationRequest;
import com.example.chat.dto.request.notification.MessageNotificationRequest;
import com.example.chat.dto.response.message.MessageResponse;
import com.example.chat.enums.ChatEvent;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    @NonFinal
    SocketIONamespace chatNamespace;

    @NonFinal
    String CHAT_ROOM_PREFIX;

    @NonFinal
    Map<String, Map<String, Set<SocketIOClient>>> conversationClients = new ConcurrentHashMap<>();

    public void init(SocketIONamespace chatNamespace, String chatRoomPrefix) {
        this.chatNamespace = chatNamespace;
        this.CHAT_ROOM_PREFIX = chatRoomPrefix;
    }

    public void joinChat(SocketIOClient client, String conversationId, AckRequest ackSender) {
        String room = CHAT_ROOM_PREFIX + conversationId;

        try {
            client.set("conversationId", conversationId);
            client.joinRoom(room);

            String userId = client.get("userId");
            conversationClients
                    .computeIfAbsent(conversationId, c -> new ConcurrentHashMap<>())
                    .computeIfAbsent(userId, u -> ConcurrentHashMap.newKeySet())
                    .add(client);
        } catch (Exception e) {
            client.leaveRoom(room);
            if (ackSender.isAckRequested()) {
                ackSender.sendAckData("error", e.getMessage());
            }
        }
    }

    public void exitChat(SocketIOClient client, AckRequest ackSender) {
        try {
            String conversationId = client.get("conversationId");
            String userId = client.get("userId");

            if (conversationId == null || userId == null) {
                return;
            }

            String room = CHAT_ROOM_PREFIX + conversationId;
            client.leaveRoom(room);

            Map<String, Set<SocketIOClient>> users = conversationClients.get(conversationId);
            if (users != null) {
                Set<SocketIOClient> clients = users.get(userId);
                if (clients != null) {
                    clients.remove(client);
                    if (clients.isEmpty()) {
                        users.remove(userId);
                    }
                }

                if (users.isEmpty()) {
                    conversationClients.remove(conversationId);
                }
            }
        } catch (Exception e) {
            if (ackSender.isAckRequested()) {
                ackSender.sendAckData("error", e.getMessage());
            }
        }
    }

    public void handleSendAttachment(String conversationId, MessageResponse response) {
        String room = CHAT_ROOM_PREFIX + conversationId;
        chatNamespace.getRoomOperations(room).sendEvent(ChatEvent.CHAT_MESSAGE_NEW.getEvent(), response);
    }

    public void sendSystemMessage(String conversationId, MessageResponse systemMsg) {
        String room = CHAT_ROOM_PREFIX + conversationId;
        chatNamespace.getRoomOperations(room).sendEvent(ChatEvent.CHAT_MESSAGE_NEW.getEvent(), systemMsg);
    }

    public void handleSendAppException(SocketIOClient client, AppException ae) {
        client.sendEvent(ChatEvent.CHAT_MESSAGE_FAILED.getEvent(), ae);
    }

    public void sendEventToConversation(SendEventToConversationRequest request) {
        String room = CHAT_ROOM_PREFIX + request.getConversationId();
        chatNamespace.getRoomOperations(room).sendEvent(request.getEvent(), request.getData());
    }

    public Set<String> getOnlineUserIdsByConversationId(String conversationId) {
        return conversationClients.getOrDefault(conversationId, Collections.emptyMap()).keySet();
    }

    public Set<SocketIOClient> getSockets(String conversationId, String userId) {
        return conversationClients
                .getOrDefault(conversationId, Collections.emptyMap())
                .getOrDefault(userId, Collections.emptySet());
    }

    public Collection<SocketIOClient> getClientsByConversationId(String conversationId) {
        Map<String, Set<SocketIOClient>> users = conversationClients.get(conversationId);

        if (users == null) {
            return Collections.emptyList();
        }

        return users.values()
                .stream()
                .flatMap(Set::stream)
                .toList();
    }
}