package com.finance.gui;

import javax.swing.*;
import java.awt.*;

public class AreaDialog extends JDialog {
    private JTextField areaField;
    
    public AreaDialog(JFrame parent) {
        super(parent, "Input the region.", true);
        initComponents();
        setupLayout();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        areaField = new JTextField(20);
        areaField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        
        // Set current saved region
        String currentArea = UserInfoManager.getArea();
        if (currentArea != null && !currentArea.isEmpty()) {
            areaField.setText(currentArea);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel label = new JLabel("Region:");
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        inputPanel.add(label);
        inputPanel.add(areaField);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Return");
        
        // Set button font and size
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        Dimension buttonSize = new Dimension(80, 30);
        
        applyButton.setFont(buttonFont);
        cancelButton.setFont(buttonFont);
        applyButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        // Add button events
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
        
        // Add to main panel
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set dialog size
        setSize(300, 150);
    }
}