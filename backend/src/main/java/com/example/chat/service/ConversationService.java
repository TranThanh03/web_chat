package com.example.chat.service;

import com.example.chat.dto.request.ConversationRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.entity.Conversation;
import com.example.chat.enums.ActionType;
import com.example.chat.enums.ConversationType;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.util.CodeGenerator;
import com.example.chat.util.TimeUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    UserService userService;
    ChatService chatService;
    FriendService friendService;
    SimpMessagingTemplate messagingTemplate;

    @Retryable(
            retryFor = DuplicateKeyException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public Conversation createConversation(String ownerId, ConversationRequest request) {
        String generateCode = CodeGenerator.generateShortCode();

        if (request.getParticipants().isEmpty() || !userService.checkParticipantsValid(request.getParticipants())) {
            throw new AppException(ErrorCode.PARTICIPANT_INVALID);
        }

        friendService.validateFriendships(ownerId, request.getParticipants());

        Conversation conversation = new Conversation();

        conversation.setCode(generateCode);
        conversation.setName(request.getName());
        conversation.setOwnerId(ownerId);
        conversation.setParticipants(request.getParticipants());
        conversation.setType(request.getParticipants().size() > 2 ? ConversationType.GROUP.name() : ConversationType.SINGLE.name());
        conversation.setCreatedAt(TimeUtils.toUnixMillisUtcNow());

        return conversationRepository.save(conversation);
    }

    public void groupCreationEvents(String conversationId, String ownerId, List<String> memberIds) {
        chatService.systemMessage(conversationId, ActionType.CREATE_GROUP.name(), ownerId, null, null);

        memberIds.stream()
                .forEach(userId ->
                        chatService.systemMessage(conversationId, ActionType.ADD_MEMBER.name(), ownerId, userId, null)
                );
    }

    public void addUserToGroupEvents(String conversationId, String actorId, List<String> participants) {
        if (participants.isEmpty() || !userService.checkParticipantsValid(participants)) {
            throw new AppException(ErrorCode.PARTICIPANT_INVALID);
        }

        friendService.validateFriendships(actorId, participants);

        Conversation conversation = this.getConversationById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.CANNOT_ADD_PARTICIPANT);
        }

        List<String> currentParticipants = new ArrayList<>(conversation.getParticipants());

        if (!currentParticipants.contains(actorId)) {
            throw new AppException(ErrorCode.ADD_PERSON_INVALID);
        }

        List<String> newParticipants = participants.stream()
                .filter(userId -> !currentParticipants.contains(userId) || !conversation.getOwnerId().contains(userId))
                .collect(Collectors.toList());

        if (newParticipants.isEmpty()) {
            throw new AppException(ErrorCode.NEW_PARTICIPANTS_EMPTY);
        }

        for (String userId : newParticipants) {
            MessageResponse systemMsg = chatService.systemMessage(conversationId, ActionType.ADD_MEMBER.name(), actorId, userId, null);

            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId,
                    systemMsg
            );

            currentParticipants.add(userId);
        }

        conversation.setParticipants(currentParticipants);

        conversationRepository.save(conversation);
    }

    public void joinGroupEvents(String conversationId, String userId) {
        userService.validateActiveUserExists(userId);

        Conversation conversation = this.getConversationById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.CANNOT_JOIN_GROUP);
        }

        List<String> currentParticipants = conversation.getParticipants();

        if (currentParticipants.contains(userId)) {
            throw new AppException(ErrorCode.CANNOT_JOIN_GROUP);
        }

        MessageResponse systemMsg = chatService.systemMessage(conversationId, ActionType.JOIN_GROUP.name(), userId, null, null);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                systemMsg
        );

        currentParticipants.add(userId);

        conversationRepository.save(conversation);
    }

    public Conversation getConversationById(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXITED));
    }
}
