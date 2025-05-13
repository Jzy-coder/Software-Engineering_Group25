package com.finance.gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RegisterDialog extends Dialog<String> {

    public RegisterDialog(Stage owner) {
        initOwner(owner);
        initStyle(StageStyle.UTILITY);
        setTitle("Register New User");

        // Set dialog style
        DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        // Create form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Create input fields
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        // Add form elements
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        // Add password requirement hint
        Label passwordHintLabel = new Label("Password must contain at least two types of characters from: uppercase letters, lowercase letters, numbers, and underscores");
        passwordHintLabel.setWrapText(true);
        passwordHintLabel.getStyleClass().add("hint-label");
        grid.add(passwordHintLabel, 0, 3, 2, 1);

        // Set dialog content
        dialogPane.setContent(grid);

        // Add buttons
        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(registerButtonType, cancelButtonType);

        // 设置结果转换器
        setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (username.isEmpty()) {
                    showError("Username cannot be empty");
                    return null;
                }

                if (password.isEmpty()) {
                    showError("Password cannot be empty");
                    return null;
                }

                // Validate password complexity
                boolean hasUpperCase = password.matches(".*[A-Z].*");
                boolean hasLowerCase = password.matches(".*[a-z].*");
                boolean hasDigit = password.matches(".*\\d.*");
                boolean hasUnderscore = password.matches(".*_.*");
                
                int characterTypeCount = (hasUpperCase ? 1 : 0) + 
                                         (hasLowerCase ? 1 : 0) + 
                                         (hasDigit ? 1 : 0) + 
                                         (hasUnderscore ? 1 : 0);
                
                if (characterTypeCount < 2) {
                    showError("Password must contain at least two types of characters from: uppercase letters, lowercase letters, numbers, and underscores");
                    return null;
                }

                if (!password.equals(confirmPassword)) {
                    showError("Passwords do not match");
                    return null;
                }

                return username + "#" + password;
            }
            return null;
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // 应用CSS样式
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        // 修改按钮文本
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(
            new ButtonType("OK", ButtonBar.ButtonData.OK_DONE)
        );
        
        alert.showAndWait();
    }
}