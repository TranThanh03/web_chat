package com.example.chat.service;

import com.example.chat.dto.request.MessageRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.entity.Message;
import com.example.chat.entity.Conversation;
import com.example.chat.enums.MediaType;
import com.example.chat.enums.MessageStatus;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.repository.MessageRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    MessageRepository messageRepository;
    MessageMapper messageMapper;

    public MessageResponse sendMessage(String conversationId, MessageRequest request) {
        Message message = new Message();

        message.setConversationId(conversationId);
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent());
        message.setTimeStamp(TimeUtils.toUnixMillisUtcNow());
        message.setMedia(MediaType.TEXT.toString());
        message.setStatus(MessageStatus.SEEN.toString());

        messageRepository.save(message);

        return messageMapper.toMessageResponse(message);
    }
}
