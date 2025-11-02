package com.example.chat.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.example.chat.dto.request.message.MessageData;
import com.example.chat.dto.request.notification.MessageNotificationRequest;
import com.example.chat.dto.response.SocketErrorResponse;
import com.example.chat.dto.response.attachment.AttachmentSummaryResponse;
import com.example.chat.dto.response.message.MessageResponse;
import com.example.chat.entity.Attachment;
import com.example.chat.enums.ChatEvent;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AsyncAttachmentProcessor {
    AttachmentService attachmentService;
    NotificationService notificationService;

    @Async("taskExecutor")
    public void process(
            SocketIONamespace chatNamespace,
            SocketIOClient client,
            String room,
            String conversationId,
            String senderId,
            List<String> publicIds
    ) {
        try {
            List<MessageResponse> responses = attachmentService.getMetadata(conversationId, senderId, publicIds);
            List<Attachment> attachments = new ArrayList<>();

            for (MessageResponse response : responses) {
                chatNamespace.getRoomOperations(room).sendEvent(ChatEvent.CHAT_MESSAGE_NEW.getEvent(), response);
                attachments.addAll(response.getAttachments());
            }

            notificationService.sendMessageNotification(
                    MessageNotificationRequest.builder()
                            .conversationId(conversationId)
                            .senderId(senderId)
                            .attachments(attachments)
                            .createAt(TimeUtils.toUnixMillisUtcNow())
                            .build()
            );
        } catch (AppException ae) {
            SocketErrorResponse err = new SocketErrorResponse(
                    ae.getErrorCode().getCode(),
                    ae.getErrorCode().getMessage(),
                    ae.getDetail()
            );
            client.sendEvent(ChatEvent.CHAT_MESSAGE_FAILED.getEvent(), err);
        } catch (Exception ex) {
            SocketErrorResponse err = new SocketErrorResponse(
                    ErrorCode.UNKNOWN_ERROR.getCode(),
                    ErrorCode.UNKNOWN_ERROR.getMessage(),
                    ex.getMessage()
            );
            client.sendEvent(ChatEvent.CHAT_MESSAGE_FAILED.getEvent(), err);
        }
    }
}