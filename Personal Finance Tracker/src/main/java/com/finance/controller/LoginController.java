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

/**
 * Login Interface Controller
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    
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
    }
    
    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Username and password cannot be empty");
            return;
        }
        
        if (validateLogin(username, password)) {
            showAlert(AlertType.INFORMATION, "Success", "Login successful!");
            openMainView();
        } else {
            showAlert(AlertType.ERROR, "Error", "Invalid username or password!");
        }
    }
    
    /**
     * Show register dialog
     */
    @FXML
    private void showRegisterDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Register New User");
        dialog.initStyle(StageStyle.UTILITY);
        
        // Create the registration form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField newUsernameField = new TextField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(newUsernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons to the dialog
        dialog.getDialogPane().getButtonTypes().addAll(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL);
        
        // Handle the registration when OK is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == javafx.scene.control.ButtonType.OK) {
                String username = newUsernameField.getText();
                String password = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                
                if (username.isEmpty() || password.isEmpty()) {
                    showAlert(AlertType.ERROR, "Error", "Username and password cannot be empty");
                    return null;
                }
                
                if (!password.equals(confirmPassword)) {
                    showAlert(AlertType.ERROR, "Error", "Passwords do not match");
                    return null;
                }
                
                String hashedPassword = hashPassword(password);
                saveUserToFile(username, hashedPassword);
                showAlert(AlertType.INFORMATION, "Success", "Registration successful!");
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Validate login credentials
     */
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
            System.err.println("Error validating login: " + e.getMessage());
            return false;
        }
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
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load main view: " + e.getMessage());
        }
    }
}