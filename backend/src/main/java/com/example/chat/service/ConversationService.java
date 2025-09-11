package com.example.chat.service;

import com.example.chat.dto.request.ConversationRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.entity.Conversation;
import com.example.chat.enums.ConversationType;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.util.ShortCodeGenerator;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    UserService userService;
    ChatService chatService;
    SimpMessagingTemplate messagingTemplate;

    @Retryable(
            value = DuplicateKeyException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public Conversation createConversation(ConversationRequest request) {
        String generateCode = ShortCodeGenerator.generateShortCodeFromUUID();

        if (!userService.checkParticipantsValid(request.getParticipants())) {
            throw new AppException(ErrorCode.PARTICIPANT_INVALID);
        }

        Conversation conversation = new Conversation();

        conversation.setCode(generateCode);
        conversation.setName(request.getName());
        conversation.setParticipants(request.getParticipants());
        conversation.setType(request.getParticipants().size() > 2 ? ConversationType.GROUP.name() : ConversationType.SINGLE.name());
        conversation.setCreatedAt(TimeUtils.toUnixMillisUtcNow());

        return conversationRepository.save(conversation);
    }

    public void groupCreationEvents(String conversationId, String creatorId, List<String> participants) {
        Map<String, String> fullNames = participants.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> userService.getUserById(id).getFullName()
                ));

        chatService.systemMessage(conversationId, fullNames.get(creatorId) + " đã tạo nhóm.");

        participants.stream()
                .filter(userId -> !userId.equals(creatorId))
                .forEach(userId ->
                        chatService.systemMessage(conversationId, fullNames.get(creatorId) + " đã thêm " + fullNames.get(userId) + " vào nhóm.")
                );
    }

    public void addUserToGroupEvents(String conversationId, String addPersonId, List<String> participants) {
        if (!userService.checkParticipantsValid(participants)) {
            throw new AppException(ErrorCode.PARTICIPANT_INVALID);
        }

        Conversation conversation = this.getConversationById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.CANNOT_ADD_PARTICIPANT);
        }

        List<String> currentParticipants = new ArrayList<>(conversation.getParticipants());

        if (!currentParticipants.contains(addPersonId)) {
            throw new AppException(ErrorCode.ADD_PERSON_INVALID);
        }

        List<String> newParticipants = participants.stream()
                .filter(userId -> !currentParticipants.contains(userId))
                .collect(Collectors.toList());

        if (newParticipants.isEmpty()) {
            throw new AppException(ErrorCode.NEW_PARTICIPANTS_EMPTY);
        }

        Map<String, String> fullNames = newParticipants.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> userService.getUserById(id).getFullName()
                ));

        String addPersonName = userService.getUserById(addPersonId).getFullName();

        for (String userId : newParticipants) {
            String msg = addPersonName + " đã thêm " + fullNames.get(userId) + " vào nhóm.";
            MessageResponse systemMsg = chatService.systemMessage(conversationId, msg);

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
        userService.validateUserIdExists(userId);

        Conversation conversation = this.getConversationById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.CANNOT_JOIN_GROUP);
        }

        List<String> currentParticipants = conversation.getParticipants();

        if (currentParticipants.contains(userId)) {
            throw new AppException(ErrorCode.CANNOT_JOIN_GROUP);
        }

        String fullName = userService.getUserById(userId).getFullName();

        String msg = fullName + " đã tham gia nhóm.";
        MessageResponse systemMsg = chatService.systemMessage(conversationId, msg);

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
