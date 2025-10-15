package com.example.chat.repository;

import com.example.chat.entity.UserConversation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserConversationRepository extends MongoRepository<UserConversation, String> {
    UserConversation findByUserIdAndConversationId(String userId, String conversationId);
}