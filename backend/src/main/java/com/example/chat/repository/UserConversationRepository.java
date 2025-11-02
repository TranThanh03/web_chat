package com.example.chat.repository;

import com.example.chat.entity.UserConversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface UserConversationRepository extends MongoRepository<UserConversation, String> {
    UserConversation findByUserIdAndConversationId(String userId, String conversationId);

    @Query("{ 'conversationId': ?0, 'userId': { $in: ?1 } }")
    @Update("{ '$set': { 'restrictedAt': null } }")
    void updateRestrictedAtNullByConversationIdAndUserIds(String conversationId, List<String> userIds);
}