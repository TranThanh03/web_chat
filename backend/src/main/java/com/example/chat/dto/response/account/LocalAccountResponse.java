package com.example.chat.dto.response.account;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalAccountResponse {
    String id;
    String uid;
    String firstName;
    String lastName;
    String fullName;
    String email;
    LocalDate dateOfBirth;
    String provider;
    String status;
    List<String> roles;
    Long createAt;
}