package com.example.chat.service;

import com.example.chat.dto.request.message.MessageData;
import com.example.chat.dto.response.message.MessageResponse;
import com.example.chat.entity.Message;
import com.example.chat.enums.MessageStatus;
import com.example.chat.enums.MessageType;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.MessageRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {
    MessageRepository messageRepository;
    MessageMapper messageMapper;
    UserConversationService userConversationService;

    private static final Map<String, MessageType> MEDIA_MAP = Map.of(
            "image", MessageType.IMAGE,
            "video", MessageType.VIDEO,
            "file",  MessageType.FILE
    );

    public MessageResponse userMessage(String conversationId, String senderId, MessageData data) {
        Message message = new Message();

        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setType(MessageType.TEXT.name());
        message.setContent(data.getContent());
        message.setTimeStamp(TimeUtils.toUnixMillisUtcNow());
        message.setStatus(MessageStatus.SENT.name());

        messageRepository.save(message);

        return messageMapper.toMessageResponse(message);
    }

    public MessageResponse systemMessage(String conversationId, String actionType, String actorId, String targetId, String extraData) {
        Message message = new Message();

        message.setConversationId(conversationId);
        message.setType(MessageType.SYSTEM.name());
        message.setActionType(actionType);
        message.setActorId(actorId);
        message.setTargetId(targetId);
        message.setExtraData(extraData);
        message.setTimeStamp(TimeUtils.toUnixMillisUtcNow());
        message.setStatus(MessageStatus.SENT.name());

        messageRepository.save(message);

        return messageMapper.toMessageResponse(message);
    }

    public MessageResponse atachmentMessage(String conversationId, String senderId, String resourceType, List attachments) {
        Message message = new Message();

        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setAttachments(attachments);
        MessageType type = MEDIA_MAP.getOrDefault(resourceType, MessageType.FILE);
        message.setType(type.name());
        message.setTimeStamp(TimeUtils.toUnixMillisUtcNow());
        message.setStatus(MessageStatus.SENT.name());

        messageRepository.save(message);

        return messageMapper.toMessageResponse(message);
    }

    public List<MessageResponse> getLatestMessages(String conversationId, String userId) {
        var userConversation = userConversationService.getByUserIdAndConversationId(userId, conversationId);
        Long deletedAt = userConversation != null ? userConversation.getDeletedAt() : null;
        Long restrictedAt = userConversation != null ? userConversation.getRestrictedAt() : null;

        List<MessageResponse> messages;

        if (restrictedAt != null && deletedAt != null) {
            messages = messageMapper.toMessageListResponse(
                    messageRepository.findTop20Between(
                            conversationId, deletedAt, restrictedAt
                    )
            );
        } else if (restrictedAt != null) {
            messages = messageMapper.toMessageListResponse(
                    messageRepository.findTop20ByConversationIdAndTimeStampLessThanEqualOrderByIdDesc(
                            conversationId, restrictedAt
                    )
            );
        } else if (deletedAt != null) {
            messages = messageMapper.toMessageListResponse(
                    messageRepository.findTop20ByConversationIdAndTimeStampGreaterThanOrderByIdDesc(
                            conversationId, deletedAt
                    )
            );
        } else {
            messages = messageMapper.toMessageListResponse(
                    messageRepository.findTop20ByConversationIdOrderByIdDesc(conversationId)
            );
        }

        Collections.reverse(messages);
        return messages;
    }


    public List<MessageResponse> getMoreMessages(String conversationId, String messageId, String userId) {
        try {
            ObjectId objectId = new ObjectId(messageId);
            var userConversation = userConversationService.getByUserIdAndConversationId(userId, conversationId);
            Long deletedAt = userConversation != null ? userConversation.getDeletedAt() : null;
            Long restrictedAt = userConversation != null ? userConversation.getRestrictedAt() : null;
            List<MessageResponse> messages;

            if (restrictedAt != null && deletedAt != null) {
                messages = messageMapper.toMessageListResponse(
                        messageRepository.findTop20BetweenWithIdLessThan(
                                conversationId, deletedAt, restrictedAt, objectId
                        )
                );
            } else if (restrictedAt != null) {
                messages = messageMapper.toMessageListResponse(
                        messageRepository.findTop20ByConversationIdAndTimeStampLessThanEqualAndIdLessThanOrderByIdDesc(
                                conversationId, restrictedAt, objectId
                        )
                );
            } else if (deletedAt != null) {
                messages = messageMapper.toMessageListResponse(
                        messageRepository.findTop20ByConversationIdAndTimeStampGreaterThanAndIdLessThanOrderByIdDesc(
                                conversationId, deletedAt, objectId
                        )
                );
            } else {
                messages = messageMapper.toMessageListResponse(
                        messageRepository.findTop20ByConversationIdAndIdLessThanOrderByIdDesc(conversationId, objectId)
                );
            }

            Collections.reverse(messages);
            return messages;
        } catch (Exception e) {
            throw new AppException(ErrorCode.MESSAGE_ID_INVALID);
        }
    }
}
