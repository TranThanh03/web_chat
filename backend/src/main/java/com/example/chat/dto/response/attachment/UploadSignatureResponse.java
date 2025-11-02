package com.example.chat.dto.response.attachment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadSignatureResponse {
    String apiKey;
    String cloudName;
    String folder;
    long timeStamp;
    List<UploadSignatureItem> items;
}