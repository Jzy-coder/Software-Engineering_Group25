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
        // Create gender radio buttons
        genderGroup = new ButtonGroup();
        maleButton = new JRadioButton("Man");
        femaleButton = new JRadioButton("Female");
        
        // Set font and size for radio buttons
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        maleButton.setFont(buttonFont);
        femaleButton.setFont(buttonFont);
        
        // Add radio buttons to the group
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        
        // setupLayout();
        String currentGender = UserInfoManager.getGender();
        if ("Man".equals(currentGender)) {
            maleButton.setSelected(true);
        } else if ("Female".equals(currentGender)) {
            femaleButton.setSelected(true);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // create gender panel
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        
        // create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Return");
        
        // set button font and size
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        Dimension buttonSize = new Dimension(80, 30);
        
        applyButton.setFont(buttonFont);
        cancelButton.setFont(buttonFont);
        applyButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        // add button events
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
        
        // add panels to the dialog
        add(genderPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(250, 150);
    }
}