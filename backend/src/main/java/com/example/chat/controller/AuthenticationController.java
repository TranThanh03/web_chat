package com.example.chat.controller;

import com.example.chat.dto.request.AuthenticationRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.dto.response.AuthenticationResponse;
import com.example.chat.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @NonFinal
    @Value("${base.url}")
    protected String BASE_URL;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {

//        if (!recaptchaService.verifyCB(request.getRecaptcha())) {
//            throw new AppException(ErrorCode.RECAPTCHA_FAILED);
//        }

        String token = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .code(1500)
                .result(AuthenticationResponse.builder()
                        .token(token)
                        .build())
                .message("Đăng nhập thành công.")
                .build();
    }

    @GetMapping("/introspect")
    public ApiResponse<Boolean> introspectToken(String token) {
        var result = authenticationService.introspect(token);

        return ApiResponse.<Boolean>builder()
                .code(1501)
                .result(result)
                .build();
    }

    @PostMapping("/outbound")
    public ApiResponse<AuthenticationResponse> outboundAuthenticate(@RequestParam("code") String code) {
        var result = authenticationService.outboundAuthenticate(code);

        return ApiResponse.<AuthenticationResponse>builder()
                .code(1502)
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<String> logout(String token) throws ParseException, JOSEException {
        authenticationService.logout(token);

        return ApiResponse.<String>builder()
                .code(1503)
                .message("Đăng xuất thành công.")
                .build();
    }
}