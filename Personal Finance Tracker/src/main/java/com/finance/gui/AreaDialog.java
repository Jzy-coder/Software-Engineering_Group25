package com.finance.gui;

import javax.swing.*;
import java.awt.*;

public class AreaDialog extends JDialog {
    private JTextField areaField;
    
    public AreaDialog(JFrame parent) {
        super(parent, "输入地区", true);
        initComponents();
        setupLayout();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        areaField = new JTextField(20);
        areaField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        
        // 设置当前保存的地区
        String currentArea = UserInfoManager.getArea();
        if (currentArea != null && !currentArea.isEmpty()) {
            areaField.setText(currentArea);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel label = new JLabel("地区：");
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        inputPanel.add(label);
        inputPanel.add(areaField);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton applyButton = new JButton("应用");
        JButton cancelButton = new JButton("返回");
        
        // 设置按钮字体和大小
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        Dimension buttonSize = new Dimension(80, 30);
        
        applyButton.setFont(buttonFont);
        cancelButton.setFont(buttonFont);
        applyButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        // 添加按钮事件
        applyButton.addActionListener(e -> {
            String area = areaField.getText().trim();
            if (!area.isEmpty()) {
                UserInfoManager.setArea(area);
            }
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        
        // 添加到主面板
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 设置对话框大小
        setSize(300, 150);
    }
}