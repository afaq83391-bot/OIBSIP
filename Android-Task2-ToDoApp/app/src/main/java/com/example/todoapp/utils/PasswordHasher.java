package com.example.todoapp.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for hashing passwords with SHA-256 and a static salt.
 * This is basic hashing suitable for local apps — not production-grade security.
 */
public class PasswordHasher {

    // Static salt to prevent simple rainbow table lookups
    private static final String SALT_PREFIX = "tf_2024_";
    private static final String SALT_SUFFIX = "_xk9";

    /**
     * Hashes a plain-text password using SHA-256 with salt.
     * @param password The plain-text password.
     * @return Hex-encoded SHA-256 hash string.
     */
    public static String hash(String password) {
        try {
            String salted = SALT_PREFIX + password + SALT_SUFFIX;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(salted.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to exist on Android
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored hash.
     * @param password   The plain-text password to check.
     * @param storedHash The previously stored hash.
     * @return true if the password matches.
     */
    public static boolean verify(String password, String storedHash) {
        return hash(password).equals(storedHash);
    }
}