package com.example.chat.util;

import java.util.UUID;

public class ShortCodeGenerator {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static String base62Encode(long value) {
        StringBuilder sb = new StringBuilder();

        while (value != 0) {
            int index = (int) (Math.abs(value % 62));
            sb.append(BASE62.charAt(index));
            value /= 62;
        }

        return sb.reverse().toString();
    }

    public static String generateShortCodeFromUUID() {
        UUID uuid = UUID.randomUUID();
        long l = uuid.getMostSignificantBits();

        return base62Encode(l);
    }
}

