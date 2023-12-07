package com.manilov.taskmanager.security.util;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecretKeyGenerator {

    public static String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secretKey = new byte[128];
        secureRandom.nextBytes(secretKey);
        return Base64.getEncoder().encodeToString(secretKey);
    }
}

