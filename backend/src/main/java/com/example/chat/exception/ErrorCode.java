package com.example.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    TOKEN_NOT_EXITED(1001, "Token không tồn tại!", HttpStatus.BAD_REQUEST),

    SERVER_ERROR(1002, "Server error!", HttpStatus.INTERNAL_SERVER_ERROR),

    UNCATEGORIZED_EXCEPTION(1003, "Uncategorized error!", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(1004, "Unauthenticated!", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(1005, "Unauthorized!", HttpStatus.FORBIDDEN),

    TOKEN_INVALID(1006, "Token invalid!", HttpStatus.BAD_REQUEST),

    NOT_BLANK(1007, "Không được bỏ trống!", HttpStatus.BAD_REQUEST),

    NOT_EMPTY(1008, "Không được rỗng!", HttpStatus.BAD_REQUEST),

    FULLNAME_LENGTH_INVALID(1009, "Tên có độ dài khoảng từ 5 đến 40 ký tự!", HttpStatus.BAD_REQUEST),

    FULLNAME_INVALID(1010,"Tên không chứa số hoặc ký tự đặc biệt!", HttpStatus.BAD_REQUEST),

    PHONE_INVALID(1010, "Số điện thoại không đúng định dạng!", HttpStatus.BAD_REQUEST),

    EMAIL_INVALID(1011, "Email không đúng định dạng!", HttpStatus.BAD_REQUEST),

    PASSWORD_INVALID(1011, "Mật khẩu có độ dài khoảng từ 8 đến 40 ký tự!", HttpStatus.BAD_REQUEST),

    PHONE_EXISTED(1012, "Số điện thoại đã tồn tại!", HttpStatus.BAD_REQUEST),

    EMAIL_EXISTED(1013, "Email đã tồn tại!", HttpStatus.BAD_REQUEST),

    USER_NOT_EXITED(1014, "Người dùng không tồn tại!", HttpStatus.BAD_REQUEST),

    PASSWORD_EXISTED(1014, "Mật khẩu đã tồn tại!", HttpStatus.BAD_REQUEST),

    PASSWORD_SAME_AS_OLD(1015, "Mật khẩu mới không được trùng với mật khẩu cũ!", HttpStatus.BAD_REQUEST),

    INCORRECT_CURRENT_PASSWORD(1015, "Mật khẩu hiện tại không đúng!", HttpStatus.BAD_REQUEST),

    LOGIN_FAILED(1014, "Tài khoản hoặc mật khẩu không đúng!", HttpStatus.BAD_REQUEST),

    ACCOUNT_NOT_ACTIVE(1014, "Tài khoản đang không hoạt động!", HttpStatus.BAD_REQUEST),

    ACCOUNT_BANNED(1014, "Tài khoản này đã bị khóa bởi hệ thống!", HttpStatus.BAD_REQUEST),

    NAME_GROUP_LENGTH_INVALID(1012, "Tên nhóm có độ dài khoảng từ 3 đến 50 ký tự!", HttpStatus.BAD_REQUEST),

    PARTICIPANT_GROUP_SIZE_INVALID(1012, "Số người tham gia tối thiểu từ 2 người!", HttpStatus.BAD_REQUEST),

    PARTICIPANT_INVALID(1013, "Người tham gia không hợp lệ!", HttpStatus.BAD_REQUEST),

    CREATOR_CANNOT_BE_PARTICIPANT(1020, "Người tạo không được có trong danh sách người tham gia!", HttpStatus.BAD_REQUEST),

    ACTOR_CANNOT_BE_PARTICIPANT(1020, "Người thêm không được có trong danh sách người tham gia!", HttpStatus.BAD_REQUEST),

    ACTOR_INVALID(1021, "Người thêm không có trong nhóm hội thoại!", HttpStatus.NOT_FOUND),

    PARTICIPANT_ALREADY_EXISTS(1022, "Danh sách người tham gia chứa người đã tham gia nhóm!", HttpStatus.BAD_REQUEST),

    SINGLE_CONVERSATION(2001, "Đây là hội thoại 1-1!", HttpStatus.BAD_REQUEST),

    USER_ALREADY_IN_GROUP(1021, "Người dùng đã tham gia nhóm!", HttpStatus.BAD_REQUEST),

    USER_NOT_IN_GROUP(1021, "Người dùng không có trong nhóm!", HttpStatus.BAD_REQUEST),

    CONVERSATION_NOT_EXITED(1020, "Hội thoại không tồn tại!", HttpStatus.NOT_FOUND),

    CONVERSATION_EXITED(1020, "Hội thoại đã tồn tại!", HttpStatus.NOT_FOUND),

    MESSAGE_ID_INVALID(1020, "Id tin nhắn không hợp lệ!", HttpStatus.BAD_REQUEST),

    CANNOT_SEND_FRIEND(1020, "Không thể gửi lời kết bạn!", HttpStatus.BAD_REQUEST),

    CANNOT_ACCEPT_FRIEND(1020, "Không thể kết bạn!", HttpStatus.BAD_REQUEST),

    CANNOT_REJECT_FRIEND(1020, "Không thể từ chối kết bạn!", HttpStatus.BAD_REQUEST),

    CANNOT_CANCEL_FRIEND(1020, "Không thể hủy kết bạn!", HttpStatus.BAD_REQUEST),

    CANNOT_UNFRIEND_FRIEND(1020, "Không thể hủy bạn bè!", HttpStatus.BAD_REQUEST),

    CANNOT_BLOCK_FRIEND(1020, "Không thể chặn người dùng!", HttpStatus.BAD_REQUEST),

    CANNOT_UNBLOCK_FRIEND(1020, "Không thể bỏ chặn người dùng!", HttpStatus.BAD_REQUEST),

    NOT_FRIENDS(1020, "Không phải là bạn bè!", HttpStatus.BAD_REQUEST);

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