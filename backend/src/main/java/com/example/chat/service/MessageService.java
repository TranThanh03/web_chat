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

    public List<MessageResponse> getLatestMessages(String conversationId) {
        List<MessageResponse> messages = messageMapper.toMessageListResponse(messageRepository.findTop20ByConversationIdOrderByIdDesc(conversationId));
        Collections.reverse(messages);

        return messages;
    }

    public List<MessageResponse> getMoreMessages(String conversationId, String messageId) {
        try {
            ObjectId objectId = new ObjectId(messageId);

            List<MessageResponse> messages = messageMapper.toMessageListResponse(messageRepository.findTop20ByConversationIdAndIdLessThanOrderByIdDesc(conversationId, objectId));
            Collections.reverse(messages);

            return messages;
        } catch (Exception e) {
            throw new AppException(ErrorCode.MESSAGE_ID_INVALID);
        }
    }
}
