package com.finance.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 管理用户登录状态的工具类
 */
public class LoginManager {
    private static String currentUsername = "Default User";
    private static String currentPassword = "";
    
    /**
     * 验证用户登录信息
     * @param username 用户名
     * @param password 密码
     * @return 是否验证成功
     */
    public static boolean validateLogin(String username, String password) {
        try {
            // 统一使用target/UserInfo目录保存用户信息
            File userFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + username + ".txt");
            
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
                        currentUsername = username;
                        currentPassword = password;
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
     * 获取当前登录用户的用户名
     * @return 当前用户名
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * 设置当前登录用户的用户名
     * @param username 用户名
     */
    public static void setCurrentUsername(String username) {
        String oldUsername = currentUsername;
        
        try {
            // 统一使用target/UserInfo目录保存用户信息
            File userFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + oldUsername + ".txt");
            
            if (userFile.exists()) {
                // 读取文件内容
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Username: ")) {
                            line = "Username: " + username;
                        }
                        lines.add(line);
                    }
                }
                
                // 直接写回原文件
                try (PrintWriter writer = new PrintWriter(new FileWriter(userFile))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
                currentUsername = username;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 更新用户密码
     * @param newPassword 新密码
     */
    public static void updatePassword(String newPassword) {
        try {
            String hashedPassword = hashPassword(newPassword);
            if (hashedPassword == null) {
                return;
            }
            
            // 统一使用target/UserInfo目录保存用户信息
            File userFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + currentUsername + ".txt");
            
            if (userFile.exists()) {
                // 读取文件内容
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
                
                // 直接写回原文件
                try (PrintWriter writer = new PrintWriter(new FileWriter(userFile))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
                currentPassword = newPassword;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    /**
     * 保存密码到配置文件
     * @param password 要保存的密码
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