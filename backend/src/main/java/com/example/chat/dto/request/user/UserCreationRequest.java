package com.example.chat.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String accountId;
    String firstName;
    String lastName;
    String avatar;
    LocalDate dateOfBirth;
    String accountStatus;
}