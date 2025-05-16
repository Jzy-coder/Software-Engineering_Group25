package com.finance.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.finance.model.Budget;
import com.finance.util.BudgetDataManager;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.DialogPane;

public class BudgetController implements Initializable {

    // UI Components
    @FXML private VBox singleBudgetContainer;
    @FXML private ListView<String> planListView;
    @FXML private Label budgetBalanceLabel;
    @FXML private GridPane inputGrid;
    @FXML private TextField budgetNameField;
    @FXML private TextField plannedAmountField;
    @FXML private TextField actualAmountField;

    // Data
    private Budget currentBudget;
    private ObservableList<String> plans = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentBudget = BudgetDataManager.loadBudget();
        if (currentBudget != null) {
            plans.setAll(currentBudget.getPlans()); // 转换为 ObservableList
            planListView.setItems(plans);
            refreshSingleBudgetDisplay();
        }
    }

    //================ Budget Management ================//
    // 修改后的 handleAddBudget 方法
    @FXML
    private void handleAddBudget() {
        // 如果当前存在预算，检查其进度
        if (currentBudget != null) {
            double progress = currentBudget.getActualAmount() / currentBudget.getPlannedAmount();
            if (progress < 1.0) {
                // 如果进度未达到100%，清除所有计划并保存
                plans.clear();
                currentBudget.setPlans(plans);
                BudgetDataManager.saveBudget(currentBudget);
                planListView.setItems(plans);
            }
        }

        // 创建对话框
        Dialog<ButtonType> dialog = new Dialog<>(); 
        dialog.setTitle("Add Budget");
        dialog.setHeaderText("Enter budget details");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("confirmation-dialog"); // 添加CSS类
    
        // 设置按钮类型 - 使用自定义ButtonType确保英文显示
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);
    
        // 构建输入表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
    
        TextField nameField = new TextField();
        TextField plannedField = new TextField();
        TextField actualField = new TextField();
    
        grid.addRow(0, new Label("Budget Name:"), nameField);
        grid.addRow(1, new Label("Planned Amount:"), plannedField);
        grid.addRow(2, new Label("Actual Amount:"), actualField);
    
        dialog.getDialogPane().setContent(grid);
    
        // 处理结果
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            try {
                String name = nameField.getText().trim();
                String plannedText = plannedField.getText().trim();
                String actualText = actualField.getText().trim();

                // 验证输入是否为纯数字（可以包含小数点）
                if (!plannedText.matches("^\\d*\\.?\\d+$")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Planned amount must be a valid number!", "error-alert");
                    return;
                }
                if (!actualText.matches("^\\d*\\.?\\d+$")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Actual amount must be a valid number!", "error-alert");
                    return;
                }

                double planned = Double.parseDouble(plannedText);
                double actual = Double.parseDouble(actualText);
    
                // 输入验证
                if (name.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Budget name cannot be empty!", "error-alert");
                    return;
                }
                if (planned <= 0 || actual < 0 || planned < actual) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid amounts. Ensure:\n- Planned > 0\n- Actual ≥ 0\n- Planned >= Actual", "error-alert");
                    return;
                }
    
                // 创建新预算并保存
                currentBudget = new Budget(name, planned, actual);
                BudgetDataManager.saveBudget(currentBudget);
                // 显式添加到历史记录中，因为这是新创建的预算
                BudgetDataManager.addBudgetToHistory(currentBudget);
    
                // 强制刷新界面
                refreshSingleBudgetDisplay();
                updateBudgetBalance();
    
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numbers!", "error-alert");
            }
        }
    }

    
    @FXML
    private void handleEditBudget() {
        if (currentBudget != null) {
            showBudgetDialog("Edit Budget", 
                currentBudget.getName(), 
                currentBudget.getPlannedAmount(), 
                currentBudget.getActualAmount(),
                "confirmation-dialog" // 添加CSS类
            );
        }
    }
    
    @FXML
    private void handleReviewBudget() {
        List<Budget> budgetHistory = BudgetDataManager.loadBudgetHistory();
        
        if (budgetHistory.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Information", "No budget history found.", "info-alert");
            return;
        }
        
        // 移除重复的预算记录（保留最新的）
        for (int i = budgetHistory.size() - 1; i >= 0; i--) {
            Budget current = budgetHistory.get(i);
            for (int j = i - 1; j >= 0; j--) {
                Budget compare = budgetHistory.get(j);
                if (current.getName().equals(compare.getName()) &&
                    Math.abs(current.getPlannedAmount() - compare.getPlannedAmount()) < 0.01 &&
                    Math.abs(current.getActualAmount() - compare.getActualAmount()) < 0.01) {
                    budgetHistory.remove(j);
                    i--;
                }
            }
        }
        
        // Create dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Budget History Review");
        dialog.setHeaderText("Your Budget History");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("review-dialog"); // 添加CSS类
        
        // 使用自定义ButtonType确保英文显示
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        // Create content container
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        
        // Add each budget to the dialog - using read-only version for history items
        for (Budget budget : budgetHistory) {
            HBox budgetItem = createReadOnlyBudgetItem(
                budget.getName(),
                budget.getPlannedAmount(),
                budget.getActualAmount()
            );
            contentBox.getChildren().add(budgetItem);
        }
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.showAndWait();
    }

    @FXML
    private void handleRemoveBudget() {
        if (currentBudget != null) {
            // 确保当前预算已添加到历史记录中，这样即使删除当前预算，历史记录中仍然保留
            BudgetDataManager.addBudgetToHistory(currentBudget);
            
            // 删除当前活动预算
            BudgetDataManager.saveBudget(null);
            currentBudget = null;
            // 清空plans列表
            plans.clear();
            planListView.setItems(plans); // 更新UI显示
            refreshSingleBudgetDisplay();
            updateBudgetBalance(); // 更新余额为0
            showAlert(Alert.AlertType.INFORMATION, "Success", "Budget removed successfully", "success-alert");
        }
    }

    //================ Plan Management ================//
        @FXML
    private void handleAddPlan() {
        // 检查是否存在预算，如果不存在则显示提示并返回
        if (currentBudget == null) {
            showAlert(Alert.AlertType.WARNING, "No Budget", "Please create a budget first before adding plans.", "warning-alert");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Plan");
        dialog.setHeaderText("Enter plan description:");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("input-dialog"); // 添加CSS类
        
        // 显式设置按钮类型（TextInputDialog 默认已有 OK 和 Cancel）
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        // 正确获取输入结果（返回 Optional<String>）
        Optional<String> result = dialog.showAndWait();

        // 检查用户是否点击了 OK（通过 Optional<String> 的存在性判断）
        if (result.isPresent()) {
            String plan = result.get().trim(); // 直接获取输入的字符串
            if (!plan.isEmpty()) {
                plans.add(plan);
                currentBudget.setPlans(plans);
                BudgetDataManager.saveBudget(currentBudget);
                
                // 更新历史记录中的预算，确保plan实时同步到budget history
                Budget oldBudget = new Budget(currentBudget.getName(), 
                                            currentBudget.getPlannedAmount(), 
                                            currentBudget.getActualAmount());
                // 设置旧的plans列表（不包含新添加的plan）
                List<String> oldPlans = new java.util.ArrayList<>(plans);
                oldPlans.remove(oldPlans.size() - 1); // 移除最后添加的plan
                oldBudget.setPlans(oldPlans);
                
                // 更新历史记录
                BudgetDataManager.updateBudgetInHistory(oldBudget, currentBudget);
                
                planListView.setItems(plans); // 强制刷新列表
            }
        }
    }

    @FXML
    private void handleRemovePlan() {
        int selectedIndex = planListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            // 保存编辑前的预算信息，用于在历史记录中查找匹配项
            Budget oldBudget = new Budget(currentBudget.getName(), 
                                        currentBudget.getPlannedAmount(), 
                                        currentBudget.getActualAmount());
            oldBudget.setPlans(currentBudget.getPlans());
            
            plans.remove(selectedIndex);
            currentBudget.setPlans(plans); // 更新计划列表
            BudgetDataManager.saveBudget(currentBudget);
            
            // 更新历史记录中的预算项
            BudgetDataManager.updateBudgetInHistory(oldBudget, currentBudget);
        }
    }

    //================ Core Logic ================//
    private void showBudgetDialog(String title, String name, double planned, double actual, String styleClass) {
        // 改用 Dialog<ButtonType> 替代 TextInputDialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Edit budget details");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        if (styleClass != null && !styleClass.isEmpty()) {
            dialogPane.getStyleClass().add(styleClass);
        }
        
        // 添加 OK 和 Cancel 按钮
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        // 构建输入表单（原有代码不变）
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(name);
        TextField plannedField = new TextField(String.valueOf(planned));
        TextField actualField = new TextField(String.valueOf(actual));

        grid.addRow(0, new Label("Budget Name:"), nameField);
        grid.addRow(1, new Label("Planned Amount:"), plannedField);
        grid.addRow(2, new Label("Actual Amount:"), actualField);

        dialog.getDialogPane().setContent(grid);

        // 处理结果
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) { // 明确检查 OK 按钮
            try {
                String newName = nameField.getText().trim();
                double newPlanned = Double.parseDouble(plannedField.getText());
                double newActual = Double.parseDouble(actualField.getText());

                //validateInput(newName, newPlanned, newActual); // 将由新的showAlert处理

                // 输入验证
                if (newName.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Budget name cannot be empty!", "error-alert");
                    return;
                }
                if (newPlanned <= 0 || newActual < 0 || newPlanned < newActual) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid amounts. Ensure:\n- Planned > 0\n- Actual ≥ 0\n- Planned >= Actual", "error-alert");
                    return;
                }
                
                // 检查是否达成目标（actual等于planned）
                if (Math.abs(newActual - newPlanned) < 0.01) { // 使用近似相等来处理浮点数比较
                    // 保存编辑前的预算信息，用于在历史记录中查找匹配项
                    Budget oldBudget = new Budget(currentBudget.getName(), 
                                                currentBudget.getPlannedAmount(), 
                                                currentBudget.getActualAmount());
                    oldBudget.setPlans(currentBudget.getPlans());
                    
                    // 更新当前预算（虽然即将删除，但先更新历史记录）
                    currentBudget.setName(newName);
                    currentBudget.setPlannedAmount(newPlanned);
                    currentBudget.setActualAmount(newActual);
                    
                    // 更新历史记录中的预算项
                    BudgetDataManager.updateBudgetInHistory(oldBudget, currentBudget);
                    
                    // 显示祝贺弹窗
                    Alert congratsAlert = new Alert(Alert.AlertType.INFORMATION);
                    congratsAlert.setTitle("Goal Achieved");
                    congratsAlert.setHeaderText("Congratulations!");
                    congratsAlert.setContentText("Congratulations on achieving this goal!");
                    DialogPane congratsDialogPane = congratsAlert.getDialogPane();
                    congratsDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    congratsDialogPane.getStyleClass().add("success-alert"); // 添加CSS类
                    
                    // 自定义按钮文本为英文
                    ButtonType okButtonAlert = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    congratsAlert.getButtonTypes().setAll(okButtonAlert);
                    
                    congratsAlert.showAndWait();
                    
                    // 自动删除该预算
                    handleRemoveBudget();
                } else {
                    // 保存编辑前的预算信息，用于在历史记录中查找匹配项
                    Budget oldBudget = new Budget(currentBudget.getName(), 
                                                currentBudget.getPlannedAmount(), 
                                                currentBudget.getActualAmount());
                    oldBudget.setPlans(currentBudget.getPlans());
                    
                    // 直接更新现有对象，避免创建新实例
                    currentBudget.setName(newName);
                    currentBudget.setPlannedAmount(newPlanned);
                    currentBudget.setActualAmount(newActual);
                    BudgetDataManager.saveBudget(currentBudget);
                    
                    // 更新历史记录中的预算项
                    BudgetDataManager.updateBudgetInHistory(oldBudget, currentBudget);
                    
                    refreshSingleBudgetDisplay();
                    updateBudgetBalance(); // 触发余额更新
                }

                } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid number format", "error-alert");
                } catch (IllegalArgumentException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", e.getMessage(), "error-alert");
                }
        }
    }
    
    // private void validateInput(String name, double planned, double actual) {
    //     if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
    //     if (planned <= 0) throw new IllegalArgumentException("Planned amount must > 0");
    //     if (actual < 0) throw new IllegalArgumentException("Actual amount cannot be negative");
    //     if (planned < actual) throw new IllegalArgumentException("Planned must >= Actual");
    //     // Note: We now allow actual == planned for goal achievement
    // }
    // Validation logic is now integrated into methods calling showAlert with appropriate styleClass.

    // 修改后的 refreshSingleBudgetDisplay 方法
    private void refreshSingleBudgetDisplay() {
        singleBudgetContainer.getChildren().clear(); // 确保清空旧内容

        if (currentBudget != null) {
            HBox budgetItem = createBudgetItem(
                currentBudget.getName(),
                currentBudget.getPlannedAmount(),
                currentBudget.getActualAmount()
            );
            singleBudgetContainer.getChildren().add(budgetItem); // 只添加一次
        }
    }
    

    //================ UI Components ================//


    private HBox createBudgetItem(String name, double planned, double actual) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");

        // ==================== 进度条部分 ====================
        VBox progressBox = new VBox(8);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ProgressBar progressBar = new ProgressBar(actual / planned);
        progressBar.setPrefWidth(300);
      

        // 动态监听进度变化并应用颜色样式
            progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
            double progress = newVal.doubleValue();
            progressBar.getStyleClass().removeAll("warning", "caution", "safe"); // 移除所有旧状态

            // 输出当前进度，帮助调试
            System.out.println("Current Progress: " + progress);

            // 根据进度值添加对应的状态
            if (progress < 0.4) {
                progressBar.getStyleClass().add("warning"); // 红色
            } else if (progress < 0.6) {
                progressBar.getStyleClass().add("caution"); // 黄色
            } else {
                progressBar.getStyleClass().add("safe"); // 绿色
            }
        });



        // 百分比标签（叠加在进度条上）
        Label progressLabel = new Label();
        progressLabel.textProperty().bind(
            Bindings.format("%.0f%%", progressBar.progressProperty().multiply(100))
        );
        progressLabel.getStyleClass().add("progress-label");

        StackPane progressStack = new StackPane();
        progressStack.getChildren().addAll(progressBar, progressLabel); // 正确添加进度条和标签

        // 详细金额标签
        Label detailLabel = new Label(String.format("Planned: ￥%.2f | Actual: ￥%.2f", planned, actual));
        detailLabel.setStyle("-fx-text-fill: #666;");

        progressBox.getChildren().addAll(nameLabel, progressStack, detailLabel); // 替换为 progressStack

        // ==================== 操作按钮 ====================
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        editBtn.setOnAction(e -> showBudgetDialog("Edit Budget", name, planned, actual, "confirmation-dialog"));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            // 从历史记录中删除该预算项
            List<Budget> tempBudgetHistory = BudgetDataManager.loadBudgetHistory();
            tempBudgetHistory.removeIf(b -> b.getName().equals(name) && 
                                  Math.abs(b.getPlannedAmount() - planned) < 0.01 && 
                                  Math.abs(b.getActualAmount() - actual) < 0.01);
            BudgetDataManager.saveBudgetHistory(tempBudgetHistory);
            
            // 自定义确认弹窗按钮文本为英文
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm deletion");
            confirmDialog.setHeaderText("Delete confirmation");
            confirmDialog.setContentText("Once deleted, it cannot be modified anymore. Are you sure you want to delete it?");
            DialogPane confirmDialogPane = confirmDialog.getDialogPane();
            confirmDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            confirmDialogPane.getStyleClass().add("confirmation-dialog"); // 添加CSS类
            
            // 自定义按钮文本为英文
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmDialog.getButtonTypes().setAll(okButton, cancelButton);
            
            // 等待用户确认
            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                singleBudgetContainer.getChildren().remove(container); // 动态移除当前项
                handleRemoveBudget(); // 调用控制器方法
            }
        });

        container.getChildren().addAll(progressBox, editBtn, deleteBtn);
        return container;
    }
    
    /**
     * 创建只读的预算项目，用于历史预算记录显示
     * 与createBudgetItem类似，但不包含Edit按钮，只有Delete按钮
     */
    private HBox createReadOnlyBudgetItem(String name, double planned, double actual) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");

        // ==================== 进度条部分 ====================
        VBox progressBox = new VBox(8);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ProgressBar progressBar = new ProgressBar(actual / planned);
        progressBar.setPrefWidth(300);
      
        // 动态监听进度变化并应用颜色样式
        progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
            double progress = newVal.doubleValue();
            progressBar.getStyleClass().removeAll("warning", "caution", "safe"); // 移除所有旧状态

            // 根据进度值添加对应的状态
            if (progress < 0.4) {
                progressBar.getStyleClass().add("warning"); // 红色
            } else if (progress < 0.6) {
                progressBar.getStyleClass().add("caution"); // 黄色
            } else {
                progressBar.getStyleClass().add("safe"); // 绿色
            }
        });

        // 百分比标签（叠加在进度条上）
        Label progressLabel = new Label();
        progressLabel.textProperty().bind(
            Bindings.format("%.0f%%", progressBar.progressProperty().multiply(100))
        );
        progressLabel.getStyleClass().add("progress-label");

        StackPane progressStack = new StackPane();
        progressStack.getChildren().addAll(progressBar, progressLabel);

        // 详细金额标签
        Label detailLabel = new Label(String.format("Planned: ￥%.2f | Actual: ￥%.2f", planned, actual));
        detailLabel.setStyle("-fx-text-fill: #666;");

        // 查找对应的预算对象，以获取其计划列表
        Budget matchingBudget = null;
        List<Budget> budgetHistoryList = BudgetDataManager.loadBudgetHistory();
        for (Budget b : budgetHistoryList) {
            if (b.getName().equals(name) && 
                Math.abs(b.getPlannedAmount() - planned) < 0.01 && 
                Math.abs(b.getActualAmount() - actual) < 0.01) {
                matchingBudget = b;
                break;
            }
        }
        
        // 添加计划列表显示
        if (matchingBudget != null && !matchingBudget.getPlans().isEmpty()) {
            Label plansLabel = new Label("Plans:");
            plansLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 0 0;");
            
            ListView<String> plansListView = new ListView<>();
            plansListView.setPrefHeight(Math.min(matchingBudget.getPlans().size() * 24 + 2, 100)); // 动态调整高度，最大100
            plansListView.setItems(FXCollections.observableArrayList(matchingBudget.getPlans()));
            plansListView.setStyle("-fx-background-color: transparent;"); // 设置透明背景
            plansListView.setMouseTransparent(true); // 禁用鼠标事件
            plansListView.setFocusTraversable(false); // 禁用焦点
            plansListView.setCellFactory(param -> new javafx.scene.control.ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setText(item);
                        setStyle("-fx-background-color: transparent; -fx-text-fill: #666;"); // 设置文本颜色为灰色
                    }
                }
            });
            plansListView.getSelectionModel().clearSelection(); // 清除任何默认选择
            
            progressBox.getChildren().addAll(nameLabel, progressStack, detailLabel, plansLabel, plansListView);
        } else {
            progressBox.getChildren().addAll(nameLabel, progressStack, detailLabel);
        }

        // ==================== 操作按钮 ====================
        // 只添加Delete按钮，不添加Edit按钮
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            // 从历史记录中删除该预算项
            List<Budget> tempBudgetHistory = BudgetDataManager.loadBudgetHistory();
            tempBudgetHistory.removeIf(b -> b.getName().equals(name) && 
                                  Math.abs(b.getPlannedAmount() - planned) < 0.01 && 
                                  Math.abs(b.getActualAmount() - actual) < 0.01);
            BudgetDataManager.saveBudgetHistory(tempBudgetHistory);
            
            // 自定义确认弹窗按钮文本为英文
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm deletion");
            confirmDialog.setHeaderText("Delete confirmation");
            confirmDialog.setContentText("Once deleted, it cannot be modified anymore. Are you sure you want to delete it?");
            DialogPane confirmDialogPane = confirmDialog.getDialogPane();
            confirmDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            confirmDialogPane.getStyleClass().add("confirmation-dialog"); // 添加CSS类
            
            // 自定义按钮文本为英文
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmDialog.getButtonTypes().setAll(okButton, cancelButton);
            
            // 等待用户确认
            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                // 从UI中移除
                ((VBox) container.getParent()).getChildren().remove(container);
            }
        });

        container.getChildren().addAll(progressBox, deleteBtn);
        return container;
    }
    //================ Utilities ================//
    private void loadBudgetData() {
        currentBudget = BudgetDataManager.loadBudget();
        if (currentBudget != null) {
            plans.setAll(currentBudget.getPlans());
        }
    }

    private void updateBudgetBalance() {
        double balance = (currentBudget != null) ? 
            currentBudget.getPlannedAmount() - currentBudget.getActualAmount() : 0;
        budgetBalanceLabel.setText(String.format("Budget Balance: ￥%.2f", balance));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message, String styleClass) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // 应用CSS样式
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        if (styleClass != null && !styleClass.isEmpty()) {
            dialogPane.getStyleClass().add(styleClass);
        }

        // 修改按钮文本为英文
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(
            new ButtonType("OK", ButtonBar.ButtonData.OK_DONE)
        );

        alert.showAndWait();
    }



    // Overload for simple info alerts if no specific class is needed, or to maintain old calls
    private void showAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", message, "info-alert");
    }
}