package com.finance.gui;

import javax.swing.*;
import java.awt.*;
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

     
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

       
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

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Back");

        
        Dimension buttonSize = new Dimension(80, 30);
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 14);

        applyButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        applyButton.setFont(buttonFont);
        cancelButton.setFont(buttonFont);

     
        applyButton.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            if (validateUsername(newUsername)) {
                updateUsername(newUsername);
                dispose();
            }
        });

        
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

    
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

     
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
       
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            JOptionPane.showMessageDialog(this, "Username can only contain letters, numbers, and underscores", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
       
        File targetUserFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + username + ".txt");
        if (targetUserFile.exists() && !username.equals(LoginManager.getCurrentUsername())) {
            JOptionPane.showMessageDialog(this, "Username already exists, please use a different username", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    private void updateUsername(String newUsername) {
        String oldUsername = LoginManager.getCurrentUsername();
        
       
        if (!oldUsername.equals(newUsername)) {
            try {
               
                File oldUserFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + oldUsername + ".txt");
                File oldUserFileInCurrentDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + oldUsername + ".txt");
                
               
                File newUserFile = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo" + File.separator + newUsername + ".txt");
                File newUserFileInCurrentDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo" + File.separator + newUsername + ".txt");
                
              
                File targetUserInfoDir = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "UserInfo");
                File currentUserInfoDir = new File(System.getProperty("user.dir") + File.separator + "UserInfo");
                if (!targetUserInfoDir.exists()) {
                    targetUserInfoDir.mkdirs();
                }
                if (!currentUserInfoDir.exists()) {
                    currentUserInfoDir.mkdirs();
                }
                
                if (oldUserFile.exists()) {
                    
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
                    
                   
                    try (PrintWriter writer = new PrintWriter(new FileWriter(newUserFile))) {
                        for (String line : lines) {
                            writer.println(line);
                        }
                    }
                    
                    
                    oldUserFile.delete();
                }
                
                
                if (oldUserFileInCurrentDir.exists()) {
                   
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
                    
                 
                    try (PrintWriter writer = new PrintWriter(new FileWriter(newUserFileInCurrentDir))) {
                        for (String line : lines) {
                            writer.println(line);
                        }
                    }
                    
                   
                    oldUserFileInCurrentDir.delete();
                }
                
             
                LoginManager.setCurrentUsername(newUsername);
                
             
                String oldKey = oldUsername + ".username";
                String newKey = newUsername + ".username";
                
                
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
                
                
                welcomeLabel.setText("Hi~ " + newUsername);
                
                JOptionPane.showMessageDialog(this, "Username changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred while changing username", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            
            JOptionPane.showMessageDialog(this, "New username is the same as current username, no changes made", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}