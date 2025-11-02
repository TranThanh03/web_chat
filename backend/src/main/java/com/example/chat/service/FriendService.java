package com.example.chat.service;

import com.example.chat.entity.Friend;
import com.example.chat.enums.FriendStatus;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.FriendRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendService {
    FriendRepository friendRepository;
    UserService userService;
    MongoTemplate mongoTemplate;

    public void sendFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        userService.verifyActiveAccount(userId);
        userService.verifyActiveAccount(friendId);

        String[] sortedIds = sortUserIds(userId, friendId);

        if (friendRepository.existsByUserIdAndFriendIdAndActionId(sortedIds[0], sortedIds[1], userId)
            || friendRepository.existsByUserIdAndFriendIdAndStatusNot(sortedIds[0], sortedIds[1], FriendStatus.REJECTED.name())
        ) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_SEND_FAILED);
        }

        Friend friend = new Friend();

        friend.setUserId(sortedIds[0]);
        friend.setFriendId(sortedIds[1]);
        friend.setStatus(FriendStatus.PENDING.name());
        friend.setActionId(userId);
        friend.setCreatedAt(TimeUtils.toUnixMillisUtcNow());

        friendRepository.save(friend);
    }

    @Transactional
    public void acceptFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        userService.verifyActiveAccount(userId);
        userService.verifyActiveAccount(friendId);

        String[] sortedIds = sortUserIds(userId, friendId);
        Friend friend = friendRepository.findByUserIdAndFriendIdAndActionIdNotAndStatus(sortedIds[0], sortedIds[1], userId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_ACCEPT_FAILED);
        }

        friend.setStatus(FriendStatus.ACCEPTED.name());
        friend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        friend.setExpireAt(null);

        friendRepository.save(friend);

        Friend rejectedFriend = friendRepository.findByUserIdAndFriendIdAndStatus(sortedIds[0], sortedIds[1], FriendStatus.REJECTED.name());
        if (rejectedFriend != null) {
            friendRepository.delete(rejectedFriend);
        }
    }

    public void rejectFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        userService.verifyActiveAccount(userId);
        userService.verifyActiveAccount(friendId);

        String[] sortedIds = sortUserIds(userId, friendId);
        Friend friend = friendRepository.findByUserIdAndFriendIdAndActionIdNotAndStatus(sortedIds[0], sortedIds[1], userId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_REJECT_FAILED);
        }

        friend.setStatus(FriendStatus.REJECTED.name());
        friend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        friend.setExpireAt(Instant.now().plus(Duration.ofDays(10)));

        friendRepository.save(friend);
    }

    @Transactional
    public void cancelFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        userService.verifyActiveAccount(userId);
        userService.checkExists(friendId);

        String[] sortedIds = sortUserIds(userId, friendId);
        Friend friend = friendRepository.findByUserIdAndFriendIdAndActionIdAndStatus(sortedIds[0], sortedIds[1], userId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_CANCEL_FAILED);
        }

        friendRepository.delete(friend);
    }

    @Transactional
    public void unFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        userService.verifyActiveAccount(userId);
        userService.checkExists(friendId);

        String[] sortedIds = sortUserIds(userId, friendId);
        Friend friend = friendRepository.findByUserIdAndFriendIdAndStatus(sortedIds[0], sortedIds[1], FriendStatus.ACCEPTED.name());

        if (friend == null) {
            throw new AppException(ErrorCode.UNFRIEND_FAILED);
        }

        friendRepository.delete(friend);
    }

    @Transactional
    public void blockFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        userService.verifyActiveAccount(userId);
        userService.verifyActiveAccount(friendId);

        String[] sortedIds = sortUserIds(userId, friendId);
        Friend friend = friendRepository.findByUserIdAndFriendIdAndStatus(sortedIds[0], sortedIds[1], FriendStatus.ACCEPTED.name());

        if (friend != null) {
            friendRepository.delete(friend);
        }

        Friend newFriend = new Friend();

        newFriend.setUserId(sortedIds[0]);
        newFriend.setFriendId(sortedIds[1]);
        newFriend.setStatus(FriendStatus.BLOCKED.name());
        newFriend.setActionId(userId);
        newFriend.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        newFriend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        newFriend.setExpireAt(null);

        friendRepository.save(newFriend);
    }

    @Transactional
    public void unBlockFriend(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.SELF_ACTION_NOT_ALLOWED);
        }

        userService.verifyActiveAccount(userId);
        userService.checkExists(friendId);

        String[] sortedIds = sortUserIds(userId, friendId);
        Friend friend = friendRepository.findByUserIdAndFriendIdAndActionIdAndStatus(sortedIds[0], sortedIds[1], userId, FriendStatus.BLOCKED.name());

        if (friend == null) {
            throw new AppException(ErrorCode.UNBLOCK_USER_FAILED);
        }

        friendRepository.delete(friend);
    }

    public void validateAreFriends(String userId, String anotherId) {
        String[] sortedIds = sortUserIds(userId, anotherId);

        if (!friendRepository.existsByUserIdAndFriendIdAndStatus(sortedIds[0], sortedIds[1], FriendStatus.ACCEPTED.name())) {
            throw new AppException(ErrorCode.NOT_FRIENDS);
        }
    }

    public void validateFriendships(String ownerId, List<String> memberIds) {
        if (memberIds.isEmpty()) {
            return;
        }

        List<Map<String, String>> pairs = memberIds.stream()
                .map(memberId -> {
                    String[] sorted = sortUserIds(ownerId, memberId);
                    Map<String, String> pair = new HashMap<>();
                    pair.put("userId", sorted[0]);
                    pair.put("friendId", sorted[1]);
                    return pair;
                })
                .toList();

        Criteria[] orCriterias = pairs.stream()
                .map(pair -> Criteria.where("userId").is(pair.get("userId"))
                        .and("friendId").is(pair.get("friendId"))
                        .and("status").is(FriendStatus.ACCEPTED.name()))
                .toArray(Criteria[]::new);

        Query query = new Query(new Criteria().orOperator(orCriterias));
        List<Friend> friendships = mongoTemplate.find(query, Friend.class);

        if (friendships.size() != memberIds.size()) {
            throw new AppException(ErrorCode.NOT_FRIENDS);
        }
    }

    public List<String> getFriends(String ownerId, int page, int size) {
        int pageSize = Math.min(size, 20);
        int skip = page * pageSize;

        Query query = new Query(new Criteria()
                .and("status").is(FriendStatus.ACCEPTED.name())
                .orOperator(
                        Criteria.where("userId").is(ownerId),
                        Criteria.where("friendId").is(ownerId)
                ))
                .skip(skip)
                .limit(pageSize)
                .with(Sort.by(Sort.Direction.DESC, "updatedAt"));

        List<Friend> friendships = mongoTemplate.find(query, Friend.class);

        return friendships.stream()
                .map(f -> f.getUserId().equals(ownerId) ? f.getFriendId() : f.getUserId())
                .toList();
    }

    private static String[] sortUserIds(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return new String[]{userId1, userId2};
        } else {
            return new String[]{userId2, userId1};
        }
    }
}
