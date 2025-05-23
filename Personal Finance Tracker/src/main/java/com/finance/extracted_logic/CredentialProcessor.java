package com.finance.extracted_logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;
import java.util.Base64;

/**
 * Handles credential encryption/decryption and storage operations
 */
public class CredentialProcessor {
    private final String credentialsFile;
    private final String encryptionKey;
    private static final Gson gson = new Gson();

    private static class CredentialEntry {
        String username;
        String encryptedPassword;

        CredentialEntry(String username, String encryptedPassword) {
            this.username = username;
            this.encryptedPassword = encryptedPassword;
        }
    }

    public CredentialProcessor(String credentialsFile, String encryptionKey) {
        this.credentialsFile = credentialsFile;
        this.encryptionKey = encryptionKey;
    }

    public void saveCredentials(String username, String password) {
        try {
            List<CredentialEntry> credentials = loadExistingCredentials();
            credentials.removeIf(entry -> entry.username.equals(username));
            credentials.add(new CredentialEntry(username, encryptPassword(password)));
            
            try (FileWriter writer = new FileWriter(credentialsFile)) {
                gson.toJson(credentials, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSavedUsernames() {
        return loadExistingCredentials().stream()
                .map(entry -> entry.username)
                .collect(java.util.stream.Collectors.toList());
    }

    public String getSavedPassword(String username) {
        return loadExistingCredentials().stream()
                .filter(entry -> entry.username.equals(username))
                .findFirst()
                .map(entry -> decryptPassword(entry.encryptedPassword))
                .orElse(null);
    }

    public void removeCredentials(String username) {
        try {
            List<CredentialEntry> credentials = loadExistingCredentials();
            credentials.removeIf(entry -> entry.username.equals(username));
            
            try (FileWriter writer = new FileWriter(credentialsFile)) {
                gson.toJson(credentials, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String encryptPassword(String password) {
        try {
            byte[] passwordBytes = password.getBytes();
            byte[] keyBytes = encryptionKey.getBytes();
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

    public String decryptPassword(String encryptedPassword) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(encryptedPassword);
            byte[] keyBytes = encryptionKey.getBytes();
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

    private List<CredentialEntry> loadExistingCredentials() {
        File file = new File(credentialsFile);
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
}