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

    PHONE_EXISTED(1012, "Số điện thoại đã tồn tại!", HttpStatus.BAD_REQUEST),

    EMAIL_EXISTED(1013, "Email đã tồn tại!", HttpStatus.BAD_REQUEST),

    USER_NOT_EXITED(1014, "Người dùng không tồn tại!", HttpStatus.BAD_REQUEST),

    PARTICIPANT_SIZE_INVALID(1012, "Số người tham gia tối thiểu từ 2 người!", HttpStatus.BAD_REQUEST),

    PARTICIPANT_INVALID(1013, "Người tham gia không hợp lệ!", HttpStatus.BAD_REQUEST),

    CREATOR_INVALID(1020, "Người tạo hội thoại không hợp lệ!", HttpStatus.BAD_REQUEST),

    ADD_PERSON_INVALID(1021, "Người thêm không có trong nhóm hội thoại!", HttpStatus.NOT_FOUND),

    NEW_PARTICIPANTS_EMPTY(1022, "Danh sách người tham gia mới rỗng!", HttpStatus.BAD_REQUEST),

    CANNOT_ADD_PARTICIPANT(1021, "Không thể thêm người dùng vào hội thoại!", HttpStatus.BAD_REQUEST),

    CANNOT_JOIN_GROUP(1021, "Không thể tham gia vào hội thoại!", HttpStatus.BAD_REQUEST),

    CONVERSATION_NOT_EXITED(1020, "Hội thoại không tồn tại!", HttpStatus.NOT_FOUND),

    CANNOT_SEND_FRIEND(1020, "Không thể gửi lời kết bạn!", HttpStatus.BAD_REQUEST),

    CANNOT_ACCEPT_FRIEND(1020, "Không thể kết bạn!", HttpStatus.BAD_REQUEST),

    CANNOT_REJECT_FRIEND(1020, "Không thể từ chối kết bạn!", HttpStatus.BAD_REQUEST),

    CANNOT_CANCEL_FRIEND(1020, "Không thể hủy kết bạn!", HttpStatus.BAD_REQUEST),

    CANNOT_UNFRIEND_FRIEND(1020, "Không thể hủy bạn bè!", HttpStatus.BAD_REQUEST),

    CANNOT_BLOCK_FRIEND(1020, "Không thể chặn người dùng!", HttpStatus.BAD_REQUEST),

    CANNOT_UNBLOCK_FRIEND(1020, "Không thể bỏ chặn người dùng!", HttpStatus.BAD_REQUEST),

    MESSAGE_ID_INVALID(1020, "Id tin nhắn không hợp lệ!", HttpStatus.BAD_REQUEST);

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