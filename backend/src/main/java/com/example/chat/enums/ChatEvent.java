package com.example.chat.enums;

public enum ChatEvent {
    CHAT_JOIN("chat:join"),
    CHAT_EXIT("chat:exit"),
    CHAT_MESSAGE_SEND("chat:message:send"),
    CHAT_MESSAGE_NEW("chat:message:new"),
    CHAT_MESSAGE_UPDATE("chat:message:update"),
    CHAT_MESSAGE_FAILED("chat:message:failed"),
    CHAT_BLOCK("chat:block"),
    CHAT_UNBLOCK("chat:unblock"),
    CHAT_GROUP_JOIN("chat:group:join"),
    CHAT_GROUP_MEMBER_ADD("chat:group:member:add"),
    CHAT_GROUP_MEMBER_REMOVE("chat:group:member:remove"),
    CHAT_GROUP_ADMIN_PROMOTE("chat:group:admin:promote"),
    CHAT_GROUP_ADMIN_REVOKE("chat:group:admin:revoke"),
    CHAT_GROUP_LEAVE("chat:group:leave"),
    CHAT_GROUP_DISBAND("chat:group:disband"),
    CHAT_GROUP_CHANGE_INFO("chat:group:change:info"),
    CHAT_GROUP_CHANGE_VISIBILITY("chat:group:change:visibility");

    private final String event;

    ChatEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
