package com.example.chat.service;

import com.example.chat.dto.request.message.SendEventToConversationRequest;
import com.example.chat.entity.Conversation;
import com.example.chat.entity.SingleConversation;
import com.example.chat.enums.*;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.repository.SingleConversationRepository;
import com.example.chat.util.CodeGenerator;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SingleConversationService {
    ConversationRepository conversationRepository;
    SingleConversationRepository singleConversationRepository;
    ChatService chatService;

    @Retryable(
            retryFor = DuplicateKeyException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public Conversation create(String ownerId, String userId) {
        String generateCode = CodeGenerator.generateShortCode();
        SingleConversation conversation = new SingleConversation();
        List<String> newParticipantIds = new ArrayList<>(List.of(ownerId, userId));

        conversation.setCode(generateCode);
        conversation.setOwnerId(ownerId);
        conversation.setParticipantIds(newParticipantIds);
        conversation.setType(ConversationType.SINGLE.name());
        conversation.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        conversation.setStatus(ConversationStatus.ACTIVE.name());

        return conversationRepository.save(conversation);
    }

    public void handleBlock(String actionId, String userId) {
        List<String> newParticipantIds = new ArrayList<>(List.of(actionId, userId));
        SingleConversation conversation = singleConversationRepository.findByParticipantIdsAndType(newParticipantIds, ConversationType.SINGLE.name());

        if (conversation != null) {
            if (conversation.getStatus().equals(ConversationStatus.ACTIVE.name())) {
                conversation.setStatus(ConversationStatus.BLOCKED.name());
                conversationRepository.save(conversation);

                chatService.sendEventToConversation(
                        SendEventToConversationRequest.builder()
                                .conversationId(conversation.getId())
                                .event(ChatEvent.CHAT_BLOCK.getEvent())
                                .build()
                );
            }
        }
    }

    public void handleUnBlock(String actionId, String userId) {
        List<String> newParticipantIds = new ArrayList<>(List.of(actionId, userId));
        SingleConversation conversation = singleConversationRepository.findByParticipantIdsAndType(newParticipantIds, ConversationType.SINGLE.name());

        if (conversation != null) {
            if (conversation.getStatus().equals(ConversationStatus.BLOCKED.name())) {
                conversation.setStatus(ConversationStatus.ACTIVE.name());
                conversationRepository.save(conversation);

                chatService.sendEventToConversation(
                        SendEventToConversationRequest.builder()
                                .conversationId(conversation.getId())
                                .event(ChatEvent.CHAT_UNBLOCK.getEvent())
                                .build()
                );
            }
        }
    }

    public SingleConversation getById(String id) {
        return singleConversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
    }

    public void validateConversationNotExists(String ownerId, String friendId) {
        List<String> newParticipantIds = new ArrayList<>(List.of(ownerId, friendId));

        if (singleConversationRepository.existsByParticipantIdsAndType(newParticipantIds, ConversationType.SINGLE.name())) {
            throw new AppException(ErrorCode.CONVERSATION_ALREADY_EXISTS);
        }
    }

    public void validateUserInSingle(String id, String userId) {
        SingleConversation conversation = this.getById(id);

        if (!conversation.getParticipantIds().contains(userId)) {
            throw new AppException(ErrorCode.USER_NOT_IN_CONVERSATION);
        }
    }

    public void validateUserInSingleActive(String id, String userId) {
        SingleConversation conversation = this.getById(id);

        if (!conversation.getStatus().equals(ConversationStatus.ACTIVE.name())) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_ACTIVE);
        }

        if (!conversation.getParticipantIds().contains(userId)) {
            throw new AppException(ErrorCode.USER_NOT_IN_CONVERSATION);
        }
    }

    public List<String> getUserIdInSingle(String id) {
        SingleConversation conversation = this.getById(id);

        return conversation.getParticipantIds();
    }
}