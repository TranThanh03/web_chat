package com.example.chat.controller;

import com.example.chat.configuration.CustomSecurity;
import com.example.chat.dto.request.auth.AuthenticationRequest;
import com.example.chat.dto.request.auth.IntrospectRequest;
import com.example.chat.dto.request.auth.OAuthAuthenticateRequest;
import com.example.chat.dto.response.ApiResponse;
import com.example.chat.dto.response.auth.*;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.service.AuthenticationService;
import com.example.chat.service.RecaptchaService;
import com.example.chat.service.RefreshTokenService;
import com.example.chat.util.DomainUtils;
import com.nimbusds.jose.JOSEException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Duration;

@RestController
@RequestMapping("/auth")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    CustomSecurity customSecurity;
    RecaptchaService recaptchaService;
    RefreshTokenService refreshTokenService;

    @NonFinal
    @Value("${base.fe-url}")
    String BASE_FE_URL;

    @NonFinal
    @Value("${jwt.refresh-token-expiration}")
    long JWT_REFRESH_TOKEN_EXPIRATION;

    @PostMapping("/login/local")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody @Valid AuthenticationRequest request,
            HttpServletResponse response
    ) {
        if (!recaptchaService.verifyCB(request.getRecaptcha())) {
            throw new AppException(ErrorCode.RECAPTCHA_FAILED);
        }

        TokenAccountIdResponse result = authenticationService.authenticate(request);

        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder()
                .message("Login successfully.")
                .result(
                        AuthenticationResponse.builder()
                                .token(result.getToken())
                                .build())
                .build();

        String refreshToken = refreshTokenService.generateRefreshToken(result.getAccountId());

        ResponseCookie cookie = ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(DomainUtils.extractDomain(BASE_FE_URL))
                .path("/")
                .maxAge(JWT_REFRESH_TOKEN_EXPIRATION)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login/oauth")
    ResponseEntity<ApiResponse<AuthenticationResponse>> oauthAuthenticate(
            @RequestBody @Valid OAuthAuthenticateRequest request,
            HttpServletResponse response
    ) {
        TokenAccountIdResponse result = authenticationService.oauthAuthenticate(request);

        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder()
                .message("Login successfully.")
                .result(
                        AuthenticationResponse.builder()
                                .token(result.getToken())
                                .build())
                .build();

        String refreshToken = refreshTokenService.generateRefreshToken(result.getAccountId());

        ResponseCookie cookie = ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(DomainUtils.extractDomain(BASE_FE_URL))
                .path("/")
                .maxAge(JWT_REFRESH_TOKEN_EXPIRATION)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/introspect")
    ResponseEntity<ApiResponse<IntrospectResponse>> introspect(
            @RequestBody @Valid IntrospectRequest request
    ) {
        ApiResponse<IntrospectResponse> apiResponse = ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<String>> logout(
            Authentication authentication,
            @CookieValue(value = "refresh-token", required = false) String refreshToken,
            HttpServletResponse response
    ) throws ParseException, JOSEException {
        if (StringUtils.isBlank(refreshToken)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String token = customSecurity.getToken(authentication);

        refreshTokenService.deleteRefreshToken(refreshToken);
        authenticationService.invalidToken(token);

        ResponseCookie cookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(DomainUtils.extractDomain(BASE_FE_URL))
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Logout successfully.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/token/refresh")
    ResponseEntity<ApiResponse<AuthenticationResponse>> refreshAccessToken(
            Authentication authentication,
            @CookieValue(value = "refresh-token", required = false) String refreshToken
    ) throws ParseException, JOSEException {
        if (StringUtils.isBlank(refreshToken)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        InfoAccessTokenResponse infoToken = customSecurity.getInfoToken(authentication);
        if (infoToken == null) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        authenticationService.invalidToken(infoToken.getToken());

        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder()
                .message("Refresh access token successfully.")
                .result(
                        AuthenticationResponse.builder()
                                .token(refreshTokenService.refreshAccessToken(infoToken, refreshToken))
                                .build()
                )
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/socket-token")
    ResponseEntity<ApiResponse<SocketTokenResponse>> generateSocketToken(
            Authentication authentication
    ) {
        String userId = customSecurity.getUserId(authentication);

        ApiResponse<SocketTokenResponse> apiResponse = ApiResponse.<SocketTokenResponse>builder()
                .message("Generate socket token successfully.")
                .result(
                        SocketTokenResponse.builder()
                                .token(authenticationService.generateSocketToken(userId))
                                .build()
                )
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}