package com.example.chat.service;

import com.example.chat.dto.request.UserIdRequest;
import com.example.chat.entity.Conversation;
import com.example.chat.entity.GroupConversation;
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
    UserService userService;
    FriendService friendService;

    @Retryable(
            retryFor = DuplicateKeyException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public Conversation createSingle(String ownerId, UserIdRequest request) {
        String userId = request.getUserId();
        userService.verifyActiveAccount(userId);
        friendService.validateAreFriends(ownerId, userId);
        validateConversationNotExists(ownerId, userId);

        SingleConversation conversation = new SingleConversation();
        String generateCode = CodeGenerator.generateShortCode();
        List<String> newParticipantIds = new ArrayList<>(List.of(ownerId, userId));

        conversation.setCode(generateCode);
        conversation.setOwnerId(ownerId);
        conversation.setParticipantIds(newParticipantIds);
        conversation.setType(ConversationType.SINGLE.name());
        conversation.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        conversation.setStatus(ConversationStatus.ACTIVE.name());

        return conversationRepository.save(conversation);
    }

    public SingleConversation getSingleById(String id) {
        return singleConversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXITED));
    }

    public void validateConversationNotExists(String ownerId, String friendId) {
        List<String> newParticipantIds = new ArrayList<>(List.of(ownerId, friendId));

        if (singleConversationRepository.existsByParticipantIdsAndType(newParticipantIds, ConversationType.SINGLE.name())) {
            throw new AppException(ErrorCode.CONVERSATION_EXITED);
        }
    }

    public void validateUserInSingle(String id, String userId) {
        SingleConversation conversation = getSingleById(id);

        if (!conversation.getParticipantIds().contains(userId)) {
            throw new AppException(ErrorCode.USER_NOT_IN_CONVERSATION);
        }
    }
}