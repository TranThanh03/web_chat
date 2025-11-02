package com.example.chat.dto.response.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoResponse {
    String id;
    String code;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String avatar;
}