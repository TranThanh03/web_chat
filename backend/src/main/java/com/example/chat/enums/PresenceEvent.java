package com.example.chat.enums;

public enum PresenceEvent {
    PRESENCE_HEARTBEAT("presence:heartbeat");

    private final String event;

    PresenceEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
