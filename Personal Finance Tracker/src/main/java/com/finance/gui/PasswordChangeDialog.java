package com.finance.gui;

import javax.swing.*;
import java.awt.*;

public class PasswordChangeDialog extends JDialog {
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton applyButton;
    private JButton backButton;

    public PasswordChangeDialog(JFrame parent) {
        super(parent, "Modify Password", true);
        initComponents();
        setupLayout();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        applyButton = new JButton("Apply");
        backButton = new JButton("Return");

        applyButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "The password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean hasUpperCase = newPassword.matches(".*[A-Z].*");
            boolean hasLowerCase = newPassword.matches(".*[a-z].*");
            boolean hasDigit = newPassword.matches(".*\\d.*");
            boolean hasUnderscore = newPassword.matches(".*_.*");
            
            int characterTypeCount = (hasUpperCase ? 1 : 0) + 
                                     (hasLowerCase ? 1 : 0) + 
                                     (hasDigit ? 1 : 0) + 
                                     (hasUnderscore ? 1 : 0);
            
            if (characterTypeCount < 2) {
                JOptionPane.showMessageDialog(this, "The password must contain at least two types of characters among uppercase letters, lowercase letters, numbers and underscores", "Error", JOptionPane.ERROR_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "The passwords entered twice are not the same. Please re-enter them", "Error", JOptionPane.ERROR_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                return;
            }

            LoginManager.updatePassword(newPassword);
            JOptionPane.showMessageDialog(this, "The password modification was successful! Please use the new password when logging in next time", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        backButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel tipLabel = new JLabel("<html>Password requirements: At least two types of characters must be included: uppercase letters, lowercase letters, numbers, and underscores</html>");
        tipLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        tipLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(tipLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Affirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(confirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(applyButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        setPreferredSize(new Dimension(400, 220));
        pack();
    }
}