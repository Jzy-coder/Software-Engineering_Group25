package com.finance.extracted_logic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for authentication-related operations such as password hashing.
 */
public class AuthenticationHelper {

    /**
     * Hashes a password using SHA-256 algorithm.
     * @param password The password to hash.
     * @return The hashed password as a hexadecimal string, or null if hashing fails.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // In a real application, consider logging this exception
            e.printStackTrace();
            return null;
        }
    }
}