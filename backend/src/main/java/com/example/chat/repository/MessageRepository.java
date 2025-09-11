package com.example.chat.repository;

import com.example.chat.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findTop20ByConversationIdOrderByIdDesc(String conversationId);

    List<Message> findTop20ByConversationIdAndIdLessThanOrderByIdDesc(String conversationId, ObjectId messageId);
}
