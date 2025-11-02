package com.example.chat.dto.request.attachment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadItemsRequest {
    @NotBlank(message = "FIELD_NOT_BLANK")
    String fileId;

    @NotBlank(message = "FIELD_NOT_BLANK")
    String fileName;
}