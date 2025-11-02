package com.example.chat.dto.request.attachment;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadSignatureRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    String conversationId;

    @NotNull(message = "FIELD_NOT_NULL")
    @Size(min = 1, max = 10, message = "ATTACHMENT_LIMIT_EXCEEDED")
    List<UploadItemsRequest> items;
}