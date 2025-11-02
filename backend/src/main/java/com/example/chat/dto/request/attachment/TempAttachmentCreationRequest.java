package com.example.chat.dto.request.attachment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TempAttachmentCreationRequest {
    String publicId;
    String userId;
    String conversationId;
}