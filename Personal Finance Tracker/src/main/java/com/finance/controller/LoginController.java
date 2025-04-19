package com.finance.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import com.finance.gui.LoginManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.HBox;
import javafx.scene.control.ComboBox;
import javafx.application.Platform;

/**
 * Login Interface Controller
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    
    @FXML 
    private ComboBox<String> usernameCombo;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private CheckBox rememberPasswordBox;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create UserInfo directory if it doesn't exist
        createUserInfoDirectory();
        
        // Initialize username ComboBox with saved usernames
        usernameCombo.getItems().addAll(com.finance.util.UserCredentialManager.getSavedUsernames());
        
        // Setup focus listeners for username field
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                String currentText = usernameField.getText();
                if (currentText != null && !currentText.isEmpty()) {
                    updateComboBoxItems(currentText);
                    if (!usernameCombo.getItems().isEmpty()) {
                        usernameCombo.setManaged(true);
                        usernameCombo.setVisible(true);
                        usernameCombo.show();
                        return;
                    }
                }
            }
            usernameCombo.setManaged(false);
            usernameCombo.setVisible(false);
            usernameCombo.hide();
        });
        
        // Sync text between field and combo and filter items
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            usernameCombo.getEditor().setText(newVal);
            checkSavedPassword(newVal);
            
            if (usernameField.isFocused() && newVal != null && !newVal.isEmpty()) {
                updateComboBoxItems(newVal);
                if (!usernameCombo.getItems().isEmpty()) {
                    usernameCombo.setManaged(true);
                    usernameCombo.setVisible(true);
                    usernameCombo.show();
                } else {
                    usernameCombo.hide();
                }
            } else {
                usernameCombo.hide();
            }
        });
        
        // Handle combo selection
        usernameCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                usernameField.setText(newVal);
                checkSavedPassword(newVal);
                usernameCombo.hide();
                usernameCombo.setVisible(false);
                usernameCombo.setManaged(false);
                Platform.runLater(() -> {
                    usernameField.requestFocus();
                    passwordField.requestFocus();
                });
            }
        });

        // Handle text field changes
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                passwordField.clear();
                rememberPasswordBox.setSelected(false);
            }
            usernameCombo.getEditor().setText(newVal);
            checkSavedPassword(newVal);
        });
    }
    
    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username == null || username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Username and password cannot be empty");
            return;
        }
        
        if (LoginManager.validateLogin(username, password)) {
            // Save credentials if remember password is checked
            if (rememberPasswordBox.isSelected()) {
                com.finance.util.UserCredentialManager.saveCredentials(username, password);
            } else {
                com.finance.util.UserCredentialManager.removeCredentials(username);
            }
            
            showAlert(AlertType.INFORMATION, "Success", "Login successful!");
            openMainView();
        } else {
            showAlert(AlertType.ERROR, "Error", "Invalid username or password");
        }
    }
    
    /**
     * Show register dialog
     */
    @FXML
    private void showRegisterDialog() {
        // Create custom dialog
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Register New User");
        dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
        dialogStage.initOwner(loginButton.getScene().getWindow());
        dialogStage.initStyle(StageStyle.UTILITY);
        
        // Create registration form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        
        TextField newUsernameField = new TextField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(newUsernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(new Label("Password must contain at least two types of: uppercase letters, lowercase letters, numbers, underscores"), 0, 3, 2, 1);
        
        // Create buttons
        Button registerButton = new Button("Register");
        Button cancelButton = new Button("Cancel");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, cancelButton);
        
        grid.add(buttonBox, 0, 4, 2, 1);
        
        // Set register button action
        registerButton.setOnAction(e -> {
            String username = newUsernameField.getText();
            String password = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(AlertType.ERROR, "Error", "Username and password cannot be empty");
                return; // Return to form without closing dialog
            }
            
            // Validate username can only contain letters, numbers and underscores
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                showAlert(AlertType.ERROR, "Error", "Username can only contain letters, numbers and underscores");
                return; // Return to form without closing dialog
            }
            
            // Check if username already exists
            File userFile = new File("UserInfo" + File.separator + username + ".txt");
            if (userFile.exists()) {
                showAlert(AlertType.ERROR, "Error", "Username already exists, please use a different one");
                return; // Return to form without closing dialog
            }
            
            // First validate if passwords match
            if (!password.equals(confirmPassword)) {
                showAlert(AlertType.ERROR, "Error", "Passwords do not match");
                return; // Return to form without closing dialog
            }
            
            // Then validate if password contains at least two types of characters
            boolean hasUpperCase = password.matches(".*[A-Z].*");
            boolean hasLowerCase = password.matches(".*[a-z].*");
            boolean hasDigit = password.matches(".*\\d.*");
            boolean hasUnderscore = password.matches(".*_.*");
            
            int characterTypeCount = (hasUpperCase ? 1 : 0) + 
                                     (hasLowerCase ? 1 : 0) + 
                                     (hasDigit ? 1 : 0) + 
                                     (hasUnderscore ? 1 : 0);
            
            if (characterTypeCount < 2) {
                showAlert(AlertType.ERROR, "Error", "Password must contain at least two types of: uppercase letters, lowercase letters, numbers, underscores");
                return; // Return to form without closing dialog
            }
            
            // All validations passed, save user information
            String hashedPassword = hashPassword(password);
            saveUserToFile(username, hashedPassword);
            showAlert(AlertType.INFORMATION, "Success", "Registration successful!");
            
            // Close dialog
            dialogStage.close();
        });
        
        // Set cancel button action
        cancelButton.setOnAction(e -> dialogStage.close());
        
        // Create scene and display
        Scene scene = new Scene(grid);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
    
    /**
     * Create UserInfo directory if it doesn't exist
     */
    private void createUserInfoDirectory() {
        File userInfoDir = new File("UserInfo");
        if (!userInfoDir.exists()) {
            userInfoDir.mkdir();
        }
    }
    
    /**
     * Save user information to file
     */
    private void saveUserToFile(String username, String hashedPassword) {
        createUserInfoDirectory();
        File userFile = new File("UserInfo" + File.separator + username + ".txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(userFile))) {
            writer.println("Username: " + username);
            writer.println("Password: " + hashedPassword);
        } catch (IOException e) {
            System.err.println("Failed to save user information: " + e.getMessage());
            showAlert(AlertType.ERROR, "Error", "Failed to save user information");
        }
    }
    
    /**
     * Hash password using SHA-256
     */
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
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Check if password is saved for given username
     */
    private void updateComboBoxItems(String filter) {
        usernameCombo.getItems().clear();
        usernameCombo.getItems().addAll(
            com.finance.util.UserCredentialManager.getSavedUsernames()
                .stream()
                .filter(name -> name.toLowerCase().contains(filter.toLowerCase()))
                .toList()
        );
    }

    private void checkSavedPassword(String username) {
        if (username != null && !username.isEmpty()) {
            String savedPassword = com.finance.util.UserCredentialManager.getSavedPassword(username);
            if (savedPassword != null) {
                passwordField.setText(savedPassword);
                rememberPasswordBox.setSelected(true);
            } else {
                passwordField.clear();
                rememberPasswordBox.setSelected(false);
            }
        }
    }

    /**
     * Open main view after successful login
     */
    private void openMainView() {
        try {
            // Load the main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Create new scene with main view
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            // Set the scene to the stage
            stage.setTitle("Personal Finance Assistant");
            stage.setScene(scene);
            stage.setWidth(1152);
            stage.setHeight(768);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load main view: " + e.getMessage());
        }
    }
}
