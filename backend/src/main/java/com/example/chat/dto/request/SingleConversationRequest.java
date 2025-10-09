package com.example.chat.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SingleConversationRequest {
    @NotEmpty(message = "NOT_EMPTY")
    String friendId;
}