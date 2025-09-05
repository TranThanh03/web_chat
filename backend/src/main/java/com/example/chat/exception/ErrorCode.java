package com.example.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    TOKEN_NOT_EXITED(4449, "Token không tồn tại!", HttpStatus.BAD_REQUEST),

    SERVER_ERROR(1001, "Server error!", HttpStatus.INTERNAL_SERVER_ERROR),

    UNCATEGORIZED_EXCEPTION(1002, "Uncategorized error!", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(1003, "Unauthenticated!", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(1004, "Unauthorized!", HttpStatus.FORBIDDEN),

    TOKEN_INVALID(1005, "Token invalid!", HttpStatus.BAD_REQUEST),

    NOT_NULL(1006, "Không được bỏ trống!", HttpStatus.BAD_REQUEST),

    NOT_EMPTY(1007, "Không được rỗng!", HttpStatus.BAD_REQUEST),

    FULLNAME_LENGTH_INVALID(1008, "Tên có độ dài khoảng từ 5 đến 100 ký tự!", HttpStatus.BAD_REQUEST),

    FULLNAME_INVALID(1009,"Tên không chứa số hoặc ký tự đặc biệt!", HttpStatus.BAD_REQUEST),

    PHONE_INVALID(1010, "Số điện thoại không đúng định dạng!", HttpStatus.BAD_REQUEST),

    EMAIL_INVALID(1011, "Email không đúng định dạng!", HttpStatus.BAD_REQUEST),

    PARTICIPANT_SIZE_INVALID(1012, "Số người tham gia tối thiểu từ 2 người!", HttpStatus.BAD_REQUEST),

    PARTICIPANT_INVALID(1013, "Người tham gia không hợp lệ!", HttpStatus.BAD_REQUEST),

    CONVERSATION_NOT_EXITED(1020, "Hội thoại không tồn tại!", HttpStatus.NOT_FOUND);

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