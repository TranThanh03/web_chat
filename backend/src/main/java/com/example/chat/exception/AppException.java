package com.example.chat.exception;

public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private Object detail;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, Object detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getDetail() {
        return detail;
    }
}
