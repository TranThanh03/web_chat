package com.example.chat.controller;

import com.example.chat.dto.request.MessageRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    ChatService chatService;

    @MessageMapping("/send-message/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public MessageResponse sendMessage(
            @DestinationVariable String conversationId,
            @RequestBody MessageRequest request) {

        return chatService.sendMessage(conversationId, request);
    }
}
