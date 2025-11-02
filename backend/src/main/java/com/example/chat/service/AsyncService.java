package com.example.chat.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.chat.dto.request.notification.FriendNotificationRequest;
import com.example.chat.dto.request.notification.MessageNotificationRequest;
import com.example.chat.dto.request.notification.SendEventToUserRequest;
import com.example.chat.dto.response.attachment.AttachmentSummaryResponse;
import com.example.chat.dto.response.message.MessageResponse;
import com.example.chat.dto.response.notification.FriendNotificationResponse;
import com.example.chat.dto.response.notification.MessageNotificationResponse;
import com.example.chat.entity.Attachment;
import com.example.chat.enums.NotificationType;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.projection.GroupNotifyView;
import com.example.chat.projection.UserNotifyView;
import com.example.chat.util.TimeUtils;
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
public class AsyncService {
    AttachmentService attachmentService;
    ConversationService conversationService;
    ChatService chatService;
    GroupConversationService groupConversationService;
    UserService userService;
    DirectNotificationService directNotificationService;

    @Async("taskExecutor")
    public void handleProcessAttachment(
            SocketIOClient client,
            String conversationId,
            String senderId,
            List<String> publicIds
    ) {
        try {
            List<MessageResponse> responses = attachmentService.getMetadata(conversationId, senderId, publicIds);

            for (MessageResponse response : responses) {
                chatService.handleSendAttachment(conversationId, response);
                this.handleSendMessageNotification(
                        MessageNotificationRequest.builder()
                                .conversationId(conversationId)
                                .senderId(senderId)
                                .attachments(response.getAttachments())
                                .createAt(TimeUtils.toUnixMillisUtcNow())
                                .build()
                );
            }
        } catch (AppException ae) {
            chatService.handleSendAppException(client, ae);
        } catch (Exception ex) {
            AppException ae = new AppException(
                    ErrorCode.UNKNOWN_ERROR,
                    ex.getMessage()
            );

            chatService.handleSendAppException(client, ae);
        }
    }

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
                directNotificationService.sendMessageNotification(userId, response);
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

        directNotificationService.sendFriendNotification(request.getUserId(), response);
    }
}