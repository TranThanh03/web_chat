package com.example.chat.repository;

import com.example.chat.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ConversationRepository extends MongoRepository<Conversation, String> {

}