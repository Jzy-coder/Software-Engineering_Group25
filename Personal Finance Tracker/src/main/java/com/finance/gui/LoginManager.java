package com.finance.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.finance.service.TransactionService;

/**
 * Login state management utility class
 */
public class LoginManager {
    private static String currentUsername = "Default User";
    private static String currentPassword = "";
    
    /**
     * Validate user login information
     * @param username username
     * @param password password
     * @return whether validation is successful
     */
    private static volatile TransactionService transactionService = null;
    
    public static TransactionService getTransactionService() {
        if (transactionService == null) {
            synchronized (LoginManager.class) {
                if (transactionService == null) {
                    transactionService = new com.finance.service.impl.TransactionServiceImpl();
                    transactionService.switchUser(currentUsername, false);
                }
            }
        }
        return transactionService;
    }

    public static boolean validateLogin(String username, String password) {
        try {
            // Use UserInfo directory to store user information
            File userFile = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + username + ".txt");
            
            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                return false;
            }
            
            if (userFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                    String line;
                    String storedPassword = null;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Password: ")) {
                            storedPassword = line.substring("Password: ".length());
                            break;
                        }
                    }
                    
                    if (storedPassword != null && storedPassword.equals(hashedPassword)) {
                        // Clear cache before switching user
                        getTransactionService().clearCache();              
                        currentUsername = username;
                        currentPassword = password;
                        // Switch to current user's transaction data
                        getTransactionService().switchUser(username, false);
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get current logged-in username
     * @return current username
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Set current logged-in username
     * @param username username
     */
    public static void setCurrentUsername(String username) {
        // If username is the same, do nothing
        if (currentUsername.equals(username)) {
            return;
        }
        
        String oldUsername = currentUsername;
        
        try {
            // Get old user file path
            File oldUserFile = new File("UserInfo" + File.separator + oldUsername + ".txt");
            
            // Get new user file path
            File newUserFile = new File("UserInfo" + File.separator + username + ".txt");
            
            // Ensure UserInfo directory exists
            File userInfoDir = new File("UserInfo");
            if (!userInfoDir.exists()) {
                userInfoDir.mkdirs();
            }
            
            // Process user file
            if (oldUserFile.exists()) {
                // Read old file content
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(oldUserFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Username: ")) {
                            line = "Username: " + username;
                        }
                        lines.add(line);
                    }
                }
                
                // Write to new file
                try (PrintWriter writer = new PrintWriter(new FileWriter(newUserFile))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
                
                // If new file is created successfully, delete the old file
                if (newUserFile.exists()) {
                    oldUserFile.delete();
                }
            }
            
            // Clear cache before switching user
            getTransactionService().clearCache();
            // Update current username
            currentUsername = username;
            // Switch to current user's transaction data
            getTransactionService().switchUser(username, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Update user password
     * @param newPassword new password
     */
    public static void updatePassword(String newPassword) {
        try {
            String hashedPassword = hashPassword(newPassword);
            if (hashedPassword == null) {
                return;
            }
            
            // Use UserInfo directory to store user information
            File userFile = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + currentUsername + ".txt");
            
            // Ensure UserInfo directory exists
            File userInfoDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo");
            if (!userInfoDir.exists()) {
                userInfoDir.mkdirs();
            }
            
            // Update user file
            if (userFile.exists()) {
                // Read file content
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Password: ")) {
                            line = "Password: " + hashedPassword;
                        }
                        lines.add(line);
                    }
                }
                
                // Write back to original file
                try (PrintWriter writer = new PrintWriter(new FileWriter(userFile))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
            }
            
            // Update current password
            currentPassword = newPassword;
            
            // Remove saved credentials for this user
            com.finance.util.UserCredentialManager.removeCredentials(currentUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Save password to configuration file
     * @param password password to save
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}