package com.example.chat.dto.response.attachment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadSignatureItem {
    String fileId;
    String publicId;
    String signature;
}