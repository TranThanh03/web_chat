package com.example.chat.controller;

import com.example.chat.dto.request.PasswordCreationRequest;
import com.example.chat.dto.request.UserCreationRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.service.AuthenticationService;
import com.example.chat.service.UserService;
import com.example.chat.util.TokenUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    AuthenticationService authenticationService;

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
    ResponseEntity<ApiResponse<String>> createUser(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody PasswordCreationRequest request) {
        String token = TokenUtils.extractToken(authHeader);
        String id = authenticationService.getIdUserByToken(token);

        userService.createPassword(id, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1101)
                .message("Tạo mật khẩu thành công.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
