package com.example.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {
    @NotNull(message = "NOT_NULL")
    String conversationId;

    @NotNull(message = "NOT_NULL")
    String senderId;

    String content;

    @NotNull(message = "NOT_NULL")
    String media;
}
