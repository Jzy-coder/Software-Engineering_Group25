package com.finance.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicComboBoxUI;

import com.finance.gui.MainWindow;

public class LoginFrame extends JFrame {
    private JComboBox<String> usernameComboBox;
    private JPasswordField passwordField;
    private final JCheckBox rememberPasswordBox;
    private Properties userProps = new Properties();
    private File configFile = new File(System.getProperty("user.home") + File.separator + ".finance_tracker_config");

    private void loadSavedUserInfo() {
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                userProps.load(in);
                updateUsernameComboBox();
                // Initialize by clearing the password field
                passwordField.setText("");
            } catch (IOException e) {
                String errorMsg = "Failed to load configuration file: " + e.getMessage();
                System.err.println(errorMsg);
                JOptionPane.showMessageDialog(this, "Failed to load configuration file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateUsernameComboBox() {
        usernameComboBox.removeAllItems();
        java.util.Set<String> uniqueUsernames = new java.util.HashSet<>();
        for (String key : userProps.stringPropertyNames()) {
            if (key.endsWith(".username")) {
                String username = userProps.getProperty(key);
                uniqueUsernames.add(username);
            }
        }
        for (String username : uniqueUsernames) {
            usernameComboBox.addItem(username);
        }
    
        ((JTextField) usernameComboBox.getEditor().getEditorComponent()).setText("");
    }

    private void saveConfig() {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            userProps.store(out, "User Configuration");
        } catch (IOException e) {
            String errorMsg = "Failed to save configuration file: " + e.getMessage();
            System.err.println(errorMsg);
            JOptionPane.showMessageDialog(this, "Failed to save configuration file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public LoginFrame() {
        setTitle("Personal Finance Tracker - Login");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setMaximumSize(new Dimension(600, 450));
        setMinimumSize(new Dimension(600, 450));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);



        // Add title
        JLabel titleLabel = new JLabel("Personal Finance Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(usernameLabel, gbc);

        Dimension fieldSize = new Dimension(200, 25);
        usernameComboBox = new JComboBox<>();
        usernameComboBox.setEditable(true);
        usernameComboBox.setPreferredSize(fieldSize);
        usernameComboBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        // Set ComboBox UI to hide the dropdown button
        // Customize ComboBox UI to only highlight items on mouse hover
        usernameComboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new JButton() {
                    @Override
                    public int getWidth() {
                        return 0;
                    }
                };
            }
        });
        
        // Set custom renderer to handle item highlighting
        usernameComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(list.getSelectionBackground());
                    c.setForeground(list.getSelectionForeground());
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(list.getForeground());
                }
                return c;
            }
        });
        // Add mouse click event to show dropdown list when clicked
        usernameComboBox.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                usernameComboBox.showPopup();
            }
        });
        gbc.gridx = 1;
        mainPanel.add(usernameComboBox, gbc);

        // Listen for username selection changes
        usernameComboBox.addActionListener(e -> {
            String selectedUsername = (String) usernameComboBox.getSelectedItem();
            if (selectedUsername != null && !selectedUsername.trim().isEmpty()) {
                String savedPassword = getSavedPassword(selectedUsername);
                if (savedPassword != null) {
                    passwordField.setText(savedPassword);
                } else {
                    passwordField.setText("");
                }
            } else {
                passwordField.setText("");
            }
        });

        // Listen for username text changes
        ((JTextField) usernameComboBox.getEditor().getEditorComponent()).getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePassword(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePassword(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePassword(); }
            
            private void updatePassword() {
                String currentText = ((JTextField) usernameComboBox.getEditor().getEditorComponent()).getText();
                if (currentText == null || currentText.trim().isEmpty()) {
                    passwordField.setText("");
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(fieldSize);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rememberPasswordBox = new JCheckBox("Remember Password");
        checkBoxPanel.add(rememberPasswordBox);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(checkBoxPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> showRegisterDialog());

        loadSavedUserInfo();

        setContentPane(mainPanel);
    }

    private void handleLogin() {
        String username = (String) usernameComboBox.getSelectedItem();
        String password = new String(passwordField.getPassword());

        if (validateLogin(username, password)) {
            saveUserInfo(username, password);
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            MainWindow mainWindow = new MainWindow();
            mainWindow.setSize(1400, 900);
            mainWindow.setResizable(false);
            mainWindow.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateLogin(String username, String password) {
        File userFile = new File("UserInfo" + File.separator + username + ".txt");
        if (!userFile.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            String storedPassword = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Password: ")) {
                    storedPassword = line.substring("Password: ".length());
                    break;
                }
            }
            if (storedPassword == null) {
                return false;
            }
            // If the input password is already a hash value, compare directly
            if (password.length() == 64 && password.matches("[a-fA-F0-9]+")) {
                return storedPassword.equals(password);
            }
            // Otherwise, hash the input password before comparison
            return storedPassword.equals(hashPassword(password));
        } catch (IOException e) {
            String errorMsg = "Error validating login: " + e.getMessage();
            System.err.println(errorMsg);
            return false;
        }
    }

    private void createUserInfoDirectory() {
        File userInfoDir = new File("UserInfo");
        if (!userInfoDir.exists()) {
            userInfoDir.mkdir();
        }
    }

    private void saveUserToFile(String username, String hashedPassword) {
        createUserInfoDirectory();
        File userFile = new File("UserInfo" + File.separator + username + ".txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(userFile))) {
            writer.println("Username: " + username);
            writer.println("Password: " + hashedPassword);
        } catch (IOException e) {
            String errorMsg = "Failed to save user information: " + e.getMessage();
            System.err.println(errorMsg);
            JOptionPane.showMessageDialog(this, "Failed to save user information", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveUserInfo(String username, String password) {
        // Find existing user index or create new one
        int userIndex = -1;
        for (String key : userProps.stringPropertyNames()) {
            if (key.endsWith(".username") && userProps.getProperty(key).equals(username)) {
                userIndex = Integer.parseInt(key.split("\\.")[0]);
                break;
            }
        }
        if (userIndex == -1) {
            userIndex = userProps.size() / 3;
        }
        
        userProps.setProperty(userIndex + ".username", username);
        if (rememberPasswordBox.isSelected()) {
            userProps.setProperty(userIndex + ".password", hashPassword(password));
            userProps.setProperty(userIndex + ".original_password", password);
        } else {
            userProps.remove(userIndex + ".password");
            userProps.remove(userIndex + ".original_password");
        }
        saveConfig();
    }

    private String getSavedPassword(String username) {
        for (String key : userProps.stringPropertyNames()) {
            if (key.endsWith(".username") && userProps.getProperty(key).equals(username)) {
                String baseKey = key.substring(0, key.length() - ".username".length());
                String originalPassword = userProps.getProperty(baseKey + ".original_password");
                if (originalPassword != null) {
                    rememberPasswordBox.setSelected(true);
                    return originalPassword;
                }
            }
        }
        rememberPasswordBox.setSelected(false);
        return null;
    }

    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Register New User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JTextField newUsernameField = new JTextField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(newUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(confirmPasswordField, gbc);

        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            String username = newUsernameField.getText();
            String password = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String hashedPassword = hashPassword(password);
            saveUserToFile(username, hashedPassword);
            JOptionPane.showMessageDialog(dialog, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            String errorMsg = "Error hashing password: " + e.getMessage();
            System.err.println(errorMsg);
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                String errorMsg = "Error setting look and feel: " + e.getMessage();
                System.err.println(errorMsg);
            }
            new LoginFrame().setVisible(true);
        });
    }
}