package com.example.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "conversations")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeAlias("BASE")
public class Conversation {
    @Id
    String id;

    @Indexed(unique = true)
    String code;

    String ownerId;
    String type;
    Long createdAt;
    Long updatedAt;
    String status;
    List<String> deletedByUserIds;
}