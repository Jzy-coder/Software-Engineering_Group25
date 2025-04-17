package com.finance.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

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

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 验证密码是否至少包含两类字符
            boolean hasUpperCase = newPassword.matches(".*[A-Z].*");
            boolean hasLowerCase = newPassword.matches(".*[a-z].*");
            boolean hasDigit = newPassword.matches(".*\\d.*");
            boolean hasUnderscore = newPassword.matches(".*_.*");
            
            int characterTypeCount = (hasUpperCase ? 1 : 0) + 
                                     (hasLowerCase ? 1 : 0) + 
                                     (hasDigit ? 1 : 0) + 
                                     (hasUnderscore ? 1 : 0);
            
            if (characterTypeCount < 2) {
                JOptionPane.showMessageDialog(this, "密码必须至少包含大写字母、小写字母、数字、下划线中的两类字符", "错误", JOptionPane.ERROR_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致，请重新输入", "错误", JOptionPane.ERROR_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                return;
            }

            // 更新密码
            LoginManager.updatePassword(newPassword);
            JOptionPane.showMessageDialog(this, "密码修改成功！下次登录时请使用新密码", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        backButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加提示标签
        JLabel tipLabel = new JLabel("<html>密码要求：至少包含大写字母、小写字母、数字、下划线中的两类字符</html>");
        tipLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        tipLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(tipLabel, gbc);

        // 添加新密码输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("新密码："), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(newPasswordField, gbc);

        // 添加确认密码输入框
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("确认密码："), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(confirmPasswordField, gbc);

        // 添加按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(applyButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // 设置对话框大小
        setPreferredSize(new Dimension(400, 220));
        pack();
    }
}