package com.meetup.hereandnow.core.util;

import com.meetup.hereandnow.core.exception.error.GlobalErrorCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UUIDUtils {

    public static String getUUID() {
        String uuidString = UUID.randomUUID().toString();

        byte[] uuidStringBytes = uuidString.getBytes(StandardCharsets.UTF_8);
        byte[] hashBytes;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            hashBytes = messageDigest.digest(uuidStringBytes);
        } catch (NoSuchAlgorithmException e) {
            throw GlobalErrorCode.INTERNAL_SERVER_ERROR.toException();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(String.format("%02x", hashBytes[i]));
        }

        return sb.toString();
    }
}
