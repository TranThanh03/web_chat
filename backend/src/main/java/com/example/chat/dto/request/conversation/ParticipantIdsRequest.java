package com.example.chat.dto.request.conversation;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantIdsRequest {
    @NotEmpty(message = "FIELD_NOT_EMPTY")
    List<String> participantIds;
}