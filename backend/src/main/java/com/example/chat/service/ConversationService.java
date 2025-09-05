package com.example.chat.service;

import com.example.chat.dto.request.ConversationRequest;
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    UserService userService;

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

    public Conversation getConversationById(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXITED));
    }

//    public List<MessageResponse> loadMessage(String roomId, Pageable pageable) {
//
//    }
}
