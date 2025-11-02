package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.user.UserInfoUpdateRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.dto.response.user.UserInfoResponse;
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

    @GetMapping("/info")
    ResponseEntity<ApiResponse<UserInfoResponse>> getInfo(Authentication authentication) {
        String id = customSecurity.getUserId(authentication);

        ApiResponse<UserInfoResponse> apiResponse = ApiResponse.<UserInfoResponse>builder()
                .result(userService.getInfoById(id))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/update-info")
    ResponseEntity<ApiResponse<String>> updateInfo(
            Authentication authentication,
            @RequestBody @Valid UserInfoUpdateRequest request
    ) {
        String id = customSecurity.getUserId(authentication);
        userService.updateInfo(id, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Update user info successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
