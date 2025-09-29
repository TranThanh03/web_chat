package com.example.chat.service;

import com.example.chat.dto.request.MessageRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.entity.Message;
import com.example.chat.enums.MessageType;
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

    private static final Map<String, MessageType> MEDIA_MAP = Map.of(
            "image", MessageType.IMAGE,
            "video", MessageType.VIDEO,
            "file",  MessageType.FILE
    );

    public MessageResponse sendMessage(String conversationId, MessageRequest request) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent());

        MessageType type = MEDIA_MAP.getOrDefault(request.getType(), MessageType.TEXT);
        message.setType(type.name());

        message.setActionType(null);
        message.setActorId(null);
        message.setTargetId(null);
        message.setExtraData(null);
        message.setTimeStamp(TimeUtils.toUnixMillisUtcNow());
        message.setStatus(MessageStatus.SENT.name());

        messageRepository.save(message);

        return messageMapper.toMessageResponse(message);
    }

    public MessageResponse systemMessage(String conversationId, String actionType, String actorId, String targetId, String extraData) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(null);
        message.setContent(null);
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
}