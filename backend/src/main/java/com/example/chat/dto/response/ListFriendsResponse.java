package com.example.chat.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListFriendsResponse {
    String id;
    String fullName;
    String avatar;
    Long updatedAt;
}