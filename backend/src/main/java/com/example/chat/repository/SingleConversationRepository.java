package com.example.chat.repository;

import com.example.chat.entity.SingleConversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SingleConversationRepository extends MongoRepository<SingleConversation, String> {
    @Query(value = "{ 'type': ?1, 'participantIds': { $all: ?0 } }", exists = true)
    boolean existsByParticipantIdsAndType(List<String> participantIds, String type);

    SingleConversation findByIdAndStatus(String id, String status);

    @Query(value = "{ 'type': ?1, 'participantIds': { $all: ?0 } }")
    SingleConversation findByParticipantIdsAndType(List<String> participantIds, String type);
}