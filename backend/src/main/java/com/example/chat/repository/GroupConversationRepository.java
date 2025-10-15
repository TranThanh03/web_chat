package com.example.chat.repository;

import com.example.chat.entity.GroupConversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GroupConversationRepository extends MongoRepository<GroupConversation, String> {
    GroupConversation findByIdAndStatus(String id, String status);
    GroupConversation findByCodeAndStatusAndIsPublic(String code, String status, boolean isPublic);
}