package com.example.chat.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.chat.controller.NotificationSocketHandler;
import com.example.chat.dto.request.notification.FriendNotificationRequest;
import com.example.chat.dto.request.notification.MessageNotificationRequest;
import com.example.chat.dto.request.notification.SendEventToUserRequest;
import com.example.chat.dto.response.attachment.AttachmentSummaryResponse;
import com.example.chat.dto.response.notification.FriendNotificationResponse;
import com.example.chat.dto.response.notification.MessageNotificationResponse;
import com.example.chat.entity.Attachment;
import com.example.chat.enums.NotificationType;
import com.example.chat.projection.GroupNotifyView;
import com.example.chat.projection.UserNotifyView;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AsyncNotificationService {
    NotificationSocketHandler notificationSocketHandler;
    ConversationService conversationService;
    ChatService chatService;
    GroupConversationService groupConversationService;
    UserService userService;

    @Async("taskExecutor")
    public void handleSendMessageNotification(MessageNotificationRequest request) {
        String senderId = request.getSenderId();
        String conversationId = request.getConversationId();
        List<Attachment> attachments = request.getAttachments();
        List<String> participantIds = conversationService.getParticipantIdsInConversation(conversationId);
        Set<String> onlineUserIds = chatService.getOnlineUserIdsByConversationId(conversationId);
        UserNotifyView userNotify = userService.getUserNotifyById(senderId);
        GroupNotifyView groupNotify = groupConversationService.getGroupNotifyById(conversationId);
        List<AttachmentSummaryResponse> attachmentSummarys = new ArrayList<>();

        if (attachments != null && !attachments.isEmpty()) {
            for (var item : attachments) {
                attachmentSummarys.add(
                        AttachmentSummaryResponse.builder()
                                .secureUrl(item.getSecureUrl())
                                .type(item.getResourceType())
                                .format(item.getFormat())
                                .build()
                );
            }
        }

        MessageNotificationResponse response = MessageNotificationResponse.builder()
                .actionName(userNotify.getFullName())
                .actionAvatar(userNotify.getAvatar())
                .conversationId(conversationId)
                .conversationName(groupNotify.getGroupName())
                .conversationAvatar(groupNotify.getGroupAvatar())
                .content(request.getContent())
                .attachments(attachmentSummarys)
                .type(NotificationType.MESSAGE.name())
                .createAt(request.getCreateAt())
                .build();

        for (String userId : participantIds) {
            if (!userId.equals(senderId) && !onlineUserIds.contains(userId)) {
                notificationSocketHandler.handleSendDirectMessageNotification(userId, response);
            }
        }
    }

    @Async("taskExecutor")
    public void handleSendEventToUserInConversation(SendEventToUserRequest request) {
        var sockets = request.getSockets();

        if(sockets.isEmpty()) {
            return;
        }

        for (SocketIOClient client : sockets) {
            client.sendEvent(request.getEvent());
        }
    }

    @Async("taskExecutor")
    public void handleSendFriendNotification(FriendNotificationRequest request) {
        String actorId = request.getActorId();
        UserNotifyView userNotify = userService.getUserNotifyById(actorId);

        FriendNotificationResponse response = FriendNotificationResponse.builder()
                .actionName(userNotify.getFullName())
                .actionAvatar(userNotify.getAvatar())
                .type(request.getType())
                .createAt(request.getCreateAt())
                .build();

        notificationSocketHandler.handleSendDirectFriendNotification(request.getUserId(), response);
    }
}