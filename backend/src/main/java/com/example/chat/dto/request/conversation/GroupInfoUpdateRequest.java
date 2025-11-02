package com.example.chat.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupInfoUpdateRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    @Size(min = 3, max = 50, message = "GROUP_NAME_LENGTH_INVALID")
    String groupName;

    @NotBlank(message = "FIELD_NOT_BLANK")
    String groupAvatar;
}
