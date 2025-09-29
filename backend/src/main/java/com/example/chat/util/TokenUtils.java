package com.example.chat.util;

import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;

public class TokenUtils {
    public static String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else {
            throw new AppException(ErrorCode.TOKEN_NOT_EXITED);
        }
    }
}