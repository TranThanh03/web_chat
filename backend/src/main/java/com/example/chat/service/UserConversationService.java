package com.example.chat.service;

import com.example.chat.dto.request.conversation.UserConversationCreationRequest;
import com.example.chat.entity.UserConversation;
import com.example.chat.repository.UserConversationRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserConversationService {
    UserConversationRepository userConversationRepository;

    private UserConversation markUserConversationAsDeleted(UserConversationCreationRequest request) {
        UserConversation userConversation = new UserConversation();

        userConversation.setUserId(request.getUserId());
        userConversation.setConversationId(request.getConversationId());
        userConversation.setDeletedAt(TimeUtils.toUnixMillisUtcNow());

        return userConversationRepository.save(userConversation);
    }

    private UserConversation markUserConversationAsRestricted(UserConversationCreationRequest request) {
        UserConversation userConversation = new UserConversation();

        userConversation.setUserId(request.getUserId());
        userConversation.setConversationId(request.getConversationId());
        userConversation.setRestrictedAt(TimeUtils.toUnixMillisUtcNow());

        return userConversationRepository.save(userConversation);
    }

    public UserConversation getByUserIdAndConversationId(String userId, String conversationId) {
        return userConversationRepository.findByUserIdAndConversationId(userId, conversationId);
    }

    public void handleUserConversationAsDeleted(UserConversationCreationRequest request) {
        UserConversation userConversation = getByUserIdAndConversationId(request.getUserId(), request.getConversationId());

        if (userConversation == null) {
            markUserConversationAsDeleted(request);
        } else {
            userConversation.setDeletedAt(TimeUtils.toUnixMillisUtcNow());
            userConversationRepository.save(userConversation);
        }
    }

    public void handleUserConversationAsRestricted(UserConversationCreationRequest request) {
        UserConversation userConversation = getByUserIdAndConversationId(request.getUserId(), request.getConversationId());

        if (userConversation == null) {
            markUserConversationAsRestricted(request);
        } else {
            userConversation.setRestrictedAt(TimeUtils.toUnixMillisUtcNow());
            userConversationRepository.save(userConversation);
        }
    }

    public void unrestrictUserConversation(UserConversationCreationRequest request) {
        UserConversation userConversation = getByUserIdAndConversationId(request.getUserId(), request.getConversationId());

        if (userConversation != null) {
            userConversation.setRestrictedAt(null);
            userConversationRepository.save(userConversation);
        }
    }

    public void batchUnrestrictUsers(String conversationId, List<String> userIds) {
        userConversationRepository.updateRestrictedAtNullByConversationIdAndUserIds(conversationId, userIds);
    }
}