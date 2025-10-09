package com.example.chat.entity;

import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "conversations")

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TypeAlias("GROUP")
public class GroupConversation extends Conversation {
    String name;
    String groupAvatar;
    List<Member> participants;
    boolean isPublic;
}