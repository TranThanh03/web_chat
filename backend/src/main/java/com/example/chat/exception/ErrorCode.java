package com.example.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    TOKEN_NOT_EXITED(4449, "Token không tồn tại!", HttpStatus.OK),
    UNCATEGORIZED_EXCEPTION(1001, "Uncategorized error!", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1002, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "Unauthorized!", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(1004, "Token invalid!", HttpStatus.OK),
    ROOM_EXITED(1005, "Phòng đã tồn tại!", HttpStatus.OK),
    ROOM_NOT_EXITED(1006, "Phòng không tồn tại!", HttpStatus.OK),
    ;
    
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}