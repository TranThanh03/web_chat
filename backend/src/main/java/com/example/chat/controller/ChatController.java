package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.MessageRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.service.ChatService;
import com.example.chat.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    ChatService chatService;
    CustomSecurity customSecurity;
    UserService userService;

    @MessageMapping("/send-message/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public MessageResponse sendMessage(
            @DestinationVariable String conversationId,
            Authentication authentication,
            @RequestBody MessageRequest request) {

        String senderId = customSecurity.getUserId(authentication);

        userService.verifyActiveAccount(senderId);

        return chatService.sendMessage(conversationId, senderId, request);
    }
}
