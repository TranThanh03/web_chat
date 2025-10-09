package com.example.chat.service;

import com.example.chat.entity.Friend;
import com.example.chat.enums.FriendStatus;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.FriendRepository;
import com.example.chat.util.TimeUtils;
import com.mongodb.client.result.DeleteResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendService {
    FriendRepository friendRepository;
    UserService userService;
    MongoTemplate mongoTemplate;

    public Friend sendFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_SEND_FRIEND);
        }

        userService.verifyActiveAccount(friendId);

        if (friendRepository.existsByUserIdAndFriendId(userId, friendId) || friendRepository.existsByUserIdAndFriendIdAndStatusNot(friendId, userId, FriendStatus.REJECTED.name())) {
            throw new AppException(ErrorCode.CANNOT_SEND_FRIEND);
        }

        Friend friend = new Friend();

        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus(FriendStatus.PENDING.name());
        friend.setActionUserId(userId);
        friend.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        friend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());

        return friendRepository.save(friend);
    }

    public void acceptFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_ACCEPT_FRIEND);
        }

        userService.verifyActiveAccount(friendId);

        Friend friend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(friendId, userId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_ACCEPT_FRIEND);
        }

        friend.setStatus(FriendStatus.ACCEPTED.name());
        friend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        friend.setExpireAt(null);

        friendRepository.save(friend);

        Friend newFriend = new Friend();
        newFriend.setUserId(userId);
        newFriend.setFriendId(friendId);
        newFriend.setStatus(FriendStatus.ACCEPTED.name());
        newFriend.setActionUserId(friendId);
        newFriend.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        newFriend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        newFriend.setExpireAt(null);

        friendRepository.save(newFriend);

        Friend oldFriend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(userId, friendId, FriendStatus.REJECTED.name());

        if (oldFriend != null) {
            friendRepository.delete(oldFriend);
        }
    }

    public void rejectFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_REJECT_FRIEND);
        }

        userService.verifyActiveAccount(friendId);

        Friend friend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(friendId, userId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_REJECT_FRIEND);
        }

        friend.setStatus(FriendStatus.REJECTED.name());
        friend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        friend.setExpireAt(Instant.now().plus(Duration.ofDays(10)));

        friendRepository.save(friend);
    }

    public void cancelFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_FRIEND);
        }

        userService.verifyActiveAccount(friendId);

        Friend friend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(userId, friendId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_FRIEND);
        }

        friendRepository.delete(friend);
    }

    @Transactional
    public void unFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_UNFRIEND_FRIEND);
        }

        userService.verifyActiveAccount(friendId);

        DeleteResult result = deleteFriends(userId, friendId);

        if (result.getDeletedCount() == 0) {
            throw new AppException(ErrorCode.CANNOT_UNFRIEND_FRIEND);
        }
    }

    public void blockFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_BLOCK_FRIEND);
        }

        userService.verifyActiveAccount(friendId);

        deleteFriends(userId, friendId);

        Friend newFriend = new Friend();
        newFriend.setUserId(userId);
        newFriend.setFriendId(friendId);
        newFriend.setStatus(FriendStatus.BLOCKED.name());
        newFriend.setActionUserId(userId);
        newFriend.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        newFriend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        newFriend.setExpireAt(null);

        friendRepository.save(newFriend);
    }

    public void unBlockFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_UNBLOCK_FRIEND);
        }

        userService.verifyActiveAccount(friendId);

        Friend friend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(userId, friendId, FriendStatus.BLOCKED.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_UNBLOCK_FRIEND);
        }

        friendRepository.delete(friend);
    }

    public void validateAreFriends(String userId, String anotherId) {
        if (!friendRepository.existsByUserIdAndFriendIdAndStatus(userId, anotherId, FriendStatus.ACCEPTED.name())) {
            throw new AppException(ErrorCode.NOT_FRIENDS);
        }
    }

    public void validateFriendships(String ownerId, List<String> memberIds) {
        List<Friend> friendships = friendRepository.findByUserIdAndFriendIdInAndStatus(ownerId, memberIds, FriendStatus.ACCEPTED.name());

        Set<String> validFriends = friendships.stream()
                .map(Friend::getFriendId)
                .collect(Collectors.toSet());

        if (!validFriends.containsAll(memberIds)) {
            throw new AppException(ErrorCode.NOT_FRIENDS);
        }
    }

//    public List<ListFriendsResponse> listFriends(String userId) {
//
//    }

    private DeleteResult deleteFriends(String userId, String friendId) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("userId").is(userId)
                        .and("friendId").is(friendId)
                        .and("status").is(FriendStatus.ACCEPTED.name()),
                Criteria.where("userId").is(friendId)
                        .and("friendId").is(userId)
                        .and("status").is(FriendStatus.ACCEPTED.name())
        ));

        return mongoTemplate.remove(query, Friend.class);
    }
}
