package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.conversation.*;
import com.example.chat.dto.request.user.UserIdRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.service.*;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {
    ConversationService conversationService;
    SingleConversationService singleConversationService;
    GroupConversationService groupConversationService;
    CustomSecurity customSecurity;
    UserService userService;
    UserConversationService userConversationService;
    FriendService friendService;

    @PostMapping("/single")
    ResponseEntity<ApiResponse<String>> createSingle(
            Authentication authentication,
            @Valid @RequestBody UserIdRequest request
    ) {
        String ownerId = customSecurity.getUserId(authentication);
        String userId = request.getUserId();
        userService.verifyActiveAccount(ownerId);
        userService.verifyActiveAccount(userId);
        friendService.validateAreFriends(ownerId, userId);
        singleConversationService.validateConversationNotExists(ownerId, userId);

        String conversationId = singleConversationService.create(ownerId, userId).getId();

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("New conversation created successfully.")
                .result(conversationId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group")
    ResponseEntity<ApiResponse<String>> createGroup(
            Authentication authentication,
            @Valid @RequestBody GroupConversationRequest request
    ) {
        String ownerId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(ownerId);

        if(request.getParticipantsIds().contains(ownerId)) {
            throw new AppException(ErrorCode.CREATOR_CANNOT_BE_PARTICIPANT);
        }

        String conversationId = groupConversationService.create(ownerId, request).getId();
        groupConversationService.creationEvents(conversationId, ownerId, request.getParticipantsIds());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("New group conversation created successfully.")
                .result(conversationId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/add")
    ResponseEntity<ApiResponse<String>> addMember(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody ParticipantIdsRequest request
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);

        if(request.getParticipantIds().contains(actorId)) {
            throw new AppException(ErrorCode.ACTOR_CANNOT_BE_PARTICIPANT);
        }

        groupConversationService.addMember(conversationId, actorId, request.getParticipantIds());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("New participants added successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationCode}/join")
    ResponseEntity<ApiResponse<String>> joinGroup(
            Authentication authentication,
            @PathVariable String conversationCode
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.joinGroup(conversationCode, actorId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Successfully join the group.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/leave")
    ResponseEntity<ApiResponse<String>> leaveGroup(
            Authentication authentication,
            @PathVariable String conversationId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.leaveGroup(conversationId, actorId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Successfully left the group.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/remove-member")
    ResponseEntity<ApiResponse<String>> removeMember(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody UserIdRequest request
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        userService.verifyActiveAccount(request.getUserId());
        groupConversationService.removeMember(conversationId, actorId, request.getUserId());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Member removed from group successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/promote-admin")
    ResponseEntity<ApiResponse<String>> promoteAdmin(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody UserIdRequest request
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        userService.verifyActiveAccount(request.getUserId());
        groupConversationService.promoteAdmin(conversationId, actorId, request.getUserId());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Member promoted to admin successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/revoke-admin")
    ResponseEntity<ApiResponse<String>> revokeAdmin(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody UserIdRequest request
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        userService.verifyActiveAccount(request.getUserId());
        groupConversationService.revokeAdmin(conversationId, actorId, request.getUserId());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Admin privileges revoked successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{conversationId}/delete")
    ResponseEntity<ApiResponse<String>> deleteConversation(
            Authentication authentication,
            @PathVariable String conversationId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        conversationService.deleteConversation(conversationId, actorId);
        userConversationService.handleUserConversationAsDeleted(UserConversationCreationRequest.builder()
                .userId(actorId)
                .conversationId(conversationId)
                .build());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Conversation deleted successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{conversationId}/restore")
    ResponseEntity<ApiResponse<String>> restoreConversation(
            Authentication authentication,
            @PathVariable String conversationId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        conversationService.restoreConversation(conversationId, actorId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Conversation restored successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/group/{conversationId}/change-info")
    ResponseEntity<ApiResponse<String>> changeGroupInfo(
            Authentication authentication,
            @PathVariable String conversationId,
            @RequestBody GroupInfoUpdateRequest request
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.changeInfo(conversationId, actorId, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Group information updated successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/group/{conversationId}/visibility")
    ResponseEntity<ApiResponse<String>> changeGroupVisibility(
            Authentication authentication,
            @PathVariable String conversationId,
            @RequestBody @Valid VisibilityRequest request
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);

        boolean isPublic = request.getIsPublic();
        groupConversationService.changeVisibility(conversationId, actorId, isPublic);

        String message = isPublic
                ? "Group set to public successfully."
                : "Group set to private successfully.";

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message(message)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/group/{conversationId}/disband")
    ResponseEntity<ApiResponse<String>> disbandGroup(
            Authentication authentication,
            @PathVariable String conversationId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.disbandGroup(conversationId, actorId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Group dissolved successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}