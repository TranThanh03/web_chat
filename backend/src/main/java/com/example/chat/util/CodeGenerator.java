package com.example.chat.util;

import java.security.SecureRandom;
import java.util.UUID;

public class CodeGenerator {
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

    public static String generateShortCode() {
        UUID uuid = UUID.randomUUID();
        long l = uuid.getMostSignificantBits();

        return base62Encode(l);
    }

    public static String generateNumericCode() {
        SecureRandom random = new SecureRandom();
        int number = 100000 + random.nextInt(900000);

        return String.valueOf(number);
    }
}