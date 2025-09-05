package com.example.chat.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationRequest {
    String name;

    @NotEmpty(message = "NOT_EMPTY")
    @Size(min = 2, message = "PARTICIPANT_SIZE_INVALID")
    List<String> participants;
}