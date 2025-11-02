package com.example.chat.repository;

import com.example.chat.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findTop20ByConversationIdOrderByIdDesc(String conversationId);

    List<Message> findTop20ByConversationIdAndIdLessThanOrderByIdDesc(String conversationId, ObjectId messageId);

    List<Message> findTop20ByConversationIdAndTimeStampLessThanEqualOrderByIdDesc(String conversationId, Long restrictedAt);

    List<Message> findTop20ByConversationIdAndTimeStampGreaterThanOrderByIdDesc(String conversationId, Long timeStamp);

    @Query(value = "{ 'conversationId': ?0, 'timeStamp': { $gt: ?1, $lte: ?2 } }", sort = "{ '_id': -1 }")
    List<Message> findTop20Between(String conversationId, Long deletedAt, Long restrictedAt);

    List<Message> findTop20ByConversationIdAndTimeStampGreaterThanAndIdLessThanOrderByIdDesc(String conversationId, Long timeStamp, ObjectId messageId);

    List<Message> findTop20ByConversationIdAndTimeStampLessThanEqualAndIdLessThanOrderByIdDesc(String conversationId, Long restrictedAt, ObjectId id);

    @Query(value = "{ 'conversationId': ?0, 'timeStamp': { $gt: ?1, $lte: ?2 }, '_id': { $lt: ?3 } }", sort = "{ '_id': -1 }")
    List<Message> findTop20BetweenWithIdLessThan(String conversationId, Long deletedAt, Long restrictedAt, ObjectId id);
}
