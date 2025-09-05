package com.example.chat.controller;

import com.example.chat.dto.request.ConversationRequest;
import com.example.chat.dto.response.ApiResponse;
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
@CrossOrigin("http://localhost:5173")
public class ConversationController {
    ConversationService conversationService;

    @PostMapping()
    ResponseEntity<ApiResponse<String>> createConversation(@Valid @RequestBody ConversationRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1100)
                .message("Thêm hội thoại mới thành công.")
                .result(conversationService.createConversation(request).getId())
                .build();

        return ResponseEntity.ok(apiResponse);
    }


}
