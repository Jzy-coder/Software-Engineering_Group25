package com.finance.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PasswordChangeDialog extends JDialog {
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton applyButton;
    private JButton backButton;

    public PasswordChangeDialog(JFrame parent) {
        super(parent, "修改密码", true);
        initComponents();
        setupLayout();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        // 创建密码输入框
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        // 创建按钮
        applyButton = new JButton("应用");
        backButton = new JButton("返回");

        // 设置按钮事件
        applyButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.equals(confirmPassword)) {
                // 更新密码
                LoginManager.updatePassword(newPassword);
                JOptionPane.showMessageDialog(this, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致，请重新输入", "错误", JOptionPane.ERROR_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            }
        });

        backButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加新密码输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("新密码："), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(newPasswordField, gbc);

        // 添加确认密码输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("确认密码："), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(confirmPasswordField, gbc);

        // 添加按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(applyButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // 设置对话框大小
        setPreferredSize(new Dimension(300, 200));
        pack();
    }
}