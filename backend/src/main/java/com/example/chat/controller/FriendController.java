package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.notification.FriendNotificationRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.enums.NotificationType;
import com.example.chat.service.FriendService;
import com.example.chat.service.NotificationService;
import com.example.chat.service.SingleConversationService;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendController {
    FriendService friendService;
    CustomSecurity customSecurity;
    SingleConversationService singleConversationService;
    NotificationService notificationService;

    @PostMapping("/{friendId}/send")
    ResponseEntity<ApiResponse<String>> sendFriend(
            Authentication authentication,
            @PathVariable String friendId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        friendService.sendFriend(actorId, friendId);
        notificationService.sendFriendNotification(
                FriendNotificationRequest.builder()
                        .actorId(actorId)
                        .userId(friendId)
                        .type(NotificationType.FRIEND_REQUEST.name())
                        .createAt(TimeUtils.toUnixMillisUtcNow())
                        .build()
        );

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Friend request sent successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{friendId}/accept")
    ResponseEntity<ApiResponse<String>> acceptFriend(
            Authentication authentication,
            @PathVariable String friendId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        friendService.acceptFriend(actorId, friendId);
        singleConversationService.handleSingleUnBlock(actorId, friendId);
        notificationService.sendFriendNotification(
                FriendNotificationRequest.builder()
                        .actorId(actorId)
                        .userId(friendId)
                        .type(NotificationType.FRIEND_ACCEPT.name())
                        .createAt(TimeUtils.toUnixMillisUtcNow())
                        .build()
        );

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Friend request accepted successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{friendId}/reject")
    ResponseEntity<ApiResponse<String>> rejectFriend(
            Authentication authentication,
            @PathVariable String friendId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        friendService.rejectFriend(actorId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Friend request rejected successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/cancel")
    ResponseEntity<ApiResponse<String>> cancelFriend(
            Authentication authentication,
            @PathVariable String friendId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        friendService.cancelFriend(actorId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Friend request cancelled successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/unfriend")
    ResponseEntity<ApiResponse<String>> unFriend(
            Authentication authentication,
            @PathVariable String friendId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        friendService.unFriend(actorId, friendId);
        singleConversationService.handleSingleBlock(actorId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Unfriended successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{friendId}/block")
    ResponseEntity<ApiResponse<String>> blockFriend(
            Authentication authentication,
            @PathVariable String friendId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        friendService.blockFriend(actorId, friendId);
        singleConversationService.handleSingleBlock(actorId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("User blocked successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/unblock")
    ResponseEntity<ApiResponse<String>> unBlockFriend(
            Authentication authentication,
            @PathVariable String friendId
    ) {
        String actorId = customSecurity.getUserId(authentication);
        friendService.unBlockFriend(actorId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("User unblocked successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
