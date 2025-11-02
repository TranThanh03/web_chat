package com.example.chat.service;

import com.example.chat.dto.request.conversation.GroupConversationRequest;
import com.example.chat.dto.request.conversation.GroupInfoUpdateRequest;
import com.example.chat.dto.request.conversation.UserConversationCreationRequest;
import com.example.chat.dto.request.message.SendEventToConversationRequest;
import com.example.chat.dto.response.message.MessageResponse;
import com.example.chat.entity.Conversation;
import com.example.chat.entity.GroupConversation;
import com.example.chat.entity.Member;
import com.example.chat.enums.*;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.projection.GroupNotifyView;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.repository.GroupConversationRepository;
import com.example.chat.util.CodeGenerator;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupConversationService {
    GroupConversationRepository groupConversationRepository;
    ConversationRepository conversationRepository;
    UserService userService;
    ChatService chatService;
    MessageService messageService;
    FriendService friendService;
    UserConversationService userConversationService;

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

        List<Member> members = new ArrayList<>();

        for (String userId : request.getParticipantsIds()) {
            members.add(
                    Member.builder()
                            .userId(userId)
                            .roles(MemberRole.MEMBER.name())
                            .status(MemberStatus.ACTIVE.name())
                            .build()
            );
        }
        members.add(Member.builder()
                .userId(ownerId)
                .roles(MemberRole.ADMIN.name())
                .status(MemberStatus.ACTIVE.name())
                .build());

        GroupConversation conversation = new GroupConversation();

        conversation.setCode(generateCode);
        conversation.setGroupName(request.getGroupName());
        conversation.setOwnerId(ownerId);
        conversation.setParticipants(members);
        conversation.setType(ConversationType.GROUP.name());
        conversation.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        conversation.setStatus(ConversationStatus.ACTIVE.name());

        return conversationRepository.save(conversation);
    }

    public void groupCreationEvents(String conversationId, String ownerId, List<String> memberIds) {
        messageService.systemMessage(conversationId, GroupActionType.CREATE_GROUP.name(), ownerId, null, null);

        for (String userId : memberIds) {
            messageService.systemMessage(conversationId, GroupActionType.ADD_MEMBER.name(), ownerId, userId, null);
        }
    }

    public void addMember(String conversationId, String actorId, List<String> participantsIds) {
        if (!userService.checkParticipantsValid(participantsIds)) {
            throw new AppException(ErrorCode.PARTICIPANT_INVALID);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);
        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        boolean isActorValid = false;
        Map<String, Member> memberMap = new HashMap<>();

        for (Member m : conversation.getParticipants()) {
            memberMap.put(m.getUserId(), m);

            if (m.getUserId().equals(actorId) && m.getStatus().equals(MemberStatus.ACTIVE.name())) {
                isActorValid = true;
            }
        }

        if (!isActorValid) {
            throw new AppException(ErrorCode.ACTION_NOT_ALLOWED);
        }

        friendService.validateFriendships(actorId, participantsIds);

        List<Member> newMembers = new ArrayList<>();
        for (String userId : participantsIds) {

            Member existing = memberMap.get(userId);

            if (existing != null) {
                if (existing.getStatus().equals(MemberStatus.ACTIVE.name())) {
                    throw new AppException(ErrorCode.PARTICIPANT_ALREADY_EXISTS);
                }
                existing.setStatus(MemberStatus.ACTIVE.name());
            } else {
                newMembers.add(
                        Member.builder()
                                .userId(userId)
                                .roles(MemberRole.MEMBER.name())
                                .status(MemberStatus.ACTIVE.name())
                                .build()
                );
            }
        }

        conversation.getParticipants().addAll(newMembers);
        conversationRepository.save(conversation);

        userConversationService.batchUnrestrictUsers(conversationId, participantsIds);

        for (String userId : participantsIds) {
            MessageResponse systemMsg = messageService.systemMessage(
                    conversationId,
                    GroupActionType.ADD_MEMBER.name(),
                    actorId,
                    userId,
                    null
            );
            chatService.sendSystemMessage(conversationId, systemMsg);
        }

        chatService.sendEventToConversation(
                SendEventToConversationRequest.builder()
                        .conversationId(conversationId)
                        .event(ChatEvent.CHAT_GROUP_MEMBER_ADD.getEvent())
                        .data(Map.of("participantsIds", participantsIds))
                        .build()
        );
    }

    public void joinGroup(String conversationCode, String actorId) {
        GroupConversation conversation = getActivePublicGroupByCode(conversationCode);
        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        Member foundMember = null;

        for (Member m : conversation.getParticipants()) {
            if (m.getUserId().equals(actorId)) {
                foundMember = m;
                break;
            }
        }

        if (foundMember == null) {
            conversation.getParticipants().add(Member.builder()
                    .userId(actorId)
                    .roles(MemberRole.MEMBER.name())
                    .status(MemberStatus.ACTIVE.name())
                    .build());

            conversationRepository.save(conversation);
        } else {
            if (foundMember.getStatus().equals(MemberStatus.ACTIVE.name())) {
                throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
            } else if (foundMember.getStatus().equals(MemberStatus.LEFT.name())) {
                foundMember.setStatus(MemberStatus.ACTIVE.name());
                conversationRepository.save(conversation);
            } else {
                throw new AppException(ErrorCode.CANNOT_JOIN_GROUP);
            }
        }

        String conversationId = conversation.getId();

        userConversationService.unrestrictUserConversation(
                UserConversationCreationRequest.builder()
                        .conversationId(conversationId)
                        .userId(actorId)
                        .build());

        MessageResponse systemMsg = messageService.systemMessage(
                conversationId,
                GroupActionType.JOIN_GROUP.name(),
                actorId,
                null,
                null
        );

        chatService.sendSystemMessage(conversationId, systemMsg);
        chatService.sendEventToConversation(
                SendEventToConversationRequest.builder()
                        .conversationId(conversationId)
                        .event(ChatEvent.CHAT_GROUP_JOIN.getEvent())
                        .data(Map.of("userId", actorId))
                        .build()
        );
    }

    public void leaveGroup(String conversationId, String actorId) {
        GroupConversation conversation = getActiveGroupById(conversationId);
        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        Member currentMember = null;
        int activeAdminCount = 0;
        Member candidateMember = null;

        for (Member m : conversation.getParticipants()) {
            if (m.getUserId().equals(actorId)
                    && m.getStatus().equals(MemberStatus.ACTIVE.name())) {
                currentMember = m;
            }

            if (m.getStatus().equals(MemberStatus.ACTIVE.name())) {
                if (m.getRoles().equals(MemberRole.ADMIN.name())) {
                    activeAdminCount++;
                }

                if (!m.getUserId().equals(actorId)
                        && m.getRoles().equals(MemberRole.MEMBER.name())
                        && candidateMember == null) {
                    candidateMember = m;
                }
            }
        }

        if (currentMember == null) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        Member promotedMember = null;

        if (currentMember.getRoles().equals(MemberRole.ADMIN.name())) {
            if (activeAdminCount == 1) {
                if (candidateMember != null) {
                    candidateMember.setRoles(MemberRole.ADMIN.name());
                    promotedMember = candidateMember;
                } else {
                    conversation.setPublic(false);
                    conversation.setStatus(ConversationStatus.DISBANDED.name());
                }
            }
        }

        currentMember.setStatus(MemberStatus.LEFT.name());
        currentMember.setRoles(MemberRole.MEMBER.name());
        conversationRepository.save(conversation);

        if (promotedMember != null) {
            MessageResponse promoteMsg = messageService.systemMessage(
                    conversationId,
                    GroupActionType.PROMOTE_ADMIN.name(),
                    actorId,
                    promotedMember.getUserId(),
                    null
            );
            chatService.sendSystemMessage(conversationId, promoteMsg);
        }

        MessageResponse leaveMsg = messageService.systemMessage(
                conversationId,
                GroupActionType.LEAVE_GROUP.name(),
                actorId,
                null,
                null
        );
        userConversationService.handleUserConversationAsRestricted(
                UserConversationCreationRequest.builder()
                        .conversationId(conversationId)
                        .userId(actorId)
                        .build()
        );

        chatService.sendSystemMessage(conversationId, leaveMsg);
        chatService.sendEventToConversation(
                SendEventToConversationRequest.builder()
                        .conversationId(conversationId)
                        .event(ChatEvent.CHAT_GROUP_LEAVE.getEvent())
                        .data(Map.of("userId", actorId))
                        .build()
        );
    }

    public void removeMember(String conversationId, String actorId, String userId) {
        if (actorId.equals(userId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);
        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        Member targetMember = null;
        boolean isActorAdmin = false;

        for (Member m : conversation.getParticipants()) {
            if (m.getUserId().equals(userId) && m.getStatus().equals(MemberStatus.ACTIVE.name())) {
                targetMember = m;
            }

            if (m.getUserId().equals(actorId) && m.getStatus().equals(MemberStatus.ACTIVE.name()) && m.getRoles().equals(MemberRole.ADMIN.name())) {
                isActorAdmin = true;
            }
        }

        if (!isActorAdmin) {
            throw new AppException(ErrorCode.ADMIN_REQUIRED);
        }

        if (targetMember == null) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        targetMember.setStatus(MemberStatus.REMOVED.name());
        targetMember.setRoles(MemberRole.MEMBER.name());
        conversationRepository.save(conversation);

        MessageResponse systemMsg = messageService.systemMessage(
                conversationId,
                GroupActionType.REMOVE_MEMBER.name(),
                actorId,
                userId,
                null
        );
        userConversationService.handleUserConversationAsRestricted(
                UserConversationCreationRequest.builder()
                        .conversationId(conversationId)
                        .userId(userId)
                        .build()
        );

        chatService.sendSystemMessage(conversationId, systemMsg);
        chatService.sendEventToConversation(
                SendEventToConversationRequest.builder()
                        .conversationId(conversationId)
                        .event(ChatEvent.CHAT_GROUP_MEMBER_REMOVE.getEvent())
                        .data(Map.of("userId", userId))
                        .build()
        );
    }

    public void promoteAdmin(String conversationId, String actorId, String userId) {
        if (actorId.equals(userId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);
        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        Member targetMember = null;
        boolean isActorAdmin = false;

        for (Member m : conversation.getParticipants()) {
            if (m.getUserId().equals(userId) && m.getStatus().equals(MemberStatus.ACTIVE.name()) && m.getRoles().equals(MemberRole.MEMBER.name())) {
                targetMember = m;
            }

            if (m.getUserId().equals(actorId) && m.getStatus().equals(MemberStatus.ACTIVE.name()) && m.getRoles().equals(MemberRole.ADMIN.name())) {
                isActorAdmin = true;
            }
        }

        if (!isActorAdmin) {
            throw new AppException(ErrorCode.ADMIN_REQUIRED);
        }

        if (targetMember == null) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        targetMember.setRoles(MemberRole.ADMIN.name());
        conversationRepository.save(conversation);

        MessageResponse systemMsg = messageService.systemMessage(
                conversationId,
                GroupActionType.PROMOTE_ADMIN.name(),
                actorId,
                userId,
                null
        );

        chatService.sendSystemMessage(conversationId, systemMsg);
        chatService.sendEventToConversation(
                SendEventToConversationRequest.builder()
                        .conversationId(conversationId)
                        .event(ChatEvent.CHAT_GROUP_ADMIN_PROMOTE.getEvent())
                        .data(Map.of("userId", userId))
                        .build()
        );
    }

    public void revokeAdmin(String conversationId, String actorId, String userId) {
        if (actorId.equals(userId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);
        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        Member targetMember = null;
        boolean isActorAdmin = false;

        for (Member m : conversation.getParticipants()) {
            if (m.getUserId().equals(userId) && m.getStatus().equals(MemberStatus.ACTIVE.name()) && m.getRoles().equals(MemberRole.ADMIN.name())) {
                targetMember = m;
            }

            if (m.getUserId().equals(actorId) && m.getStatus().equals(MemberStatus.ACTIVE.name()) && m.getRoles().equals(MemberRole.ADMIN.name())) {
                isActorAdmin = true;
            }
        }

        if (!isActorAdmin) {
            throw new AppException(ErrorCode.ADMIN_REQUIRED);
        }

        if (targetMember == null) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }

        targetMember.setRoles(MemberRole.MEMBER.name());
        conversationRepository.save(conversation);

        MessageResponse systemMsg = messageService.systemMessage(
                conversationId,
                GroupActionType.REVOKE_ADMIN.name(),
                actorId,
                userId,
                null
        );

        chatService.sendSystemMessage(conversationId, systemMsg);
        chatService.sendEventToConversation(
                SendEventToConversationRequest.builder()
                        .conversationId(conversationId)
                        .event(ChatEvent.CHAT_GROUP_ADMIN_REVOKE.getEvent())
                        .data(Map.of("userId", userId))
                        .build()
        );
    }

    public void disbandGroup(String conversationId, String actorId) {
        GroupConversation conversation = getActiveGroupById(conversationId);
        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        boolean isActorAdmin = false;

        for (Member m : conversation.getParticipants()) {
            if (m.getUserId().equals(actorId) && m.getStatus().equals(MemberStatus.ACTIVE.name()) && m.getRoles().equals(MemberRole.ADMIN.name())) {
                isActorAdmin = true;
                break;
            }
        }

        if (!isActorAdmin) {
            throw new AppException(ErrorCode.ADMIN_REQUIRED);
        }

        conversation.setStatus(ConversationStatus.DISBANDED.name());
        conversationRepository.save(conversation);

        MessageResponse systemMsg = messageService.systemMessage(
                conversationId,
                GroupActionType.DISBAND_GROUP.name(),
                actorId,
                null,
                null
        );

        chatService.sendSystemMessage(conversationId, systemMsg);
        chatService.sendEventToConversation(
                SendEventToConversationRequest.builder()
                        .conversationId(conversationId)
                        .event(ChatEvent.CHAT_GROUP_DISBAND.getEvent())
                        .build()
        );
    }

    public GroupConversation getGroupById(String id) {
        return groupConversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
    }

    public GroupConversation getActiveGroupById(String id) {
        GroupConversation groupConversation = groupConversationRepository.findByIdAndStatus(id, ConversationStatus.ACTIVE.name());

        if (groupConversation == null) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND);
        }

        return groupConversation;
    }

    public GroupConversation getActivePublicGroupByCode(String code) {
        GroupConversation groupConversation = groupConversationRepository.findByCodeAndStatusAndIsPublic(code, ConversationStatus.ACTIVE.name(), true);

        if (groupConversation == null) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND);
        }

        return groupConversation;
    }

    public void validateUserInGroup(String id, String userId) {
        GroupConversation conversation = getGroupById(id);

        conversation.getParticipants().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

    }

    public void validateActiveMemberInGroupActive(String id, String userId) {
        GroupConversation conversation = getGroupById(id);

        if (!conversation.getStatus().equals(ConversationStatus.ACTIVE.name())) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_ACTIVE);
        }

        for (var p : conversation.getParticipants()) {
            if (p.getUserId().equals(userId) && p.getStatus().equals(MemberStatus.ACTIVE.name())) {
                return;
            }
        }

        throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
    }

    public List<String> getActiveMemberIdInGroup(String id) {
        GroupConversation conversation = getGroupById(id);

        return conversation.getParticipants().stream()
                .filter(p -> p.getStatus().equals(MemberStatus.ACTIVE.name()))
                .map(Member::getUserId)
                .collect(Collectors.toList());
    }

    public void changeGroupInfo(String conversationId, String actorId, GroupInfoUpdateRequest request) {
        if (request.getGroupName().isBlank() && request.getGroupAvatar().isBlank()) {
            throw new AppException(ErrorCode.FIELD_NOT_BLANK);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);

        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name())
                        && m.getRoles().equals(MemberRole.ADMIN.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (!request.getGroupName().isBlank()) {
            conversation.setGroupName(request.getGroupName());
            conversationRepository.save(conversation);

            MessageResponse systemMsg = messageService.systemMessage(conversationId, GroupActionType.RENAME_GROUP.name(), actorId, null, null);
            chatService.sendSystemMessage(conversationId, systemMsg);
        }

        if (!request.getGroupAvatar().isBlank()) {
            conversation.setGroupAvatar(request.getGroupAvatar());
            conversationRepository.save(conversation);

            MessageResponse systemMsg = messageService.systemMessage(conversationId, GroupActionType.CHANGE_AVATAR.name(), actorId, null, null);
            chatService.sendSystemMessage(conversationId, systemMsg);
        }
    }

    public void changeGroupVisibility(String conversationId, String actorId, boolean isPublic) {
        GroupConversation conversation = getActiveGroupById(conversationId);

        if (!ConversationType.GROUP.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.GROUP_CONVERSATION_REQUIRED);
        }

        conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name())
                        && m.getRoles().equals(MemberRole.ADMIN.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (isPublic) {
            conversation.setPublic(true);
            conversationRepository.save(conversation);

            MessageResponse systemMsg = messageService.systemMessage(conversationId, GroupActionType.CHANGE_TO_PUBLIC.name(), actorId, null, null);
            chatService.sendSystemMessage(conversationId, systemMsg);
        } else {
            conversation.setPublic(false);
            conversationRepository.save(conversation);

            MessageResponse systemMsg = messageService.systemMessage(conversationId, GroupActionType.CHANGE_TO_PRIVATE.name(), actorId, null, null);
            chatService.sendSystemMessage(conversationId, systemMsg);
        }
    }

    public GroupNotifyView getGroupNotifyById(String id) {
        return groupConversationRepository.findGroupNotifyById(id);
    }
}