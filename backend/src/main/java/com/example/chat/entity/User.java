package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
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
