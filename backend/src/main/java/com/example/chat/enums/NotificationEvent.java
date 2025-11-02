package com.example.chat.enums;

public enum NotificationEvent {
    NOTIFICATION_NEW("notification:new"),
    NOTIFICATION_DIRECT("notification:direct");

    private final String event;

    NotificationEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
