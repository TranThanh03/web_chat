package com.example.chat.repository;

import com.example.chat.entity.GroupConversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GroupConversationRepository extends MongoRepository<GroupConversation, String> {
    @Query(value = "{ 'type': ?1, 'participant': { $all: ?0 } }", exists = true)
    boolean existsByParticipantIdsAndType(List<String> participantIds, String type);
}