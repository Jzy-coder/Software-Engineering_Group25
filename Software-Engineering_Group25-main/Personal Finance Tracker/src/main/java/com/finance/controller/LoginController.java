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
            showAlert(AlertType.ERROR, "错误", "用户名和密码不能为空");
            return;
        }
        
        if (LoginManager.validateLogin(username, password)) {
            showAlert(AlertType.INFORMATION, "成功", "登录成功！");
            openMainView();
        } else {
            showAlert(AlertType.ERROR, "错误", "用户名或密码错误");
        }
    }
    
    /**
     * Show register dialog
     */
    @FXML
    private void showRegisterDialog() {
        // 创建自定义对话框
        Stage dialogStage = new Stage();
        dialogStage.setTitle("注册新用户");
        dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
        dialogStage.initOwner(loginButton.getScene().getWindow());
        dialogStage.initStyle(StageStyle.UTILITY);
        
        // 创建注册表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        
        TextField newUsernameField = new TextField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        
        grid.add(new Label("用户名:"), 0, 0);
        grid.add(newUsernameField, 1, 0);
        grid.add(new Label("密码:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("确认密码:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(new Label("密码必须包含大写字母、小写字母、数字、下划线中至少两种"), 0, 3, 2, 1);
        
        // 创建按钮
        Button registerButton = new Button("注册");
        Button cancelButton = new Button("取消");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, cancelButton);
        
        grid.add(buttonBox, 0, 4, 2, 1);
        
        // 设置注册按钮动作
        registerButton.setOnAction(e -> {
            String username = newUsernameField.getText();
            String password = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            // 验证输入
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(AlertType.ERROR, "错误", "用户名和密码不能为空");
                return; // 不关闭对话框，返回到表单
            }
            
            // 验证用户名只能包含字母、数字和下划线
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                showAlert(AlertType.ERROR, "错误", "用户名只能包含字母、数字和下划线");
                return; // 不关闭对话框，返回到表单
            }
            
            // 验证用户名是否已存在
            File userFile = new File("UserInfo" + File.separator + username + ".txt");
            if (userFile.exists()) {
                showAlert(AlertType.ERROR, "错误", "用户名已存在，请使用其他用户名");
                return; // 不关闭对话框，返回到表单
            }
            
            // 先验证两次输入的密码是否一致
            if (!password.equals(confirmPassword)) {
                showAlert(AlertType.ERROR, "错误", "两次输入的密码不一致");
                return; // 不关闭对话框，返回到表单
            }
            
            // 再验证密码是否至少包含两类字符
            boolean hasUpperCase = password.matches(".*[A-Z].*");
            boolean hasLowerCase = password.matches(".*[a-z].*");
            boolean hasDigit = password.matches(".*\\d.*");
            boolean hasUnderscore = password.matches(".*_.*");
            
            int characterTypeCount = (hasUpperCase ? 1 : 0) + 
                                     (hasLowerCase ? 1 : 0) + 
                                     (hasDigit ? 1 : 0) + 
                                     (hasUnderscore ? 1 : 0);
            
            if (characterTypeCount < 2) {
                showAlert(AlertType.ERROR, "错误", "密码必须至少包含大写字母、小写字母、数字、下划线中的两类字符");
                return; // 不关闭对话框，返回到表单
            }
            
            // 所有验证通过，保存用户信息
            String hashedPassword = hashPassword(password);
            saveUserToFile(username, hashedPassword);
            showAlert(AlertType.INFORMATION, "成功", "注册成功！");
            
            // 关闭对话框
            dialogStage.close();
        });
        
        // 设置取消按钮动作
        cancelButton.setOnAction(e -> dialogStage.close());
        
        // 创建场景并显示
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
            showAlert(AlertType.ERROR, "错误", "无法保存用户信息");
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
            stage.setWidth(1024);
            stage.setHeight(768);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load main view: " + e.getMessage());
        }
    }
}