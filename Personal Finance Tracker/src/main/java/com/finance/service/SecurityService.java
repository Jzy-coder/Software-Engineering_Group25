package com.finance.service;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecurityService {
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    public boolean verifyPassword(String inputPassword, String storedHash) {
        String inputHash = hashPassword(inputPassword);
        return inputHash.equals(storedHash);
    }
    
    public void savePassword(String username, String password) {
        String hashedPassword = hashPassword(password);
        // 实现密码保存逻辑，建议使用加密存储
        // TODO: 实现安全的密码存储
    }
}
