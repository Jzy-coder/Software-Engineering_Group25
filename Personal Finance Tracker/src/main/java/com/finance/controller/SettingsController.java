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
        welcomeLabel.setText("您好，" + LoginManager.getCurrentUsername());
    }

    /**
     * 处理用户名修改
     */
    @FXML
    private void handleNameChange() {
        // 创建一个文本输入对话框
        TextInputDialog dialog = new TextInputDialog(LoginManager.getCurrentUsername());
        dialog.setTitle("修改用户名");
        dialog.setHeaderText("请输入新的用户名");
        dialog.setContentText("用户名:");

        // 获取用户输入
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newUsername -> {
            if (validateUsername(newUsername)) {
                updateUsername(newUsername);
            }
        });
    }

    /**
     * 验证用户名
     */
    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "错误", "用户名不能为空");
            return false;
        }
        if (username.length() > 20) {
            showAlert(Alert.AlertType.ERROR, "错误", "用户名不能超过20个字符");
            return false;
        }
        // 验证用户名只能包含字母、数字和下划线
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showAlert(Alert.AlertType.ERROR, "错误", "用户名只能包含字母、数字和下划线");
            return false;
        }
        
        // 检查新用户名是否已存在
        File userFile = new File("UserInfo" + File.separator + username + ".txt");
        if (userFile.exists() && !username.equals(LoginManager.getCurrentUsername())) {
            showAlert(Alert.AlertType.ERROR, "错误", "用户名已存在，请使用其他用户名");
            return false;
        }
        
        return true;
    }

    /**
     * 更新用户名
     */
    private void updateUsername(String newUsername) {
        String oldUsername = LoginManager.getCurrentUsername();
        
        if (!oldUsername.equals(newUsername)) {
            try {
                // 旧用户文件
                File oldUserFile = new File("UserInfo" + File.separator + oldUsername + ".txt");
                // 新用户文件
                File newUserFile = new File("UserInfo" + File.separator + newUsername + ".txt");
                
                // 确保UserInfo目录存在
                File userInfoDir = new File("UserInfo");
                if (!userInfoDir.exists()) {
                    userInfoDir.mkdirs();
                }
                
                if (oldUserFile.exists()) {
                    // 读取旧文件内容
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
                    
                    // 写入新文件
                    try (PrintWriter writer = new PrintWriter(new FileWriter(newUserFile))) {
                        for (String line : lines) {
                            writer.println(line);
                        }
                    }
                    
                    // 如果新文件创建成功，删除旧文件
                    if (newUserFile.exists()) {
                        oldUserFile.delete();
                    }
                }
                
                // 更新当前用户名
                LoginManager.setCurrentUsername(newUsername);
                
                // 更新欢迎标签
                welcomeLabel.setText("您好，" + newUsername);
                
                showAlert(Alert.AlertType.INFORMATION, "成功", "用户名修改成功");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "错误", "修改用户名时发生错误: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "提示", "新用户名与当前用户名相同，未进行修改");
        }
    }

    /**
     * 处理密码修改
     */
    @FXML
    private void handlePasswordChange() {
        // 创建对话框
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("修改密码");
        dialog.setHeaderText("请输入新密码");
        
        // 设置按钮
        ButtonType confirmButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        // 创建对话框内容
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        
        grid.add(new Label("新密码:"), 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(new Label("确认密码:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);
        grid.add(new Label("密码要求: 包含大写字母、小写字母、数字、下划线中至少两类"), 0, 2, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // 显示对话框并处理结果
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            if (validatePassword(newPassword, confirmPassword)) {
                LoginManager.updatePassword(newPassword);
                showAlert(Alert.AlertType.INFORMATION, "成功", "密码修改成功");
            }
        }
    }

    /**
     * 验证密码
     */
    private boolean validatePassword(String password, String confirmPassword) {
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "错误", "密码不能为空");
            return false;
        }
        
        // 先验证两次输入的密码是否一致
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "错误", "两次输入的密码不一致");
            return false;
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
            showAlert(Alert.AlertType.ERROR, "错误", "密码必须至少包含大写字母、小写字母、数字、下划线中的两类字符");
            return false;
        }
        
        return true;
    }

    /**
     * 处理性别修改
     */
    @FXML
    private void handleGenderChange() {
        // 创建对话框
        ChoiceDialog<String> dialog = new ChoiceDialog<>("男", "男", "女", "其他");
        dialog.setTitle("修改性别");
        dialog.setHeaderText("请选择您的性别");
        dialog.setContentText("性别:");
        
        // 设置默认选择
        String currentGender = UserInfoManager.getGender();
        if (!currentGender.isEmpty()) {
            dialog.setSelectedItem(currentGender);
        }
        
        // 获取用户选择
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(gender -> {
            UserInfoManager.setGender(gender);
            showAlert(Alert.AlertType.INFORMATION, "成功", "性别修改成功");
        });
    }

    /**
     * 处理地区修改
     */
    @FXML
    private void handleAreaChange() {
        // 创建文本输入对话框
        TextInputDialog dialog = new TextInputDialog(UserInfoManager.getArea());
        dialog.setTitle("修改地区");
        dialog.setHeaderText("请输入您所在的地区");
        dialog.setContentText("地区:");
        
        // 获取用户输入
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(area -> {
            if (!area.trim().isEmpty()) {
                UserInfoManager.setArea(area);
                showAlert(Alert.AlertType.INFORMATION, "成功", "地区修改成功");
            } else {
                showAlert(Alert.AlertType.ERROR, "错误", "地区不能为空");
            }
        });
    }

    /**
     * 处理职业修改
     */
    @FXML
    private void handleOccupationChange() {
        // 创建文本输入对话框
        TextInputDialog dialog = new TextInputDialog(UserInfoManager.getOccupation());
        dialog.setTitle("修改职业");
        dialog.setHeaderText("请输入您的职业");
        dialog.setContentText("职业:");
        
        // 获取用户输入
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(occupation -> {
            if (!occupation.trim().isEmpty()) {
                UserInfoManager.setOccupation(occupation);
                showAlert(Alert.AlertType.INFORMATION, "成功", "职业修改成功");
            } else {
                showAlert(Alert.AlertType.ERROR, "错误", "职业不能为空");
            }
        });
    }

    /**
     * 处理切换账户
     */
    @FXML
    private void handleSwitchAccount() {
        try {
            // 加载登录视图
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            // 获取当前窗口
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            
            // 设置新场景
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            
            // 设置窗口属性
            stage.setTitle("Personal Finance Assistant - Login");
            stage.setScene(scene);
            stage.setWidth(600);
            stage.setHeight(450);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "错误", "无法加载登录页面: " + e.getMessage());
        }
    }

    /**
     * 处理退出登录
     */
    @FXML
    private void handleLogout() {
        Platform.exit();
    }

    /**
     * 显示警告对话框
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}