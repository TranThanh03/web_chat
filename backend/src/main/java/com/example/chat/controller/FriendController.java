package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.service.FriendService;
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

    @PostMapping("/{friendId}/send")
    ResponseEntity<ApiResponse<String>> sendFriend(
            Authentication authentication,
            @PathVariable String friendId) {

        String userId = customSecurity.getUserId(authentication);

        friendService.sendFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1400)
                .message("Gửi lời kết bạn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{friendId}/accept")
    ResponseEntity<ApiResponse<String>> acceptFriend(
            Authentication authentication,
            @PathVariable String friendId) {

        String userId = customSecurity.getUserId(authentication);

        friendService.acceptFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1401)
                .message("Chấp nhận kết bạn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{friendId}/reject")
    ResponseEntity<ApiResponse<String>> rejectFriend(
            Authentication authentication,
            @PathVariable String friendId) {

        String userId = customSecurity.getUserId(authentication);

        friendService.rejectFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1402)
                .message("Từ chối kết bạn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/cancel")
    ResponseEntity<ApiResponse<String>> cancelFriend(
            Authentication authentication,
            @PathVariable String friendId) {

        String userId = customSecurity.getUserId(authentication);

        friendService.cancelFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1403)
                .message("Hủy lời kết bạn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/unfriend")
    ResponseEntity<ApiResponse<String>> unFriend(
            Authentication authentication,
            @PathVariable String friendId) {

        String userId = customSecurity.getUserId(authentication);

        friendService.unFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1404)
                .message("Hủy bạn bè thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{friendId}/block")
    ResponseEntity<ApiResponse<String>> blockFriend(
            Authentication authentication,
            @PathVariable String friendId) {

        String userId = customSecurity.getUserId(authentication);

        friendService.blockFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1405)
                .message("Chặn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/unblock")
    ResponseEntity<ApiResponse<String>> unBlockFriend(
            Authentication authentication,
            @PathVariable String friendId) {

        String userId = customSecurity.getUserId(authentication);

        friendService.unBlockFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1406)
                .message("Bỏ chặn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
