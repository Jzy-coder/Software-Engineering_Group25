package com.finance.gui;

import java.io.*;
import java.util.Properties;

public class UserInfoManager {
    private static final String USER_INFO_DIR = "UserInfo";
    private static final String USER_INFO_FILE_TEMPLATE = "%s_info.properties";
    private static Properties properties;
    
    static {
        properties = new Properties();
        loadUserInfo();
    }
    
    /**
     * 获取当前用户的配置文件路径
     */
    private static String getUserInfoFilePath() {
        String username = LoginManager.getCurrentUsername();
        return USER_INFO_DIR + File.separator + String.format(USER_INFO_FILE_TEMPLATE, username);
    }
    
    private static void loadUserInfo() {
        // 清空现有属性
        properties.clear();
        
        // 获取当前用户的配置文件路径
        String filePath = getUserInfoFilePath();
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            // 如果文件不存在，创建一个新的文件
            saveUserInfo();
        }
    }
    
    private static void saveUserInfo() {
        try {
            // 获取当前用户的配置文件路径
            String filePath = getUserInfoFilePath();
            File file = new File(filePath);
            file.getParentFile().mkdirs(); // 确保目录存在
            try (FileOutputStream fos = new FileOutputStream(file)) {
                properties.store(fos, "User Information for " + LoginManager.getCurrentUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void setGender(String gender) {
        properties.setProperty("gender", gender);
        saveUserInfo();
    }
    
    public static String getGender() {
        return properties.getProperty("gender", "");
    }
    
    public static void setArea(String area) {
        properties.setProperty("area", area);
        saveUserInfo();
    }
    
    public static String getArea() {
        return properties.getProperty("area", "");
    }
    
    public static void setOccupation(String occupation) {
        properties.setProperty("occupation", occupation);
        saveUserInfo();
    }
    
    public static String getOccupation() {
        return properties.getProperty("occupation", "");
    }
}