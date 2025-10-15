package com.example.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class GroupConversationRequest {
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 50, message = "NAME_GROUP_LENGTH_INVALID")
    String groupName;

    @NotEmpty(message = "NOT_EMPTY")
    @Size(min = 2, message = "PARTICIPANT_GROUP_SIZE_INVALID")
    List<String> participantsIds;
}