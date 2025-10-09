package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.service.MessageService;
import com.example.chat.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {
    MessageService messageService;
    CustomSecurity customSecurity;
    UserService userService;

    @GetMapping("/{conversationId}/latest")
    ResponseEntity<ApiResponse<List<MessageResponse>>> getLatestMessages(
            Authentication authentication,
            @PathVariable String conversationId) {

        String id = customSecurity.getUserId(authentication);

        userService.verifyActiveAccount(id);

        ApiResponse<List<MessageResponse>> apiResponse = ApiResponse.<List<MessageResponse>>builder()
                .code(1300)
                .result(messageService.getLatestMessages(conversationId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{conversationId}/before")
    ResponseEntity<ApiResponse<List<MessageResponse>>> getMoreMessages(
            Authentication authentication,
            @PathVariable String conversationId,
            @RequestParam String messageId) {

        String id = customSecurity.getUserId(authentication);

        userService.verifyActiveAccount(id);

        ApiResponse<List<MessageResponse>> apiResponse = ApiResponse.<List<MessageResponse>>builder()
                .code(1301)
                .result(messageService.getMoreMessages(conversationId, messageId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
