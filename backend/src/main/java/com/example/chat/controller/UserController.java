package com.example.chat.controller;

import com.example.chat.dto.request.UserRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.service.UserService;
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

    @PostMapping()
    ResponseEntity<ApiResponse<String>> createUser(@Valid @RequestBody UserRequest request) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1100)
                .message("Tạo mới người dùng thành công.")
                .result(userService.createUser(request).getId())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
