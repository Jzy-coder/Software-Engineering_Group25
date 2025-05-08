package com.finance.gui;

import javax.swing.*;
import java.awt.*;

public class OccupationDialog extends JDialog {
    private JTextField occupationField;
    
    public OccupationDialog(JFrame parent) {
        super(parent, "Enter the occupation", true);
        initComponents();
        setupLayout();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        occupationField = new JTextField(20);
        occupationField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        
        // 设置当前保存的职业
        String currentOccupation = UserInfoManager.getOccupation();
        if (currentOccupation != null && !currentOccupation.isEmpty()) {
            occupationField.setText(currentOccupation);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel label = new JLabel("Occupation:");
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        inputPanel.add(label);
        inputPanel.add(occupationField);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Return");
        
        // 设置按钮字体和大小
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        Dimension buttonSize = new Dimension(80, 30);
        
        applyButton.setFont(buttonFont);
        cancelButton.setFont(buttonFont);
        applyButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        // 添加按钮事件
        applyButton.addActionListener(e -> {
            String occupation = occupationField.getText().trim();
            if (!occupation.isEmpty()) {
                UserInfoManager.setOccupation(occupation);
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