package com.example.chat.service;

import com.example.chat.dto.request.notification.FriendNotificationRequest;
import com.example.chat.dto.request.notification.MessageNotificationRequest;
import com.example.chat.dto.request.notification.NotificationCreationRequest;
import com.example.chat.dto.request.notification.SendEventToUserRequest;
import com.example.chat.entity.Notification;
import com.example.chat.repository.NotificationRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    AsyncNotificationService asyncNotificationService;

    public void createNotification(NotificationCreationRequest request) {
        Notification notification = new Notification();

        notification.setUserId(request.getUserId());
        notification.setActionId(request.getActionId());
        notification.setType(request.getType());
        notification.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    public void sendMessageNotification(MessageNotificationRequest request) {
        asyncNotificationService.handleSendMessageNotification(request);
    }

    public void sendEventToUserInConversation(SendEventToUserRequest request) {
        asyncNotificationService.handleSendEventToUserInConversation(request);
    }

    public void sendFriendNotification(FriendNotificationRequest request) {
        asyncNotificationService.handleSendFriendNotification(request);
    }
}