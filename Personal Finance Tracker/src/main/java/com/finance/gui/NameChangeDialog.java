package com.finance.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

public class NameChangeDialog extends JDialog {
    private JTextField usernameField;
    private Properties userProps;
    private File configFile;
    private JLabel welcomeLabel;

    public NameChangeDialog(JFrame parent, JLabel welcomeLabel) {
        super(parent, "Change Username", true);
        this.welcomeLabel = welcomeLabel;
        this.configFile = new File(System.getProperty("user.home") + File.separator + ".finance_tracker_config");
        this.userProps = new Properties();
        loadConfig();
        initComponents();
    }

    private void loadConfig() {
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                userProps.load(in);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Unable to load configuration file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveConfig() {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            userProps.store(out, "User Configuration");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to save configuration file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 200));

        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // 添加输入框
        JLabel label = new JLabel("New Username:");
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Back");

        // 设置按钮样式
        Dimension buttonSize = new Dimension(80, 30);
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);

        applyButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        applyButton.setFont(buttonFont);
        cancelButton.setFont(buttonFont);

        // 添加应用按钮事件
        applyButton.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            if (validateUsername(newUsername)) {
                updateUsername(newUsername);
                dispose();
            }
        });

        // 添加返回按钮事件
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        // 添加面板到对话框
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 设置对话框属性
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (username.length() > 20) {
            JOptionPane.showMessageDialog(this, "Username cannot exceed 20 characters", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // 验证用户名只能包含字母、数字和下划线
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            JOptionPane.showMessageDialog(this, "Username can only contain letters, numbers, and underscores", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // 检查新用户名是否已存在
        File targetUserFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + username + ".txt");
        if (targetUserFile.exists() && !username.equals(LoginManager.getCurrentUsername())) {
            JOptionPane.showMessageDialog(this, "Username already exists, please use a different username", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    private void updateUsername(String newUsername) {
        String oldUsername = LoginManager.getCurrentUsername();
        
        // 只有当新旧用户名不同时才进行修改
        if (!oldUsername.equals(newUsername)) {
            try {
                // 获取旧用户文件路径
                File oldUserFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + oldUsername + ".txt");
                File oldUserFileInCurrentDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + oldUsername + ".txt");
                
                // 获取新用户文件路径
                File newUserFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + newUsername + ".txt");
                File newUserFileInCurrentDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + newUsername + ".txt");
                
                // 确保UserInfo目录存在
                File targetUserInfoDir = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo");
                File currentUserInfoDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo");
                if (!targetUserInfoDir.exists()) {
                    targetUserInfoDir.mkdirs();
                }
                if (!currentUserInfoDir.exists()) {
                    currentUserInfoDir.mkdirs();
                }
                
                if (oldUserFile.exists()) {
                    // 读取旧文件内容
                    java.util.List<String> lines = new java.util.ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(new FileReader(oldUserFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Username: ")) {
                                line = "Username: " + newUsername;
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
                    
                    // 删除旧文件
                    oldUserFile.delete();
                }
                
                // 同样处理当前目录下的用户文件
                if (oldUserFileInCurrentDir.exists()) {
                    // 读取旧文件内容
                    java.util.List<String> lines = new java.util.ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(new FileReader(oldUserFileInCurrentDir))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("Username: ")) {
                                line = "Username: " + newUsername;
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
                    
                    // 删除旧文件
                    oldUserFileInCurrentDir.delete();
                }
                
                // 更新当前用户名
                LoginManager.setCurrentUsername(newUsername);
                
                // 更新配置文件中的用户名
                String oldKey = oldUsername + ".username";
                String newKey = newUsername + ".username";
                
                // 迁移密码属性
                String oldPasswordKey = oldUsername + ".original_password";
                String newPasswordKey = newUsername + ".original_password";
                
                if (userProps.containsKey(oldPasswordKey)) {
                    String passwordValue = userProps.getProperty(oldPasswordKey);
                    userProps.setProperty(newPasswordKey, passwordValue);
                    userProps.remove(oldPasswordKey);
                }
                
                if (userProps.containsKey(oldKey)) {
                    userProps.remove(oldKey);
                }
                userProps.setProperty(newKey, newUsername);
                saveConfig();
                
                // 更新欢迎标签
                welcomeLabel.setText("Hi~ " + newUsername);
                
                JOptionPane.showMessageDialog(this, "Username changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred while changing username", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // 用户名没有变化，直接返回
            JOptionPane.showMessageDialog(this, "New username is the same as current username, no changes made", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}