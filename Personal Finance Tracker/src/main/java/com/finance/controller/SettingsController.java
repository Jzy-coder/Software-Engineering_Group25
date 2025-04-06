package com.finance.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.finance.app.FinanceApplication;
import com.finance.gui.LoginManager;
import com.finance.gui.UserInfoManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SettingsController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         welcomeLabel.setText("Hello, " + LoginManager.getCurrentUsername());
    }

    /**
     * Handle username change
     */
    @FXML
    private void handleNameChange() {
        // Create a text input dialog
        TextInputDialog dialog = new TextInputDialog(LoginManager.getCurrentUsername());
        dialog.setTitle("Change Username");
        dialog.setHeaderText("Please enter new username");
        dialog.setContentText("Username:");

        // Get user input
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newUsername -> {
            if (validateUsername(newUsername)) {
                updateUsername(newUsername);
            }
        });
    }

    /**
     * Validate username
     */
    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username cannot be empty");
            return false;
        }
        if (username.length() > 20) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username cannot exceed 20 characters");
            return false;
        }
        // Validate username can only contain letters, numbers and underscores
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username can only contain letters, numbers and underscores");
            return false;
        }
        
        // Check if new username already exists
        File userFile = new File("UserInfo" + File.separator + username + ".txt");
        if (userFile.exists() && !username.equals(LoginManager.getCurrentUsername())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists, please use a different one");
            return false;
        }
        
        return true;
    }

    /**
     * Update username
     */
    private void updateUsername(String newUsername) {
        String oldUsername = LoginManager.getCurrentUsername();
        
        if (!oldUsername.equals(newUsername)) {
            try {
                // Old user file
                File oldUserFile = new File("UserInfo" + File.separator + oldUsername + ".txt");
                // New user file
                File newUserFile = new File("UserInfo" + File.separator + newUsername + ".txt");
                
                // Ensure UserInfo directory exists
                File userInfoDir = new File("UserInfo");
                if (!userInfoDir.exists()) {
                    userInfoDir.mkdirs();
                }
                
                if (oldUserFile.exists()) {
                    // Read old file content
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
                    
                    // Write to new file
                    try (PrintWriter writer = new PrintWriter(new FileWriter(newUserFile))) {
                        for (String line : lines) {
                            writer.println(line);
                        }
                    }
                    
                    // If new file is created successfully, delete the old file
                    if (newUserFile.exists()) {
                        oldUserFile.delete();
                    }
                }
                
                // Update current username
                LoginManager.setCurrentUsername(newUsername);
                
                // Update welcome label
                welcomeLabel.setText("Hello, " + newUsername);
                // 更新界面上的用户名标签
                usernameLabel.setText(newUsername);
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Username changed successfully");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Error occurred while changing username: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Notice", "New username is same as current one, no changes made");
        }
    }

    /**
     * Handle password change
     */
    @FXML
    private void handlePasswordChange() {
        // Create dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Please enter new password");
        
        // Set buttons
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        // Create dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        
        grid.add(new Label("New Password:"), 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);
        grid.add(new Label("Password must contain at least two types of: uppercase letters, lowercase letters, numbers, underscores"), 0, 2, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Show dialog and handle result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            if (validatePassword(newPassword, confirmPassword)) {
                LoginManager.updatePassword(newPassword);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully");
            }
        }
    }

    /**
     * Validate password
     */
    private boolean validatePassword(String password, String confirmPassword) {
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password cannot be empty");
            return false;
        }
        
        // First validate if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match");
            return false;
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
            showAlert(Alert.AlertType.ERROR, "Error", "Password must contain at least two types of: uppercase letters, lowercase letters, numbers, underscores");
            return false;
        }
        
        return true;
    }

    /**
     * Handle gender change
     */
    @FXML
    private void handleGenderChange() {
        // Create dialog
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Male", "Male", "Female", "Other");
        dialog.setTitle("Change Gender");
        dialog.setHeaderText("Please select your gender");
        dialog.setContentText("Gender:");
        
        // Set default selection
        String currentGender = UserInfoManager.getGender();
        if (!currentGender.isEmpty()) {
            dialog.setSelectedItem(currentGender);
        }
        
        // Get user selection
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(gender -> {
            UserInfoManager.setGender(gender);
            // 更新界面上的性别标签
            genderLabel.setText(gender);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Gender changed successfully");
        });
    }

    /**
     * Handle area change
     */
    @FXML
    private void handleAreaChange() {
        // Create text input dialog
        TextInputDialog dialog = new TextInputDialog(UserInfoManager.getArea());
        dialog.setTitle("Change Area");
        dialog.setHeaderText("Please enter your area");
        dialog.setContentText("Area:");
        
        // Get user input
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(area -> {
            if (!area.trim().isEmpty()) {
                UserInfoManager.setArea(area);
                // 更新界面上的地区标签
                regionLabel.setText(area);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Area changed successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Area cannot be empty");
            }
        });
    }

    /**
     * Handle occupation change
     */
    @FXML
    private void handleOccupationChange() {
        // Create text input dialog
        TextInputDialog dialog = new TextInputDialog(UserInfoManager.getOccupation());
        dialog.setTitle("Change Occupation");
        dialog.setHeaderText("Please enter your occupation");
        dialog.setContentText("Occupation:");
        
        // Get user input
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(occupation -> {
            if (!occupation.trim().isEmpty()) {
                UserInfoManager.setOccupation(occupation);
                // 更新界面上的职业标签
                occupationLabel.setText(occupation);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Occupation changed successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Occupation cannot be empty");
            }
        });
    }

    /**
     * Handle account switching
     */
    @FXML
    private void handleSwitchAccount() {
        try {
            // Load login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            // Get current window
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            
            // Set new scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            // Set window properties
            stage.setTitle("Personal Finance Assistant - Login");
            stage.setScene(scene);
            stage.setWidth(600);
            stage.setHeight(450);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login page: " + e.getMessage());
        }
    }

    /**
     * Handle logout
     */
    @FXML
    private void handleLogout() {
        Platform.exit();
    }

    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
