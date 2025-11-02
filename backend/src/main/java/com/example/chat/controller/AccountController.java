package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.account.LocalAccountRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.dto.response.account.LocalAccountResponse;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.service.AccountService;
import com.example.chat.service.RecaptchaService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {
    AccountService accountService;
    RecaptchaService recaptchaService;
    CustomSecurity customSecurity;

    @PostMapping()
    ResponseEntity<ApiResponse<LocalAccountResponse>> create(
            @RequestBody @Valid LocalAccountRequest request
    ) {
        if (!recaptchaService.verifyINV(request.getRecaptcha())) {
            throw new AppException(ErrorCode.RECAPTCHA_FAILED);
        }

        ApiResponse<LocalAccountResponse> apiResponse = ApiResponse.<LocalAccountResponse>builder()
                .message("Account created successfully.")
                .result(accountService.createLocal(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/active")
    ResponseEntity<ApiResponse<String>> activeAccount(
            Authentication authentication
    ) {
        String id = customSecurity.getAccountId(authentication);

        accountService.active(id);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Account actived successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
//
//    @PatchMapping("/update-info")
//    ResponseEntity<ApiResponse<String>> updateUserInfo(
//            Authentication authentication,
//            @Valid @RequestBody UserInfoUpdateRequest request) {
//
//        String id = customSecurity.getUserId(authentication);
//
//        userService.updateUserInfo(id, request);
//
//        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
//                .code(1102)
//                .message("Cập nhật thông tin người dùng thành công.")
//                .build();
//
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @PatchMapping("/update-contact")
//    ResponseEntity<ApiResponse<String>> updateUserContact(
//            Authentication authentication,
//            @Valid @RequestBody UserContactUpdateRequest request) {
//
//        String id = customSecurity.getUserId(authentication);
//
//        userService.updateUserContact(id, request);
//
//        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
//                .code(1103)
//                .message("Cập nhật thông tin liên hệ thành công.")
//                .build();
//
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @PatchMapping("/change-password")
//    ResponseEntity<ApiResponse<String>> changePassword(
//            Authentication authentication,
//            @Valid @RequestBody PasswordChangeRequest request) {
//
//        String id = customSecurity.getUserId(authentication);
//
//        userService.changePassword(id, request);
//
//        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
//                .code(1104)
//                .message("Cập nhật mật khẩu mới thành công.")
//                .build();
//
//        return ResponseEntity.ok(apiResponse);
//    }
}
