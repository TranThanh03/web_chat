package com.example.chat.configuration;

import com.example.chat.dto.response.ApiResponse;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        Object attr = request.getAttribute("errorCode");
        ErrorCode errorCode;

        if (attr instanceof AppException appEx) {
            errorCode = appEx.getErrorCode();
        } else if (attr instanceof ErrorCode ec) {
            errorCode = ec;
        } else {
            errorCode = ErrorCode.UNAUTHENTICATED;
        }

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        new ObjectMapper().writeValue(response.getWriter(), apiResponse);
        response.flushBuffer();
    }
}
