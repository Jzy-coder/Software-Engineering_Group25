package com.finance.gui;

import javax.swing.*;
import java.awt.*;

public class GenderDialog extends JDialog {
    private ButtonGroup genderGroup;
    private JRadioButton maleButton;
    private JRadioButton femaleButton;
    
    public GenderDialog(JFrame parent) {
        super(parent, "Select the gender", true);
        initComponents();
        setupLayout();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // 创建性别选择按钮组
        genderGroup = new ButtonGroup();
        maleButton = new JRadioButton("Man");
        femaleButton = new JRadioButton("Female");
        
        // 设置按钮字体
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        maleButton.setFont(buttonFont);
        femaleButton.setFont(buttonFont);
        
        // 添加到按钮组
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        
        // 根据已保存的性别设置选中状态
        String currentGender = UserInfoManager.getGender();
        if ("Man".equals(currentGender)) {
            maleButton.setSelected(true);
        } else if ("Female".equals(currentGender)) {
            femaleButton.setSelected(true);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建性别选择面板
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        
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
            if (maleButton.isSelected()) {
                UserInfoManager.setGender("Man");
            } else if (femaleButton.isSelected()) {
                UserInfoManager.setGender("Female");
            }
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        
        // 添加到主面板
        add(genderPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 设置对话框大小
        setSize(250, 150);
    }
}