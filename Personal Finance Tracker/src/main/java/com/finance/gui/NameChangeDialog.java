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
        super(parent, "修改用户名", true);
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
                JOptionPane.showMessageDialog(this, "无法加载配置文件", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveConfig() {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            userProps.store(out, "User Configuration");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "无法保存配置文件", "错误", JOptionPane.ERROR_MESSAGE);
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
        JLabel label = new JLabel("新用户名:");
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
        JButton applyButton = new JButton("应用");
        JButton cancelButton = new JButton("返回");

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
            JOptionPane.showMessageDialog(this, "用户名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (username.length() > 20) {
            JOptionPane.showMessageDialog(this, "用户名不能超过20个字符", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!username.matches("^[a-zA-Z]+$")) {
            JOptionPane.showMessageDialog(this, "用户名只能包含英文字母", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void updateUsername(String newUsername) {
        // 更新当前用户名
        LoginManager.setCurrentUsername(newUsername);
        
        // 更新配置文件中的用户名
        String oldUsername = LoginManager.getCurrentUsername();
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
        
        JOptionPane.showMessageDialog(this, "用户名修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
    }
}