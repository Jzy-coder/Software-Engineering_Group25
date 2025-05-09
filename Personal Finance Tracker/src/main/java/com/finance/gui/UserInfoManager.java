package com.finance.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserInfoManager {
    private static final String USER_INFO_DIR = "UserInfo";
    private static final String USER_INFO_FILE_TEMPLATE = "%s.txt";
    private static Properties properties;
    private static String loadedForUser = null; // 记录当前 'properties' 缓存是为哪个用户加载的
    
    static {
        properties = new Properties();
        loadUserInfo();
    }
    
    /**
     * 当用户切换时重新加载用户信息
     */
    public static void reloadUserInfo() {
        loadUserInfo();
    }
    
    /**
     * 获取当前用户的配置文件路径
     */
    private static String getUserInfoFilePath() {
        String username = LoginManager.getCurrentUsername();
        return USER_INFO_DIR + File.separator + username + ".txt";
    }
    
    private static void loadUserInfo() {
        // 清空现有属性
        properties.clear();
        String currentUsername = LoginManager.getCurrentUsername(); // 获取当前用户名
        
        // 获取当前用户的配置文件路径
        String filePath = getUserInfoFilePath();
        File userFile = new File(filePath);
        
        if (userFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Gender: ")) {
                        properties.setProperty("gender", line.substring(8).trim());
                    } else if (line.startsWith("Area: ")) {
                        properties.setProperty("area", line.substring(6).trim());
                    } else if (line.startsWith("Occupation: ")) {
                        properties.setProperty("occupation", line.substring(12).trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadedForUser = currentUsername; // 更新缓存状态以反映当前用户
    }
    
    private static void saveUserInfo() {
        try {
            // 获取当前用户的配置文件路径
            String filePath = getUserInfoFilePath();
            File file = new File(filePath);
            file.getParentFile().mkdirs(); // 确保目录存在
            
            // 读取现有文件内容
            List<String> lines = new ArrayList<>();
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("Gender: ") && 
                            !line.startsWith("Area: ") && 
                            !line.startsWith("Occupation: ")) {
                            lines.add(line);
                        }
                    }
                }
            }
            
            // 添加用户信息
            lines.add("Gender: " + getGender());
            lines.add("Area: " + getArea());
            lines.add("Occupation: " + getOccupation());
            
            // 写入文件
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void setGender(String gender) {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); // 在修改前，确保 'properties' 缓存是为当前用户加载的
        }
        properties.setProperty("gender", gender);
        saveUserInfo();
    }
    
    public static String getGender() {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); // 确保 'properties' 缓存对当前用户是最新的
        }
        return properties.getProperty("gender", "");
    }
    
    public static void setArea(String area) {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); // 在修改前，确保 'properties' 缓存是为当前用户加载的
        }
        properties.setProperty("area", area);
        saveUserInfo();
    }
    
    public static String getArea() {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); // 确保 'properties' 缓存对当前用户是最新的
        }
        return properties.getProperty("area", "");
    }
    
    public static void setOccupation(String occupation) {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); // 在修改前，确保 'properties' 缓存是为当前用户加载的
        }
        properties.setProperty("occupation", occupation);
        saveUserInfo();
    }
    
    public static String getOccupation() {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); // 确保 'properties' 缓存对当前用户是最新的
        }
        return properties.getProperty("occupation", "");
    }
}