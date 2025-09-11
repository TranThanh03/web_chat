package com.example.chat.service;

import com.example.chat.entity.Friend;
import com.example.chat.entity.User;
import com.example.chat.enums.AccountStatus;
import com.example.chat.enums.FriendStatus;
import com.example.chat.enums.PresenceStatus;
import com.example.chat.enums.Role;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.FriendRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendService {
    FriendRepository friendRepository;
    UserService userService;

    public Friend sendFriend(String userId, String friendId) {
        if (friendId.equals(userId)) {
            throw new AppException(ErrorCode.CANNOT_SEND_FRIEND);
        }

        userService.validateUserIdExists(friendId);

        if (friendRepository.existsFriend(userId, friendId, FriendStatus.REJECTED.name()).isPresent()) {
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
        if (friendId.equals(userId)) {
            throw new AppException(ErrorCode.CANNOT_ACCEPT_FRIEND);
        }

        userService.validateUserIdExists(userId);

        Friend friend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(userId, friendId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_ACCEPT_FRIEND);
        }

        Friend newFriend = friendRepository.findFriendByMultipleIdsAndStatus(userId, friendId, FriendStatus.REJECTED.name());

        if (newFriend != null) {
            friendRepository.delete(newFriend);
        }

        friend.setStatus(FriendStatus.ACCEPTED.name());
        friend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        friend.setExpireAt(null);

        friendRepository.save(friend);
    }

    public void rejectFriend(String userId, String friendId) {
        if (friendId.equals(userId)) {
            throw new AppException(ErrorCode.CANNOT_REJECT_FRIEND);
        }

        userService.validateUserIdExists(userId);

        Friend friend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(userId, friendId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_REJECT_FRIEND);
        }

        friend.setStatus(FriendStatus.REJECTED.name());
        friend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());
        friend.setExpireAt(Instant.now().plus(Duration.ofDays(10)));

        friendRepository.save(friend);
    }

    public void cancelFriend(String userId, String friendId) {
        if (friendId.equals(userId)) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_FRIEND);
        }

        userService.validateUserIdExists(friendId);

        Friend friend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(userId, friendId, FriendStatus.PENDING.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_FRIEND);
        }

        friendRepository.delete(friend);
    }

    public void unFriend(String userId, String friendId) {
        if (friendId.equals(userId)) {
            throw new AppException(ErrorCode.CANNOT_UNFRIEND_FRIEND);
        }

        userService.validateUserIdExists(friendId);

        Friend friend = friendRepository.findFriendByMultipleIdsAndStatus(userId, friendId, FriendStatus.ACCEPTED.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_UNFRIEND_FRIEND);
        }

        friendRepository.delete(friend);
    }

    public void blockFriend(String userId, String friendId) {
        if (friendId.equals(userId)) {
            throw new AppException(ErrorCode.CANNOT_BLOCK_FRIEND);
        }

        userService.validateUserIdExists(friendId);

        Friend friend = friendRepository.findFriendByMultipleIdsAndStatus(userId, friendId, FriendStatus.ACCEPTED.name());

        if (friend != null) {
            friendRepository.delete(friend);
        }

        Friend newFriend = new Friend();

        newFriend.setUserId(userId);
        newFriend.setFriendId(friendId);
        newFriend.setStatus(FriendStatus.BLOCKED.name());
        newFriend.setActionUserId(userId);
        newFriend.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        newFriend.setUpdatedAt(TimeUtils.toUnixMillisUtcNow());

        friendRepository.save(newFriend);
    }

    public void unBlockFriend(String userId, String friendId) {
        if (friendId.equals(userId)) {
            throw new AppException(ErrorCode.CANNOT_UNBLOCK_FRIEND);
        }

        userService.validateUserIdExists(friendId);

        Friend friend = friendRepository.findFriendByUserIdAndFriendIdAndStatus(userId, friendId, FriendStatus.BLOCKED.name());

        if (friend == null) {
            throw new AppException(ErrorCode.CANNOT_UNBLOCK_FRIEND);
        }

        friendRepository.delete(friend);
    }

    public boolean isFriend(String userId, String anotherId) {
        return friendRepository.areFriends(userId, anotherId, FriendStatus.ACCEPTED.name()).isPresent();
    }

}
