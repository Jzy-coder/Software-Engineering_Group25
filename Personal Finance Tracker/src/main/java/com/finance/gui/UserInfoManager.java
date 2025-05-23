package com.finance.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserInfoManager {
    private static final String USER_INFO_DIR = "UserInfo";
    private static Properties properties;
    private static String loadedForUser = null; 
    
    static {
        properties = new Properties();
        loadUserInfo();
    }
    
    /**
     * reload user info from the txt file
     */
    public static void reloadUserInfo() {
        loadUserInfo();
    }
    
    /**
     * aquire the user info file path
     */
    private static String getUserInfoFilePath() {
        String username = LoginManager.getCurrentUsername();
        return USER_INFO_DIR + File.separator + username + ".txt";
    }
    
    private static void loadUserInfo() {
        // clear the properties first to avoid nul
        properties.clear();
        String currentUsername = LoginManager.getCurrentUsername(); 
        
        // aquire the user info file path
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
        loadedForUser = currentUsername; // update the loaded user
    }
    
    private static void saveUserInfo() {
        try {
            // aquire the user info file path
            String filePath = getUserInfoFilePath();
            File file = new File(filePath);
            file.getParentFile().mkdirs(); // ensure the directory exists
            
            // read the existing lines
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
            
            // add the new lines
            lines.add("Gender: " + getGender());
            lines.add("Area: " + getArea());
            lines.add("Occupation: " + getOccupation());
            
            // write the lines back to the file
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
            loadUserInfo(); 
        }
        properties.setProperty("gender", gender);
        saveUserInfo();
    }
    
    public static String getGender() {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); 
        }
        return properties.getProperty("gender", "");
    }
    
    public static void setArea(String area) {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); 
        }
        properties.setProperty("area", area);
        saveUserInfo();
    }
    
    public static String getArea() {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); 
        }
        return properties.getProperty("area", "");
    }
    
    public static void setOccupation(String occupation) {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); 
        }
        properties.setProperty("occupation", occupation);
        saveUserInfo();
    }
    
    public static String getOccupation() {
        String currentUser = LoginManager.getCurrentUsername();
        if (loadedForUser == null || !loadedForUser.equals(currentUser)) {
            loadUserInfo(); 
        }
        return properties.getProperty("occupation", "");
    }
}