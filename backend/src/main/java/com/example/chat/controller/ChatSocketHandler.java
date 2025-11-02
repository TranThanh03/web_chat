package com.example.chat.controller;

import com.corundumstudio.socketio.*;
import com.example.chat.dto.request.message.MessageData;
import com.example.chat.dto.request.notification.MessageNotificationRequest;
import com.example.chat.dto.response.message.MessageResponse;
import com.example.chat.enums.ChatEvent;
import com.example.chat.service.*;
import com.example.chat.util.TimeUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatSocketHandler {
    ChatService chatService;
    ConversationService conversationService;
    MessageService messageService;
    NotificationService notificationService;
    AsyncAttachmentProcessor asyncAttachmentProcessor;

    @NonFinal
    SocketIONamespace chatNamespace;

    private static final String CHAT_ROOM_PREFIX = "chat:";

    public void registerEvents(SocketIONamespace namespace) {
        this.chatNamespace = namespace;
        chatService.init(namespace, CHAT_ROOM_PREFIX);
        namespace.addEventListener(ChatEvent.CHAT_JOIN.getEvent(), String.class, this::handleJoinChat);
        namespace.addEventListener(ChatEvent.CHAT_MESSAGE_SEND.getEvent(), MessageData.class, this::handleSendMessage);
        namespace.addEventListener(ChatEvent.CHAT_EXIT.getEvent(), String.class, this::handleExitChat);
    }

    private void handleJoinChat(SocketIOClient client, String conversationId, AckRequest ackSender) {
        String userId = client.get("userId");

        conversationService.validateActiveUserInConversationActive(conversationId, userId);
        chatService.joinChat(client, conversationId, ackSender);
    }

    private void handleSendMessage(SocketIOClient client, MessageData data, AckRequest ackSender) {
        String senderId = client.get("userId");
        String conversationId = client.get("conversationId");

        conversationService.validateActiveUserInConversationActive(conversationId, senderId);

        try {
            String room = CHAT_ROOM_PREFIX + conversationId;

            if (StringUtils.hasText(data.getContent())) {
                MessageResponse response = messageService.userMessage(conversationId, senderId, data);
                chatNamespace.getRoomOperations(room).sendEvent(ChatEvent.CHAT_MESSAGE_NEW.getEvent(), response);
                notificationService.sendMessageNotification(
                        MessageNotificationRequest.builder()
                                .conversationId(conversationId)
                                .senderId(senderId)
                                .content(data.getContent())
                                .createAt(TimeUtils.toUnixMillisUtcNow())
                                .build()
                );
            }

            if (data.getPublicIds() != null && !data.getPublicIds().isEmpty()) {
                asyncAttachmentProcessor.process(chatNamespace, client, room, conversationId, senderId, data.getPublicIds());
            }
        } catch (Exception e) {
            if (ackSender.isAckRequested()) {
                ackSender.sendAckData("error", e.getMessage());
            }
        }
    }

    public void handleExitChat(SocketIOClient client, String data, AckRequest ackSender) {
        chatService.exitChat(client, ackSender);
    }
}
