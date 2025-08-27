package com.example.chat.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtils {
    public static long toUnixMillisUtc(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli();
    }

    public static long toUnixMillisUtcNow() {
        LocalDateTime dateTime = LocalDateTime.now();

        return dateTime.atZone(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli();
    }
}