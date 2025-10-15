package com.example.chat.service;

import com.example.chat.dto.request.UserConversationCreationRequest;
import com.example.chat.entity.UserConversation;
import com.example.chat.repository.UserConversationRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserConversationService {
    UserConversationRepository userConversationRepository;

    public UserConversation createUserConversation(UserConversationCreationRequest request) {
        UserConversation userConversation = new UserConversation();

        userConversation.setUserId(request.getUserId());
        userConversation.setConversationId(request.getConversationId());
        userConversation.setDeletedAt(TimeUtils.toUnixMillisUtcNow());

        return userConversationRepository.save(userConversation);
    }

    public UserConversation getByUserIdAndConversationId(String userId, String conversationId) {
        return userConversationRepository.findByUserIdAndConversationId(userId, conversationId);
    }

    public Long getDeletedAtByUserIdAndConversationId(String userId, String conversationId) {
        UserConversation userConversation = getByUserIdAndConversationId(userId, conversationId);

        if (userConversation != null) {
            return userConversation.getDeletedAt();
        }

        return null;
    }

    public void handleUserConversation(UserConversationCreationRequest request) {
        UserConversation userConversation = getByUserIdAndConversationId(request.getUserId(), request.getConversationId());

        if (userConversation == null) {
            createUserConversation(request);
        } else {
            userConversation.setDeletedAt(TimeUtils.toUnixMillisUtcNow());
            userConversationRepository.save(userConversation);
        }
    }
}