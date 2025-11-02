package com.example.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    // ================= SYSTEM (9000+) =================
    INTERNAL_SERVER_ERROR(9000, "Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR(9001, "Unknown error.", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_ERROR(9002, "Redis server error.", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_ALLOWED(9003, "Request method is not supported.", HttpStatus.METHOD_NOT_ALLOWED),
    UNSUPPORTED_MEDIA_TYPE(9004, "Media type is not supported.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    MISSING_PARAMETER(9005, "Missing required parameter.", HttpStatus.BAD_REQUEST),

    // ================= COMMON / VALIDATION (1000 - 1099) =================
    INVALID_REQUEST(1000, "Invalid request.", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(1001, "Validation failed.", HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER(1002, "Invalid request parameter.", HttpStatus.BAD_REQUEST),
    FIELD_NOT_BLANK(1003, "Field must not be blank.", HttpStatus.BAD_REQUEST),
    FIELD_NOT_NULL(1004, "Field must not be null.", HttpStatus.BAD_REQUEST),
    FIELD_NOT_EMPTY(1005, "Field must not be empty.", HttpStatus.BAD_REQUEST),
    SELF_ACTION_NOT_ALLOWED(1006, "You cannot perform this action on yourself.", HttpStatus.BAD_REQUEST),
    ACTION_NOT_ALLOWED(1007, "This action is not allowed.", HttpStatus.BAD_REQUEST),
    RECAPTCHA_FAILED(1067, "ReCAPTCHA verification failed.", HttpStatus.BAD_REQUEST),

    // ================= AUTH (1100 - 1199) =================
    TOKEN_NOT_FOUND(1100, "Token not found.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_FOUND(1101, "Refresh token not found.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1102, "Invalid token.", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN(1103, "Invalid refresh token.", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1103, "User is not authenticated.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1103, "You do not have permission to perform this action.", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(1104, "Invalid username or password.", HttpStatus.UNAUTHORIZED),
    INVALID_AUTHORIZATE_CODE(1105, "Invalid or expired authorization code.", HttpStatus.BAD_REQUEST),

    // ================= ACCOUNT (1200 - 1299) =================
    ACCOUNT_NOT_FOUND(1200, "Account not found.", HttpStatus.NOT_FOUND),
    ACCOUNT_NOT_ACTIVE(1201, "Account is not active.", HttpStatus.BAD_REQUEST),
    ACCOUNT_INACTIVATE(1202, "Account is inactivate.", HttpStatus.BAD_REQUEST),
    ACCOUNT_BANNED(1203, "This account has been banned.", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1201, "Email already exists.", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1203, "Invalid email format.", HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH_INVALID(1204, "Password must be between 8 and 32 characters.", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1205, "Incorrect password.", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_CURRENT(1206, "New password must be different from current password.", HttpStatus.BAD_REQUEST),
    NEW_EMAIL_SAME_AS_CURRENT(1206, "New email must be different from current email.", HttpStatus.BAD_REQUEST),

    // ================= USER (1300 - 1399) =================
    USER_NOT_FOUND(1300, "User not found.", HttpStatus.NOT_FOUND),
    FIRSTNAME_LENGTH_INVALID(1301, "First name must be between 1 and 30 characters.", HttpStatus.BAD_REQUEST),
    FIRSTNAME_INVALID(1302, "First name can only contain letters and spaces.", HttpStatus.BAD_REQUEST),
    LASTNAME_LENGTH_INVALID(1303, "Last name must be between 1 and 50 characters.", HttpStatus.BAD_REQUEST),
    LASTNAME_INVALID(1304, "Last name can only contain letters and spaces.", HttpStatus.BAD_REQUEST),
    DATE_OF_BIRTH_INVALID(1301, "Date of birth must be in the past.", HttpStatus.BAD_REQUEST),
    USER_UNDER_13(1302, "User must be at least 13 years old.", HttpStatus.BAD_REQUEST),
    DATE_OF_BIRTH_TOO_OLD(1303, "Date of birth exceeds the allowed age limit.", HttpStatus.BAD_REQUEST),
    CODE_LENGTH_INVALID(1303, "Code must be between 10 and 90 characters.", HttpStatus.BAD_REQUEST),
    CODE_INVALID(1305, "Code can only contain letters, numbers, and dots.", HttpStatus.BAD_REQUEST),
    CODE_ALREADY_EXISTS(1306, "Code already exists.", HttpStatus.BAD_REQUEST),

    // ================= CONVERSATION (1400 - 1499) =================
    CONVERSATION_NOT_FOUND(1400, "Conversation not found.", HttpStatus.NOT_FOUND),
    CONVERSATION_ALREADY_EXISTS(1401, "Conversation already exists.", HttpStatus.BAD_REQUEST),
    CONVERSATION_NOT_ACTIVE(1402, "Conversation is not active.", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_CONVERSATION(1403, "User is not in this conversation.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DELETED_CONVERSATION(1404, "User already deleted this conversation.", HttpStatus.BAD_REQUEST),
    USER_NOT_DELETED_CONVERSATION(1405, "User has not deleted this conversation.", HttpStatus.BAD_REQUEST),
    GROUP_CONVERSATION_REQUIRED(1406, "This action is only available for group conversations.", HttpStatus.BAD_REQUEST),

    // ================= GROUP (1500 - 1599) =================
    GROUP_NAME_LENGTH_INVALID(1500, "Group name must be between 3 and 50 characters.", HttpStatus.BAD_REQUEST),
    GROUP_PARTICIPANT_MIN_SIZE(1501, "Group must contain at least 2 participants.", HttpStatus.BAD_REQUEST),
    PARTICIPANT_INVALID(1502, "Invalid participant.", HttpStatus.BAD_REQUEST),
    PARTICIPANT_ALREADY_EXISTS(1503, "Participant already exists in the group.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_GROUP(1504, "User is already in the group.", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_GROUP(1505, "User is not in the group.", HttpStatus.BAD_REQUEST),
    ADMIN_REQUIRED(1506, "Only group administrators can perform this action.", HttpStatus.FORBIDDEN),
    CREATOR_CANNOT_BE_PARTICIPANT(1507, "Creator cannot appear in participant list.", HttpStatus.BAD_REQUEST),
    ACTOR_CANNOT_BE_PARTICIPANT(1508, "Actor cannot appear in participant list.", HttpStatus.BAD_REQUEST),
    CANNOT_JOIN_GROUP(1509, "Cannot join this group.", HttpStatus.BAD_REQUEST),

    // ================= MESSAGE (1600 - 1699) =================
    MESSAGE_NOT_FOUND(1600, "Message not found.", HttpStatus.NOT_FOUND),
    MESSAGE_ID_INVALID(1005, "Invalid message id.", HttpStatus.BAD_REQUEST),
    MESSAGE_SEND_FAILED(1601, "Failed to send message.", HttpStatus.BAD_REQUEST),
    MESSAGE_EDIT_NOT_ALLOWED(1602, "You cannot edit this message.", HttpStatus.BAD_REQUEST),
    MESSAGE_DELETE_NOT_ALLOWED(1603, "You cannot delete this message.", HttpStatus.BAD_REQUEST),
    MESSAGE_TOO_LARGE(1604, "Message content is too large.", HttpStatus.BAD_REQUEST),

    // ================= FRIEND (1700 - 1799) =================
    FRIEND_REQUEST_SEND_FAILED(1700, "Cannot send friend request.", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_ACCEPT_FAILED(1701, "Cannot accept friend request.", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_REJECT_FAILED(1702, "Cannot reject friend request.", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_CANCEL_FAILED(1703, "Cannot cancel friend request.", HttpStatus.BAD_REQUEST),
    UNFRIEND_FAILED(1704, "Cannot remove friend.", HttpStatus.BAD_REQUEST),
    UNBLOCK_USER_FAILED(1706, "Cannot unblock this user.", HttpStatus.BAD_REQUEST),
    NOT_FRIENDS(1707, "Users are not friends.", HttpStatus.BAD_REQUEST),

    // ================= ATTACHMENT (1800 - 1899) =================
    ATTACHMENT_NOT_FOUND(1800, "Attachment not found.", HttpStatus.NOT_FOUND),
    ATTACHMENT_INVALID_PUBLIC_ID(1801, "Invalid attachment public id.", HttpStatus.BAD_REQUEST),
    ATTACHMENT_METADATA_FETCH_FAILED(1803, "Failed to fetch attachment metadata from Cloudinary.", HttpStatus.BAD_REQUEST),
    ATTACHMENT_METADATA_INVALID(1804, "Attachment metadata is invalid.", HttpStatus.BAD_REQUEST),
    ATTACHMENT_ALREADY_EXISTS(1805, "Attachment already exists.", HttpStatus.BAD_REQUEST),
    ATTACHMENT_SAVE_FAILED(1806, "Failed to save attachment.", HttpStatus.BAD_REQUEST),
    ATTACHMENT_LIMIT_EXCEEDED(1807, "Attachment limit exceeded [1-10].", HttpStatus.BAD_REQUEST),
    ATTACHMENT_NOT_BELONG_TO_CONVERSATION(1808, "Attachment does not belong to this conversation.", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

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