package com.example.chat.dto.response.attachment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentSummaryResponse {
    String secureUrl;
    String type;
    String format;
}