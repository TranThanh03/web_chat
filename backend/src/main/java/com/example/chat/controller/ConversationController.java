package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.AddParticipantsRequest;
import com.example.chat.dto.request.GroupConversationRequest;
import com.example.chat.dto.request.SingleConversationRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.service.ConversationService;
import com.example.chat.service.GroupConversationService;
import com.example.chat.service.SingleConversationService;
import com.example.chat.service.UserService;
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

    @PostMapping("/single")
    ResponseEntity<ApiResponse<String>> createConversation(
            Authentication authentication,
            @Valid @RequestBody SingleConversationRequest request) {

        String ownerId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(ownerId);

        String conversationId = singleConversationService.createSingle(ownerId, request).getId();

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1200)
                .message("Tạo hội thoại mới thành công.")
                .result(conversationId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group")
    ResponseEntity<ApiResponse<String>> createConversationGroup(
            Authentication authentication,
            @Valid @RequestBody GroupConversationRequest request) {

        String ownerId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(ownerId);

        if(request.getParticipantsIds().contains(ownerId)) {
            throw new AppException(ErrorCode.CREATOR_CANNOT_BE_PARTICIPANT);
        }

        String conversationId = groupConversationService.createGroup(ownerId, request).getId();
        groupConversationService.groupCreationEvents(conversationId, ownerId, request.getParticipantsIds());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1201)
                .message("Tạo nhóm mới thành công.")
                .result(conversationId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/add")
    ResponseEntity<ApiResponse<String>> addUserToGroup(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody AddParticipantsRequest request) {

        String actorId = customSecurity.getUserId(authentication);

        if(request.getParticipantsIds().contains(actorId)) {
            throw new AppException(ErrorCode.ACTOR_CANNOT_BE_PARTICIPANT);
        }

        userService.verifyActiveAccount(actorId);
        groupConversationService.addUserToGroup(conversationId, actorId, request.getParticipantsIds());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1202)
                .message("Thêm người tham gia mới thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/join")
    ResponseEntity<ApiResponse<String>> joinGroup(
            Authentication authentication,
            @PathVariable String conversationId) {

        String userId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(userId);
        groupConversationService.joinGroup(conversationId, userId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1203)
                .message("Tham gia nhóm thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/leave")
    ResponseEntity<ApiResponse<String>> leaveGroup(
            Authentication authentication,
            @PathVariable String conversationId) {

        String userId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(userId);
        groupConversationService.leaveGroup(conversationId, userId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1204)
                .message("Rời nhóm thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}