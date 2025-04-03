package com.finance.gui;

/**
 * 管理用户登录状态的工具类
 */
public class LoginManager {
    private static String currentUsername = "Default User";
    private static String currentPassword = "";
    
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

    /**
     * 更新用户密码
     * @param newPassword 新密码
     */
    public static void updatePassword(String newPassword) {
        currentPassword = newPassword;
        // TODO: 将新密码保存到配置文件中
        savePasswordToConfig(newPassword);
    }

    /**
     * 保存密码到配置文件
     * @param password 要保存的密码
     */
    private static void savePasswordToConfig(String password) {
        // TODO: 实现密码保存到配置文件的逻辑
        // 这里需要根据实际的配置文件格式和位置来实现
    }
}