package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attachment {
    String publicId;
    String secureUrl;
    String originalFilename;
    String resourceType;
    String format;
    Long bytes;
    Integer width;
    Integer height;
}
