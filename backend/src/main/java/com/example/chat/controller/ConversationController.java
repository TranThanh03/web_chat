package com.example.chat.controller;

import com.example.chat.dto.request.AddParticipantsRequest;
import com.example.chat.dto.request.ConversationRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.service.ConversationService;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {
    ConversationService conversationService;

    @PostMapping()
    ResponseEntity<ApiResponse<String>> createConversation(@Valid @RequestBody ConversationRequest request) {
        String ownerId = "68b81abc6a8a294a1a9d2256";

        if(request.getParticipants().contains(ownerId)) {
            throw new AppException(ErrorCode.CREATOR_INVALID);
        }

        String conversationId = conversationService.createConversation(ownerId, request).getId();

        if (request.getParticipants().size() > 2) {
            conversationService.groupCreationEvents(conversationId, ownerId, request.getParticipants());
        }

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1200)
                .message("Thêm hội thoại mới thành công.")
                .result(conversationId)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{conversationId}/add")
    ResponseEntity<ApiResponse<String>> addUserToGroup(@PathVariable String conversationId, @Valid @RequestBody AddParticipantsRequest request) {
        String addPersonId = "`68b81abc6a8a294a1a9d2256`";

        conversationService.addUserToGroupEvents(conversationId, addPersonId, request.getParticipants());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1201)
                .message("Thêm người tham gia mới thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{conversationId}/join")
    ResponseEntity<ApiResponse<String>> joinGroup(@PathVariable String conversationId, @RequestParam String userId) {
        conversationService.joinGroupEvents(conversationId, userId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1202)
                .message("Tham gia nhóm thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
