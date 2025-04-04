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
        // 如果用户名相同，不进行任何操作
        if (currentUsername.equals(username)) {
            return;
        }
        
        String oldUsername = currentUsername;
        
        try {
            // 获取旧用户文件路径
            File oldUserFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + oldUsername + ".txt");
            File oldUserFileInCurrentDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + oldUsername + ".txt");
            
            // 获取新用户文件路径
            File newUserFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + username + ".txt");
            File newUserFileInCurrentDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + username + ".txt");
            
            // 确保UserInfo目录存在
            File targetUserInfoDir = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo");
            File currentUserInfoDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo");
            if (!targetUserInfoDir.exists()) {
                targetUserInfoDir.mkdirs();
            }
            if (!currentUserInfoDir.exists()) {
                currentUserInfoDir.mkdirs();
            }
            
            // 处理target目录下的用户文件
            if (oldUserFile.exists()) {
                // 读取旧文件内容
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(oldUserFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Username: ")) {
                            line = "Username: " + username;
                        }
                        lines.add(line);
                    }
                }
                
                // 写入新文件
                try (PrintWriter writer = new PrintWriter(new FileWriter(newUserFile))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
                
                // 如果新文件创建成功，则删除旧文件
                if (newUserFile.exists()) {
                    oldUserFile.delete();
                }
            }
            
            // 处理当前目录下的用户文件
            if (oldUserFileInCurrentDir.exists()) {
                // 读取旧文件内容
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(oldUserFileInCurrentDir))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Username: ")) {
                            line = "Username: " + username;
                        }
                        lines.add(line);
                    }
                }
                
                // 写入新文件
                try (PrintWriter writer = new PrintWriter(new FileWriter(newUserFileInCurrentDir))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
                
                // 如果新文件创建成功，则删除旧文件
                if (newUserFileInCurrentDir.exists()) {
                    oldUserFileInCurrentDir.delete();
                }
            }
            
            // 更新当前用户名
            currentUsername = username;
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
            File userFileInCurrentDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + currentUsername + ".txt");
            
            // 确保UserInfo目录存在
            File targetUserInfoDir = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo");
            File currentUserInfoDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo");
            if (!targetUserInfoDir.exists()) {
                targetUserInfoDir.mkdirs();
            }
            if (!currentUserInfoDir.exists()) {
                currentUserInfoDir.mkdirs();
            }
            
            // 更新target目录下的用户文件
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
            }
            
            // 更新当前目录下的用户文件
            if (userFileInCurrentDir.exists()) {
                // 读取文件内容
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(userFileInCurrentDir))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Password: ")) {
                            line = "Password: " + hashedPassword;
                        }
                        lines.add(line);
                    }
                }
                
                // 直接写回原文件
                try (PrintWriter writer = new PrintWriter(new FileWriter(userFileInCurrentDir))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
            }
            
            // 更新当前密码
            currentPassword = newPassword;
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