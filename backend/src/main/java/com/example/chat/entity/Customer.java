package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "customers")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {
    @Id
    String id;

    @Field("code")
    String code;

    @Field("full-name")
    String fullName;

    @Field("phone")
    String phone;

    @Field("email")
    String email;

    @Field("password")
    String password;

    @Field("roles")
    String roles;

    @Field("registered-time")
    Long registeredTime;

    @Field("status")
    String status;
}
