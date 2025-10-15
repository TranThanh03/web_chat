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
}
