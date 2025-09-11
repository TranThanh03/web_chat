package com.example.chat.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String code;
    String fullName;
    String avatar;
    String phone;
    String email;
    String password;
    List<String> roles;
    Long registeredTime;
    String accountStatus;
    String presenceStatus;
}