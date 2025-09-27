package com.example.chat.controller;

import com.example.chat.dto.response.ApiResponse;
import com.example.chat.service.FriendService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendController {
    FriendService friendService;

    @PostMapping("/{friendId}/send")
    ResponseEntity<ApiResponse<String>> sendFriend(@PathVariable String friendId) {
        String userId = "68b81abc6a8a294a1a9d2256";

        friendService.sendFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1400)
                .message("Gửi lời kết bạn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{friendId}/accept")
    ResponseEntity<ApiResponse<String>> acceptFriend(@PathVariable String friendId) {
        String userId = "68b9e0b0babfe04dac6fb502";

        friendService.acceptFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1401)
                .message("Chấp nhận kết bạn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{friendId}/reject")
    ResponseEntity<ApiResponse<String>> rejectFriend(@PathVariable String friendId) {
        String userId = "68b81abc6a8a294a1a9d2256";

        friendService.rejectFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1402)
                .message("Từ chối kết bạn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/cancel")
    ResponseEntity<ApiResponse<String>> cancelFriend(@PathVariable String friendId) {
        String userId = "68b9e0b0babfe04dac6fb502";

        friendService.cancelFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1403)
                .message("Hủy lời kết bạn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/unfriend")
    ResponseEntity<ApiResponse<String>> unFriend(@PathVariable String friendId) {
        String userId = "68b9e0b0babfe04dac6fb502";

        friendService.unFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1404)
                .message("Hủy bạn bè thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{friendId}/block")
    ResponseEntity<ApiResponse<String>> blockFriend(@PathVariable String friendId) {
        String userId = "68b9e0b0babfe04dac6fb502";

        friendService.blockFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1405)
                .message("Chặn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{friendId}/unblock")
    ResponseEntity<ApiResponse<String>> unBlockFriend(@PathVariable String friendId) {
        String userId = "68b9e0b0babfe04dac6fb502";

        friendService.unBlockFriend(userId, friendId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1406)
                .message("Bỏ chặn thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
