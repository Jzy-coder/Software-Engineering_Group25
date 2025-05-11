package com.finance.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.finance.model.Budget;
import com.finance.util.BudgetDataManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
            plans.setAll(currentBudget.getPlans()); // 初始化计划列表
            planListView.setItems(plans);
            refreshSingleBudgetDisplay();
        }
        updateBudgetBalance();
    }

    //================ Budget Management ================//
    // 修改后的 handleAddBudget 方法
    @FXML
    private void handleAddBudget() {
        // 创建对话框
        Dialog<ButtonType> dialog = new Dialog<>(); // 改用 Dialog<ButtonType> 更清晰
        dialog.setTitle("Add Budget");
        dialog.setHeaderText("Enter budget details");

        // 设置按钮类型
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String name = nameField.getText().trim();
                double planned = Double.parseDouble(plannedField.getText());
                double actual = Double.parseDouble(actualField.getText());

                // 输入验证
                if (name.isEmpty()) {
                    showAlert("Budget name cannot be empty!");
                    return;
                }
                if (planned <= 0 || actual < 0 || planned <= actual) {
                    showAlert("Invalid amounts. Ensure:\n- Planned > 0\n- Actual ≥ 0\n- Planned > Actual");
                    return;
                }

                // 创建新预算并保存
                currentBudget = new Budget(name, planned, actual);
                BudgetDataManager.saveBudget(currentBudget);

                // 强制刷新界面
                refreshSingleBudgetDisplay();
                updateBudgetBalance();

            } catch (NumberFormatException e) {
                showAlert("Please enter valid numbers!");
            }
        }
    }

    
    @FXML
    private void handleEditBudget() {
        if (currentBudget != null) {
            showBudgetDialog("Edit Budget", 
                currentBudget.getName(), 
                currentBudget.getPlannedAmount(), 
                currentBudget.getActualAmount()
            );
        }
    }

    @FXML
    private void handleRemoveBudget() {
        if (currentBudget != null) {
            BudgetDataManager.saveBudget(null);
            currentBudget = null;
            refreshSingleBudgetDisplay();
            showAlert("Budget removed successfully");
        }
    }

    //================ Plan Management ================//
        @FXML
    private void handleAddPlan() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Plan");
        dialog.setHeaderText("Enter plan description:");
        
        // 显式设置按钮类型（TextInputDialog 默认已有 OK 和 Cancel）
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // 正确获取输入结果（返回 Optional<String>）
        Optional<String> result = dialog.showAndWait();

        // 检查用户是否点击了 OK（通过 Optional<String> 的存在性判断）
        if (result.isPresent()) {
            String plan = result.get().trim(); // 直接获取输入的字符串
            if (!plan.isEmpty()) {
                plans.add(plan);
                currentBudget.setPlans(plans);
                BudgetDataManager.saveBudget(currentBudget);
                planListView.setItems(plans); // 强制刷新列表
            }
        }
    }

    @FXML
    private void handleRemovePlan() {
        int selectedIndex = planListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            plans.remove(selectedIndex);
            currentBudget.setPlans(plans); // 更新计划列表
            BudgetDataManager.saveBudget(currentBudget);
        }
    }

    //================ Core Logic ================//
    // 修改后的 showBudgetDialog 方法（编辑逻辑）
    private void showBudgetDialog(String title, String name, double planned, double actual) {
        // 改用 Dialog<ButtonType> 替代 TextInputDialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        // 添加 OK 和 Cancel 按钮
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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
        if (result.isPresent() && result.get() == ButtonType.OK) { // 明确检查 OK 按钮
            try {
                String newName = nameField.getText().trim();
                double newPlanned = Double.parseDouble(plannedField.getText());
                double newActual = Double.parseDouble(actualField.getText());

                validateInput(newName, newPlanned, newActual);

                // 直接更新现有对象，避免创建新实例
                currentBudget.setName(newName);
                currentBudget.setPlannedAmount(newPlanned);
                currentBudget.setActualAmount(newActual);
                BudgetDataManager.saveBudget(currentBudget);
                refreshSingleBudgetDisplay();

                } catch (NumberFormatException e) {
                        showAlert("Invalid number format");
                } catch (IllegalArgumentException e) {
                        showAlert(e.getMessage());
                }
        }
    }
    
    private void validateInput(String name, double planned, double actual) {
        if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (planned <= 0) throw new IllegalArgumentException("Planned amount must > 0");
        if (actual < 0) throw new IllegalArgumentException("Actual amount cannot be negative");
        if (planned <= actual) throw new IllegalArgumentException("Planned must > Actual");
    }

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

        // Progress Section
        VBox progressBox = new VBox(8);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ProgressBar progressBar = new ProgressBar(actual / planned);
        progressBar.setPrefWidth(300);
        
        Label detailLabel = new Label(String.format("Planned: $%.2f | Actual: $%.2f", planned, actual));
        detailLabel.setStyle("-fx-text-fill: #666;");

        progressBox.getChildren().addAll(nameLabel, progressBar, detailLabel);

        // Action Buttons
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        editBtn.setOnAction(e -> showBudgetDialog("Edit Budget", name, planned, actual));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> handleRemoveBudget());

        container.getChildren().addAll(progressBox, editBtn, deleteBtn);
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
        budgetBalanceLabel.setText(String.format("Budget Balance: $%.2f", balance));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("System Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}