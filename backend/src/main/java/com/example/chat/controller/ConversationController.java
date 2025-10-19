package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.*;
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

    @PostMapping("/single")
    ResponseEntity<ApiResponse<String>> createSingle(
            Authentication authentication,
            @Valid @RequestBody UserIdRequest request) {

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
    ResponseEntity<ApiResponse<String>> createGroup(
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
    ResponseEntity<ApiResponse<String>> addMember(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody ParticipantIdsRequest request) {

        String actorId = customSecurity.getUserId(authentication);

        if(request.getParticipantIds().contains(actorId)) {
            throw new AppException(ErrorCode.ACTOR_CANNOT_BE_PARTICIPANT);
        }

        userService.verifyActiveAccount(actorId);
        groupConversationService.addMember(conversationId, actorId, request.getParticipantIds());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1202)
                .message("Thêm người tham gia mới thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationCode}/join")
    ResponseEntity<ApiResponse<String>> joinGroup(
            Authentication authentication,
            @PathVariable String conversationCode) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.joinGroup(conversationCode, actorId);

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

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.leaveGroup(conversationId, actorId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1204)
                .message("Rời nhóm thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/remove-member")
    ResponseEntity<ApiResponse<String>> removeMember(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody UserIdRequest request) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        userService.verifyActiveAccount(request.getUserId());
        groupConversationService.removeMember(conversationId, actorId, request.getUserId());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1205)
                .message("Xóa thành viên khỏi nhóm thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/promote-admin")
    ResponseEntity<ApiResponse<String>> promoteAdmin(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody UserIdRequest request) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        userService.verifyActiveAccount(request.getUserId());
        groupConversationService.promoteAdmin(conversationId, actorId, request.getUserId());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1206)
                .message("Thăng cấp người dùng lên quản trị viên thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{conversationId}/revoke-admin")
    ResponseEntity<ApiResponse<String>> revokeAdmin(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody UserIdRequest request) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        userService.verifyActiveAccount(request.getUserId());
        groupConversationService.revokeAdmin(conversationId, actorId, request.getUserId());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1207)
                .message("Thu hồi quyền quản trị viên thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{conversationId}/delete")
    ResponseEntity<ApiResponse<String>> deleteConversation(
            Authentication authentication,
            @PathVariable String conversationId) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        conversationService.deleteConversation(conversationId, actorId);
        userConversationService.handleUserConversation(UserConversationCreationRequest.builder()
                .userId(actorId)
                .conversationId(conversationId)
                .build());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1208)
                .message("Xóa hội thoại thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{conversationId}/restore")
    ResponseEntity<ApiResponse<String>> restoreConversation(
            Authentication authentication,
            @PathVariable String conversationId) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        conversationService.restoreConversation(conversationId, actorId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1209)
                .message("Khôi phục hội thoại thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/group/{conversationId}/change-info")
    ResponseEntity<ApiResponse<String>> changeGroupInfo(
            Authentication authentication,
            @PathVariable String conversationId,
            @RequestBody GroupInfoUpdateRequest request) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.changeGroupInfo(conversationId, actorId, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1210)
                .message("Thay đổi thông tin nhóm thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/group/{conversationId}/visibility")
    ResponseEntity<ApiResponse<String>> changeGroupVisibility(
            Authentication authentication,
            @PathVariable String conversationId,
            @RequestParam boolean isPublic) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.changeGroupVisibility(conversationId, actorId, isPublic);

        String message = isPublic
                ? "Đã chuyển nhóm sang chế độ công khai thành công."
                : "Đã chuyển nhóm sang chế độ riêng tư thành công.";

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1211)
                .message(message)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/group/{conversationId}/disband")
    ResponseEntity<ApiResponse<String>> disbandGroup(
            Authentication authentication,
            @PathVariable String conversationId) {

        String actorId = customSecurity.getUserId(authentication);
        userService.verifyActiveAccount(actorId);
        groupConversationService.disbandGroup(conversationId, actorId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1212)
                .message("Giải tán nhóm thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}