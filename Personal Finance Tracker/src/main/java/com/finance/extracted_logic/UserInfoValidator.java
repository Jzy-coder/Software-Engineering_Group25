package com.finance.extracted_logic;

import java.io.File;

public class UserInfoValidator {
    
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";
    
    /**
     * Validates the provided username.
     * Checks for emptiness, maximum length, allowed characters, and if a user file with the same name already exists.
     *
     * @param username the username to validate.
     * @return true if the username is valid and available, false otherwise.
     */
    public static boolean validateUsername(String username) {
        if (username.isEmpty()) {
            return false;
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            return false;
        }
        if (!username.matches(USERNAME_PATTERN)) {
            return false;
        }
        
        File userFile = new File("UserInfo" + File.separator + username + ".txt");
        return !userFile.exists();
    }
    
    /**
     * Validates the provided password and confirms it matches the confirmation password.
     * Checks for emptiness, matching confirmation, and complexity (at least two character types: uppercase, lowercase, digit, underscore).
     *
     * @param password the password to validate.
     * @param confirmPassword the confirmation password to compare against.
     * @return true if the password is valid and matches the confirmation, false otherwise.
     */
    public static boolean validatePassword(String password, String confirmPassword) {
        if (password.isEmpty()) {
            return false;
        }
        if (!password.equals(confirmPassword)) {
            return false;
        }
        
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasUnderscore = password.matches(".*_.*");
        
        int characterTypeCount = (hasUpperCase ? 1 : 0) + 
                                (hasLowerCase ? 1 : 0) + 
                                (hasDigit ? 1 : 0) + 
                                (hasUnderscore ? 1 : 0);
        
        return characterTypeCount >= 2;
    }
    
    /**
     * Validates basic user information fields.
     * Checks if gender, area, and occupation are not empty or contain only whitespace.
     *
     * @param gender the user's gender.
     * @param area the user's area.
     * @param occupation the user's occupation.
     * @return true if all user info fields are valid, false otherwise.
     */
    public static boolean validateUserInfo(String gender, String area, String occupation) {
        return !gender.trim().isEmpty() && !area.trim().isEmpty() && !occupation.trim().isEmpty();
    }
}