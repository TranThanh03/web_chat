package com.example.chat.service;

import com.example.chat.entity.Conversation;
import com.example.chat.enums.ConversationType;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.ConversationRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    GroupConversationService groupConversationService;
    SingleConversationService singleConversationService;

    public Conversation getConversationById(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXITED));
    }

    public void validateActiveMemberInConversation(String id, String userId) {
        Conversation conversation = getConversationById(id);

        if (conversation.getType().equals(ConversationType.GROUP.name())) {
            groupConversationService.validateActiveMemberInGroup(id, userId);
        } else {
            singleConversationService.validateUserInSingle(id, userId);
        }
    }

    public void validateUserInConversation(String id, String userId) {
        Conversation conversation = getConversationById(id);

        if (conversation.getType().equals(ConversationType.GROUP.name())) {
            groupConversationService.validateUserInGroup(id, userId);
        } else {
            singleConversationService.validateUserInSingle(id, userId);
        }
    }

    public void deleteConversation(String conversationId, String userId) {
        validateUserInConversation(conversationId, userId);

        Conversation conversation = getConversationById(conversationId);

        if (conversation.getDeletedByUserIds() == null) {
            conversation.setDeletedByUserIds(new ArrayList<>());
        }

        if (conversation.getDeletedByUserIds().contains(userId)) {
            throw new AppException(ErrorCode.USER_ALREADY_DELETED_CONVERSATION);
        }

        conversation.getDeletedByUserIds().add(userId);
        conversationRepository.save(conversation);
    }

    public void restoreConversation(String conversationId, String userId) {
        validateUserInConversation(conversationId, userId);

        Conversation conversation = getConversationById(conversationId);

        if (conversation.getDeletedByUserIds() == null || !conversation.getDeletedByUserIds().contains(userId)) {
            throw new AppException(ErrorCode.USER_NOT_DELETED_CONVERSATION);
        }

        conversation.getDeletedByUserIds().remove(userId);
        conversationRepository.save(conversation);
    }

}