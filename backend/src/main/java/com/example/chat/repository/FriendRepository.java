package com.example.chat.repository;

import com.example.chat.entity.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FriendRepository extends MongoRepository<Friend, String> {
    boolean existsByUserIdAndFriendId(String userId, String friendId);

    boolean existsByUserIdAndFriendIdAndStatusNot(String friendId, String userId, String status);

    Friend findFriendByUserIdAndFriendIdAndStatus(String friendId, String userId, String status);

    boolean existsByUserIdAndFriendIdAndStatus(String userId, String friendId, String status);

    List<Friend> findByUserIdAndFriendIdInAndStatus(String userId, List<String> friends, String status);

    @Query("{ $or: [ " +
            "{ 'userId': ?0, 'friendId': ?1, 'status': ?2 }, " +
            "{ 'userId': ?1, 'friendId': ?0, 'status': ?2 } " +
            "] }")
    Friend findFriendByMultipleIdsAndStatus(String userId, String friendId, String status);

    @Query("{ $or: [ " +
            "{ 'userId': ?0, 'friendId': ?1, 'status': { $ne: ?2} }, " +
            "{ 'userId': ?1, 'friendId': ?0, 'status': { $ne: ?2} } " +
            "] }")
    Friend findFriendByMultipleIdsAndNotStatus(String userId, String friendId, String status);

    @Query("{ $or: [ " +
            "{ 'userId': ?0, 'friendId': ?1 }, " +
            "{ 'userId': ?1, 'friendId': ?0 } " +
            "] }")
    Friend findFriendByMultipleIds(String userId, String friendId);
}
