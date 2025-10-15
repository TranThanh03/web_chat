package com.example.chat.service;

import com.example.chat.dto.response.MessageResponse;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.MessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {
    MessageRepository messageRepository;
    MessageMapper messageMapper;
    UserConversationService userConversationService;

    public List<MessageResponse> getLatestMessages(String conversationId, String userId) {
        Long deletedAt = userConversationService.getDeletedAtByUserIdAndConversationId(userId, conversationId);

        List<MessageResponse> messages;
        if (deletedAt != null) {
            messages = messageMapper.toMessageListResponse(
                    messageRepository.findTop20ByConversationIdAndTimeStampGreaterThanOrderByIdDesc(conversationId, deletedAt)
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
            Long deletedAt = userConversationService.getDeletedAtByUserIdAndConversationId(userId, conversationId);

            List<MessageResponse> messages;
            if (deletedAt != null) {
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
