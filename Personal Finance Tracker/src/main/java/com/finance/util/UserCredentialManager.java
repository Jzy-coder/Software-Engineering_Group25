package com.finance.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;
import java.util.Base64;

/**
 * Manages user credentials for the remember password feature
 * Handles saving and loading of encrypted user credentials
 */
public class UserCredentialManager {
    private static final String CREDENTIALS_FILE = "UserInfo/saved_credentials.json";
    private static final String ENCRYPTION_KEY = "FinanceTrackerSecretKey";
    private static final Gson gson = new Gson();

    /**
     * Represents a stored credential entry
     */
    private static class CredentialEntry {
        String username;
        String encryptedPassword;

        CredentialEntry(String username, String encryptedPassword) {
            this.username = username;
            this.encryptedPassword = encryptedPassword;
        }
    }

    /**
     * Save user credentials if remember password is checked
     * @param username The username to save
     * @param password The password to encrypt and save
     */
    public static void saveCredentials(String username, String password) {
        try {
            List<CredentialEntry> credentials = loadExistingCredentials();
            
            // Remove existing entry for this username if present
            credentials.removeIf(entry -> entry.username.equals(username));
            
            // Add new entry
            String encryptedPassword = encryptPassword(password);
            credentials.add(new CredentialEntry(username, encryptedPassword));
            
            // Save to file
            try (FileWriter writer = new FileWriter(CREDENTIALS_FILE)) {
                gson.toJson(credentials, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load saved credentials from file
     * @return List of saved usernames
     */
    public static List<String> getSavedUsernames() {
        List<CredentialEntry> credentials = loadExistingCredentials();
        return credentials.stream()
                .map(entry -> entry.username)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get saved password for a username
     * @param username The username to look up
     * @return The decrypted password if found, null otherwise
     */
    public static String getSavedPassword(String username) {
        List<CredentialEntry> credentials = loadExistingCredentials();
        return credentials.stream()
                .filter(entry -> entry.username.equals(username))
                .findFirst()
                .map(entry -> decryptPassword(entry.encryptedPassword))
                .orElse(null);
    }

    /**
     * Remove saved credentials for a username
     * @param username The username whose credentials should be removed
     */
    public static void removeCredentials(String username) {
        try {
            List<CredentialEntry> credentials = loadExistingCredentials();
            credentials.removeIf(entry -> entry.username.equals(username));
            
            try (FileWriter writer = new FileWriter(CREDENTIALS_FILE)) {
                gson.toJson(credentials, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load existing credentials from file
     */
    private static List<CredentialEntry> loadExistingCredentials() {
        File file = new File(CREDENTIALS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, new TypeToken<List<CredentialEntry>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Encrypt password using simple XOR encryption
     */
    private static String encryptPassword(String password) {
        try {
            byte[] passwordBytes = password.getBytes();
            byte[] keyBytes = ENCRYPTION_KEY.getBytes();
            byte[] encrypted = new byte[passwordBytes.length];

            for (int i = 0; i < passwordBytes.length; i++) {
                encrypted[i] = (byte) (passwordBytes[i] ^ keyBytes[i % keyBytes.length]);
            }

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt password using simple XOR encryption
     */
    private static String decryptPassword(String encryptedPassword) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(encryptedPassword);
            byte[] keyBytes = ENCRYPTION_KEY.getBytes();
            byte[] decrypted = new byte[encrypted.length];

            for (int i = 0; i < encrypted.length; i++) {
                decrypted[i] = (byte) (encrypted[i] ^ keyBytes[i % keyBytes.length]);
            }

            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}