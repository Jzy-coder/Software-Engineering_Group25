import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JComboBox<String> usernameComboBox;
    private JPasswordField passwordField;
    private JCheckBox rememberPasswordBox;
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
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load configuration file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateUsernameComboBox() {
        usernameComboBox.removeAllItems();
        for (String key : userProps.stringPropertyNames()) {
            if (key.endsWith(".username")) {
                String username = userProps.getProperty(key);
                usernameComboBox.addItem(username);
            }
        }
    }

    private void saveConfig() {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            userProps.store(out, "User Configuration");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save configuration file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public LoginFrame() {
        setTitle("Personal Finance Tracker - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Personal Finance Tracker");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Username:"), gbc);

        usernameComboBox = new JComboBox<>();
        usernameComboBox.setEditable(true);
        // Set ComboBox UI to hide the dropdown button
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
        // Add mouse click event to show dropdown list when clicked
        usernameComboBox.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePassword(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePassword(); }
            
            private void updatePassword() {
                String currentText = ((JTextField) usernameComboBox.getEditor().getEditorComponent()).getText();
                if (currentText == null || currentText.trim().isEmpty()) {
                    passwordField.setText("");
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 9));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rememberPasswordBox = new JCheckBox("Remember Password");
        checkBoxPanel.add(rememberPasswordBox);

        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(checkBoxPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
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
            return storedPassword.equals(hashPassword(password));
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save user information", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveUserInfo(String username, String password) {
        int userIndex = userProps.size() / 2;
        userProps.setProperty(userIndex + ".username", username);
        if (rememberPasswordBox.isSelected()) {
            userProps.setProperty(userIndex + ".password", password);
        }
        saveConfig();
    }

    private String getSavedPassword(String username) {
        for (String key : userProps.stringPropertyNames()) {
            if (key.endsWith(".username") && userProps.getProperty(key).equals(username)) {
                String passwordKey = key.replace(".username", ".password");
                return userProps.getProperty(passwordKey);
            }
        }
        return null;
    }

    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Register New User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

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
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}