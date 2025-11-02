package com.example.chat.dto.response.friend;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendResponse {
    String id;
//    String fullName;
//    String avatar;
}