package com.example.chat.service;

import com.example.chat.dto.request.MessageRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.entity.Message;
import com.example.chat.enums.MediaType;
import com.example.chat.enums.MessageStatus;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.MessageRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    MessageRepository messageRepository;
    MessageMapper messageMapper;

    private static final Map<String, MediaType> MEDIA_MAP = Map.of(
            "image", MediaType.IMAGE,
            "video", MediaType.VIDEO,
            "file",  MediaType.FILE
    );

    public MessageResponse sendMessage(String conversationId, MessageRequest request) {
        Message message = new Message();

        message.setConversationId(conversationId);
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent().trim());
        message.setTimeStamp(TimeUtils.toUnixMillisUtcNow());

        MediaType type = MEDIA_MAP.getOrDefault(request.getMedia(), MediaType.TEXT);
        message.setMedia(type.name());

        message.setStatus(MessageStatus.SENT.name());

        messageRepository.save(message);

        return messageMapper.toMessageResponse(message);
    }

    public MessageResponse systemMessage(String conversationId, String content) {
        Message message = new Message();

        message.setConversationId(conversationId);
        message.setContent(content);
        message.setTimeStamp(TimeUtils.toUnixMillisUtcNow());
        message.setMedia(MediaType.SYSTEM.name());
        message.setStatus(MessageStatus.SENT.name());

        messageRepository.save(message);

        return messageMapper.toMessageResponse(message);
    }
}