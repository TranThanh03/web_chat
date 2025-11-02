package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "users")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    String id;

    @Indexed(unique = true)
    String accountId;

    @Indexed(unique = true)
    String code;

    String firstName;
    String lastName;
    String fullName;
    String avatar;
    LocalDate dateOfBirth;
    String accountStatus;
}
