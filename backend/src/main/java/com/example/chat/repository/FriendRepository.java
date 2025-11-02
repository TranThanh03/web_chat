package com.example.chat.repository;

import com.example.chat.entity.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FriendRepository extends MongoRepository<Friend, String> {
    boolean existsByUserIdAndFriendIdAndActionId(String userId, String friendId, String actionId);

    boolean existsByUserIdAndFriendIdAndStatusNot(String userId, String friendId, String status);

    Friend findByUserIdAndFriendIdAndActionIdNotAndStatus(String userId, String friendId, String actionId, String status);

    Friend findByUserIdAndFriendIdAndStatus(String userId, String friendId, String status);

    Friend findByUserIdAndFriendIdAndActionIdAndStatus(String userId, String friendId, String actionId, String status);

    boolean existsByUserIdAndFriendIdAndStatus(String userId, String friendId, String status);

    List<Friend> findByUserIdAndFriendIdInAndStatus(String userId, List<String> friends, String status);
}
