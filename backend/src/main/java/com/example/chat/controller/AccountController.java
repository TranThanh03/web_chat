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

    @PostMapping("/register")
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
    ResponseEntity<ApiResponse<String>> active(
            Authentication authentication
    ) {
        String id = customSecurity.getAccountId(authentication);

        accountService.active(id);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Account actived successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
