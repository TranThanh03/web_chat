package com.example.chat.repository;

import com.example.chat.entity.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface FriendRepository extends MongoRepository<Friend, String> {
    @Query("{ $or: [ " +
            "{ 'userId': ?0, 'friendId': ?1, 'actionUserId': ?0 }, " +
            "{ 'userId': ?1, 'friendId': ?0, 'status': { $ne: ?2 } } " +
            "] }")
    Optional<Friend> existsFriend(String userId, String friendId, String status);

    Friend findFriendByUserIdAndFriendIdAndStatus(String userId, String friendId, String status);

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

    @Query("{ $or: [ " +
            "{ 'userId': ?0, 'friendId': ?1, 'status': ?2 }, " +
            "{ 'userId': ?1, 'friendId': ?0, 'status': ?2 } " +
            "] }")
    Optional<Friend> areFriends(String userId, String friendId, String status);
}
