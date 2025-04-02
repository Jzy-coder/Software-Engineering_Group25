package com.finance.gui;

/**
 * 管理用户登录状态的工具类
 */
public class LoginManager {
    private static String currentUsername = "Default User";
    
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
        currentUsername = username;
    }
}