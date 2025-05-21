package com.finance.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import com.finance.gui.LoginManager;
import com.finance.gui.RegisterDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
            // Synchronize ComboBox editor content only when ComboBox is not shown or not focused, to avoid overwriting user input
            if (!usernameCombo.isShowing() && !usernameCombo.getEditor().isFocused()) {
                usernameCombo.getEditor().setText(newVal);
            }
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
            // Real-time update of text field content
            usernameCombo.getEditor().setText(newVal);
            // Check for matching saved usernames
            updateComboBoxItems(newVal);
            if (!usernameCombo.getItems().isEmpty() && usernameField.isFocused()) {
                usernameCombo.setManaged(true);
                usernameCombo.setVisible(true);
                usernameCombo.show();
            } else {
                usernameCombo.setManaged(false);
                usernameCombo.setVisible(false);
                usernameCombo.hide();
            }
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
        Stage stage = (Stage) loginButton.getScene().getWindow();
        RegisterDialog dialog = new RegisterDialog(stage);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result != null) {
                String[] parts = result.split("#");
                String username = parts[0];
                String password = parts[1];
                
                // Register new user
                if (LoginManager.registerUser(username, password)) {
                    showAlert(AlertType.INFORMATION, "Success", "Registration is successful! Please log in with the new account.");
                } else {
                    showAlert(AlertType.ERROR, "Error", "The username already exists.");
                }
            }
        });
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
    public void saveUserToFile(String username, String hashedPassword) {
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
     * Show alert dialog
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // apply css
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        // mpdify the text color
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(
            new ButtonType("OK", ButtonBar.ButtonData.OK_DONE)
        );
        
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
