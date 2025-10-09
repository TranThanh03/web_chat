package com.example.chat.entity;

import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "conversations")

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TypeAlias("SINGLE")
@CompoundIndexes({
        @CompoundIndex(name = "type_participantIds_idx", def = "{'type': 1, 'participantIds': 1}")
})
public class SingleConversation extends Conversation {
    List<String> participantIds;
}