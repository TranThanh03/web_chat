package com.example.chat.service;

import com.example.chat.dto.request.GroupConversationRequest;
import com.example.chat.dto.request.GroupInfoUpdateRequest;
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
        conversation.setGroupName(request.getGroupName());
        conversation.setOwnerId(ownerId);

        List<Member> members = request.getParticipantsIds().stream()
                .map(userId -> Member.builder()
                        .userId(userId)
                        .roles(MemberRole.MEMBER.name())
                        .status(MemberStatus.ACTIVE.name())
                        .build())
                .collect(Collectors.toList());

        members.add(Member.builder()
                .userId(ownerId)
                .roles(MemberRole.ADMIN.name())
                .status(MemberStatus.ACTIVE.name())
                .build());

        conversation.setParticipants(members);
        conversation.setType(ConversationType.GROUP.name());
        conversation.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        conversation.setStatus(ConversationStatus.ACTIVE.name());

        return conversationRepository.save(conversation);
    }

    public void groupCreationEvents(String conversationId, String ownerId, List<String> memberIds) {
        chatService.systemMessage(conversationId, GroupActionType.CREATE_GROUP.name(), ownerId, null, null);

        for (String userId : memberIds) {
            chatService.systemMessage(conversationId, GroupActionType.ADD_MEMBER.name(), ownerId, userId, null);
        }
    }

    public void addMember(String conversationId, String actorId, List<String> participantsIds) {
        if (!userService.checkParticipantsValid(participantsIds)) {
            throw new AppException(ErrorCode.PARTICIPANT_INVALID);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ACTOR_INVALID));

        friendService.validateFriendships(actorId, participantsIds);

        List<String> currentParticipants = conversation.getParticipants().stream()
                .filter(p -> p.getStatus().equals(MemberStatus.ACTIVE.name()))
                .map(Member::getUserId)
                .collect(Collectors.toList());

        boolean hasDuplicate = participantsIds.stream().anyMatch(currentParticipants::contains);
        if (hasDuplicate) {
            throw new AppException(ErrorCode.PARTICIPANT_ALREADY_EXISTS);
        }

        List<Member> newMembers = new ArrayList<>();
        Map<String, Member> memberMap = conversation.getParticipants().stream()
                .filter(m -> !m.getStatus().equals(MemberStatus.ACTIVE.name()))
                .collect(Collectors.toMap(Member::getUserId, m -> m, (a, b) -> a));

        for (String userId : participantsIds) {
            Member existing = memberMap.get(userId);

            if (existing != null) {
                existing.setStatus(MemberStatus.ACTIVE.name());
            } else {
                Member newMember = Member.builder()
                        .userId(userId)
                        .roles(MemberRole.MEMBER.name())
                        .status(MemberStatus.ACTIVE.name())
                        .build();
                newMembers.add(newMember);
            }
        }

        conversation.getParticipants().addAll(newMembers);
        conversationRepository.save(conversation);

        for (String userId : participantsIds) {
            MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.ADD_MEMBER.name(), actorId, userId, null);

            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId,
                    systemMsg
            );
        }
    }

    public void joinGroup(String conversationCode, String actorId) {
        GroupConversation conversation = getActivePublicGroupByCode(conversationCode);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        Optional<Member> memberOpt = conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId))
                .findFirst();

        if (memberOpt.isEmpty()) {
            conversation.getParticipants().add(Member.builder()
                    .userId(actorId)
                    .roles(MemberRole.MEMBER.name())
                    .status(MemberStatus.ACTIVE.name())
                    .build());

            conversationRepository.save(conversation);
        } else {
            Member member = memberOpt.get();

            if (member.getStatus().equals(MemberStatus.ACTIVE.name())) {
                throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
            } else if (member.getStatus().equals(MemberStatus.LEFT.name())) {
                member.setStatus(MemberStatus.ACTIVE.name());
                conversationRepository.save(conversation);
            } else {
                throw new AppException(ErrorCode.CANNOT_JOIN_GROUP);
            }
        }

        String conversationId = conversation.getId();
        MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.JOIN_GROUP.name(), actorId, null, null);
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                systemMsg
        );
    }

    public void leaveGroup(String conversationId, String actorId) {
        GroupConversation conversation = getActiveGroupById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        Member member = conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        Member promotedMember = null;

        if (member.getRoles().equals(MemberRole.ADMIN.name())) {
            List<Member> activeAdmins = conversation.getParticipants().stream()
                    .filter(m -> m.getStatus().equals(MemberStatus.ACTIVE.name())
                            && m.getRoles().equals(MemberRole.ADMIN.name()))
                    .collect(Collectors.toList());

            if (activeAdmins.size() == 1) {
                promotedMember = conversation.getParticipants().stream()
                        .filter(m -> m.getStatus().equals(MemberStatus.ACTIVE.name())
                                && m.getRoles().equals(MemberRole.MEMBER.name()))
                        .findFirst()
                        .orElse(null);

                if (promotedMember != null) {
                    promotedMember.setRoles(MemberRole.ADMIN.name());
                } else {
                    conversation.setPublic(false);
                    conversation.setStatus(ConversationStatus.DISBANDED.name());
                }
            }
        }

        member.setStatus(MemberStatus.LEFT.name());
        member.setRoles(MemberRole.MEMBER.name());

        conversationRepository.save(conversation);

        if (promotedMember != null) {
            MessageResponse promoteMsg = chatService.systemMessage(conversationId, GroupActionType.PROMOTE_ADMIN.name(), actorId, promotedMember.getUserId(), null);
            messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, promoteMsg);
        }

        MessageResponse leaveMsg = chatService.systemMessage(conversationId, GroupActionType.LEAVE_GROUP.name(), actorId, null, null);
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, leaveMsg);
    }

    public void removeMember(String conversationId, String actorId, String userId) {
        if (actorId.equals(userId)) {
            throw new AppException(ErrorCode.NOT_SELF_ACTION);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        Member member = conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(userId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name())
                        && m.getRoles().equals(MemberRole.ADMIN.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        member.setStatus(MemberStatus.REMOVED.name());
        member.setRoles(MemberRole.MEMBER.name());
        conversationRepository.save(conversation);

        MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.REMOVE_MEMBER.name(), actorId, userId, null);
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                systemMsg
        );
    }

    public void promoteAdmin(String conversationId, String actorId, String userId) {
        if (actorId.equals(userId)) {
            throw new AppException(ErrorCode.NOT_SELF_ACTION);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        Member member = conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(userId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name())
                        && m.getRoles().equals(MemberRole.MEMBER.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name())
                        && m.getRoles().equals(MemberRole.ADMIN.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        member.setRoles(MemberRole.ADMIN.name());
        conversationRepository.save(conversation);

        MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.PROMOTE_ADMIN.name(), actorId, userId, null);
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                systemMsg
        );
    }

    public void revokeAdmin(String conversationId, String actorId, String userId) {
        if (actorId.equals(userId)) {
            throw new AppException(ErrorCode.NOT_SELF_ACTION);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        Member member = conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(userId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name())
                        && m.getRoles().equals(MemberRole.ADMIN.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name())
                        && m.getRoles().equals(MemberRole.ADMIN.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        member.setRoles(MemberRole.MEMBER.name());
        conversationRepository.save(conversation);

        MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.REVOKE_ADMIN.name(), actorId, userId, null);
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                systemMsg
        );
    }

    public void disbandGroup(String conversationId, String actorId) {
        GroupConversation conversation = getActiveGroupById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
        }

        conversation.getParticipants().stream()
                .filter(m -> m.getUserId().equals(actorId)
                        && m.getStatus().equals(MemberStatus.ACTIVE.name())
                        && m.getRoles().equals(MemberRole.ADMIN.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (!conversation.getStatus().equals(ConversationStatus.ACTIVE.name())) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_ACTIVE);
        }

        conversation.setStatus(ConversationStatus.DISBANDED.name());
        conversationRepository.save(conversation);

        MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.DISBAND_GROUP.name(), actorId, null, null);
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                systemMsg
        );
    }

    public GroupConversation getGroupById(String id) {
        return groupConversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXITED));
    }

    public GroupConversation getActiveGroupById(String id) {
        GroupConversation groupConversation = groupConversationRepository.findByIdAndStatus(id, ConversationStatus.ACTIVE.name());

        if (groupConversation == null) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_EXITED);
        }

        return groupConversation;
    }

    public GroupConversation getActivePublicGroupByCode(String code) {
        GroupConversation groupConversation = groupConversationRepository.findByCodeAndStatusAndIsPublic(code, ConversationStatus.ACTIVE.name(), true);

        if (groupConversation == null) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_EXITED);
        }

        return groupConversation;
    }

    public void validateActiveMemberInGroup(String id, String userId) {
        GroupConversation conversation = getGroupById(id);

        conversation.getParticipants().stream()
                .filter(p -> p.getUserId().equals(userId)
                        && p.getStatus().equals(MemberStatus.ACTIVE.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));
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

        conversation.getParticipants().stream()
                .filter(p -> p.getUserId().equals(userId)
                        && p.getStatus().equals(MemberStatus.ACTIVE.name()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_GROUP));

        if (!conversation.getStatus().equals(ConversationStatus.ACTIVE.name())) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_ACTIVE);
        }
    }

    public void changeGroupInfo(String conversationId, String actorId, GroupInfoUpdateRequest request) {
        if (request.getGroupName().isBlank() && request.getGroupAvatar().isBlank()) {
            throw new AppException(ErrorCode.NOT_BLANK);
        }

        GroupConversation conversation = getActiveGroupById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
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

            MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.RENAME_GROUP.name(), actorId, null, null);
            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId,
                    systemMsg
            );
        }

        if (!request.getGroupAvatar().isBlank()) {
            conversation.setGroupAvatar(request.getGroupAvatar());
            conversationRepository.save(conversation);

            MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.CHANGE_AVATAR.name(), actorId, null, null);
            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId,
                    systemMsg
            );
        }
    }

    public void changeGroupVisibility(String conversationId, String actorId, boolean isPublic) {
        GroupConversation conversation = getActiveGroupById(conversationId);

        if (ConversationType.SINGLE.name().equals(conversation.getType())) {
            throw new AppException(ErrorCode.SINGLE_CONVERSATION);
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

            MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.CHANGE_TO_PUBLIC.name(), actorId, null, null);
            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId,
                    systemMsg
            );
        } else {
            conversation.setPublic(false);
            conversationRepository.save(conversation);

            MessageResponse systemMsg = chatService.systemMessage(conversationId, GroupActionType.CHANGE_TO_PRIVATE.name(), actorId, null, null);
            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId,
                    systemMsg
            );
        }
    }
}