package com.finance.gui;

import java.io.*;
import java.util.Properties;

public class UserInfoManager {
    private static final String USER_INFO_FILE = "UserInfo/userInfo.properties";
    private static Properties properties;
    
    static {
        properties = new Properties();
        loadUserInfo();
    }
    
    private static void loadUserInfo() {
        try (FileInputStream fis = new FileInputStream(USER_INFO_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // 如果文件不存在，创建一个新的文件
            saveUserInfo();
        }
    }
    
    private static void saveUserInfo() {
        try {
            File file = new File(USER_INFO_FILE);
            file.getParentFile().mkdirs(); // 确保目录存在
            try (FileOutputStream fos = new FileOutputStream(file)) {
                properties.store(fos, "User Information");
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