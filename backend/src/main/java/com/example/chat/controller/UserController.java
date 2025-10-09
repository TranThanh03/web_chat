package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.*;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.service.AuthenticationService;
import com.example.chat.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    CustomSecurity customSecurity;

    @PostMapping()
    ResponseEntity<ApiResponse<String>> createUser(@Valid @RequestBody UserCreationRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1100)
                .message("Tạo mới người dùng thành công.")
                .result(userService.createUser(request).getId())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create-password")
    ResponseEntity<ApiResponse<String>> createUser(
            Authentication authentication,
            @Valid @RequestBody PasswordRequest request) {

        String id = customSecurity.getUserId(authentication);

        userService.createPassword(id, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1101)
                .message("Tạo mật khẩu thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/active")
    ResponseEntity<ApiResponse<String>> activeAccount(
            Authentication authentication) {

        String id = customSecurity.getUserId(authentication);

        userService.activeAccount(id);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1102)
                .message("Kích hoạt tài khoản thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/update-info")
    ResponseEntity<ApiResponse<String>> updateUserInfo(
            Authentication authentication,
            @Valid @RequestBody UserInfoUpdateRequest request) {

        String id = customSecurity.getUserId(authentication);

        userService.updateUserInfo(id, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1102)
                .message("Cập nhật thông tin người dùng thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/update-contact")
    ResponseEntity<ApiResponse<String>> updateUserContact(
            Authentication authentication,
            @Valid @RequestBody UserContactUpdateRequest request) {

        String id = customSecurity.getUserId(authentication);

        userService.updateUserContact(id, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1103)
                .message("Cập nhật thông tin liên hệ thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/change-password")
    ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeRequest request) {

        String id = customSecurity.getUserId(authentication);

        userService.changePassword(id, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1104)
                .message("Cập nhật mật khẩu mới thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
