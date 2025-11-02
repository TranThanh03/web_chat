package com.example.chat.repository;

import com.example.chat.entity.TempAttachment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TempAttachmentRepository extends MongoRepository<TempAttachment, String> {
    Boolean existsByPublicIdAndUserIdAndConversationId(String publicId, String userId, String conversationId);

    @Transactional
    void deleteByPublicId(String publicId);
}