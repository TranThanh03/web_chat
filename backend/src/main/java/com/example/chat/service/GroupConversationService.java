package com.example.chat.service;

import com.example.chat.dto.request.GroupConversationRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.entity.Conversation;
import com.example.chat.entity.GroupConversation;
import com.example.chat.entity.Member;
import com.example.chat.enums.*;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.repository.GroupConversationRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupConversationService {
    GroupConversationRepository groupConversationRepository;
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
    public Conversation createGroup(String ownerId, GroupConversationRequest request) {
        String generateCode = CodeGenerator.generateShortCode();

        if (!userService.checkParticipantsValid(request.getParticipantsIds())) {
            throw new AppException(ErrorCode.PARTICIPANT_INVALID);
        }

        friendService.validateFriendships(ownerId, request.getParticipantsIds());

        GroupConversation conversation = new GroupConversation();
        conversation.setCode(generateCode);
        conversation.setName(request.getName());
        conversation.setOwnerId(ownerId);

        List<Member> members = request.getParticipantsIds().stream()
                .map(userId -> Member.builder()
                        .userId(userId)
                        .roles(List.of(MemberRole.MEMBER.name()))
                        .status(MemberStatus.ACTIVE.name())
                        .build())
                .collect(Collectors.toList());

        members.add(Member.builder()
                .userId(ownerId)
                .roles(List.of(MemberRole.OWNER.name(), MemberRole.ADMIN.name()))
                .status(MemberStatus.ACTIVE.name())
                .build());

        conversation.setParticipants(members);
        conversation.setType(ConversationType.GROUP.name());
        conversation.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        conversation.setStatus(GroupStatus.ACTIVE.name());

        return conversationRepository.save(conversation);
    }

    public void groupCreationEvents(String conversationId, String ownerId, List<String> memberIds) {
        chatService.systemMessage(conversationId, ActionType.CREATE_GROUP.name(), ownerId, null, null);

        for (String userId : memberIds) {
            chatService.systemMessage(conversationId, ActionType.ADD_MEMBER.name(), ownerId, userId, null);
        }
    }

    public void addUserToGroup(String conversationId, String actorId, List<String> participantsIds) {
        if (!userService.checkParticipantsValid(participantsIds)) {
            throw new AppException(ErrorCode.PARTICIPANT_INVALID);
        }

        GroupConversation conversation = this.getGroupConversationById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        List<String> currentParticipants = conversation.getParticipants().stream()
                .map(Member::getUserId)
                .toList();

        if (!currentParticipants.contains(actorId)) {
            throw new AppException(ErrorCode.ACTOR_INVALID);
        }

        friendService.validateFriendships(actorId, participantsIds);

        boolean hasDuplicate = participantsIds.stream().anyMatch(currentParticipants::contains);
        if (hasDuplicate) {
            throw new AppException(ErrorCode.PARTICIPANT_ALREADY_EXISTS);
        }

        List<Member> members = participantsIds.stream()
                .map(userId -> Member.builder()
                        .userId(userId)
                        .roles(List.of(MemberRole.MEMBER.name()))
                        .status(MemberStatus.ACTIVE.name())
                        .build())
                .collect(Collectors.toList());

        conversation.getParticipants().addAll(members);

        for (String userId : participantsIds) {
            MessageResponse systemMsg = chatService.systemMessage(conversationId, ActionType.ADD_MEMBER.name(), actorId, userId, null);

            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId,
                    systemMsg
            );
        }

        conversationRepository.save(conversation);
    }

    public void joinGroup(String conversationId, String userId) {
        GroupConversation conversation = this.getGroupConversationById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        List<String> currentParticipants = conversation.getParticipants().stream()
                .map(Member::getUserId)
                .collect(Collectors.toList());

        if (currentParticipants.contains(userId)) {
            throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
        }

        MessageResponse systemMsg = chatService.systemMessage(conversationId, ActionType.JOIN_GROUP.name(), userId, null, null);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                systemMsg
        );

        conversation.getParticipants().add(Member.builder()
                        .userId(userId)
                        .roles(List.of(MemberRole.MEMBER.name()))
                        .status(MemberStatus.ACTIVE.name())
                .build());

        conversationRepository.save(conversation);
    }

    public void leaveGroup(String conversationId, String userId) {
        GroupConversation conversation = this.getGroupConversationById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        List<String> currentParticipants = conversation.getParticipants().stream()
                .map(Member::getUserId)
                .collect(Collectors.toList());

        if (!currentParticipants.contains(userId)) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        MessageResponse systemMsg = chatService.systemMessage(conversationId, ActionType.LEAVE_GROUP.name(), userId, null, null);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                systemMsg
        );

        conversation.getParticipants().forEach(p -> {
            if (p.getUserId().equals(userId)) {
                p.setStatus(MemberStatus.LEFT.name());
            }
        });

        conversationRepository.save(conversation);
    }

    public GroupConversation getGroupConversationById(String id) {
        return groupConversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXITED));
    }
}