package com.example.chat.repository;

import com.example.chat.entity.GroupConversation;
import com.example.chat.projection.GroupNotifyView;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupConversationRepository extends MongoRepository<GroupConversation, String> {
    GroupConversation findByIdAndStatus(String id, String status);

    GroupConversation findByCodeAndStatusAndIsPublic(String code, String status, boolean isPublic);

    GroupNotifyView findGroupNotifyById(String id);
}