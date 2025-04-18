package com.finance.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.finance.model.Budget;
import com.finance.util.BudgetDataManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BudgetController implements Initializable {
    @FXML
    private Button addButton;
    
    @FXML
    private GridPane inputGrid;
    
    @FXML
    private TextField budgetNameField;
    
    @FXML
    private TextField plannedAmountField;
    
    @FXML
    private TextField actualAmountField;
    
    @FXML
    private VBox budgetListContainer;

    @FXML
    private Label budgetBalanceLabel; // 新增的预算差额标签
    
    private int currentEditingIndex = -1;
    private List<Budget> budgets = new ArrayList<>();
    
    @FXML
    private void handleAddBudget() {
        resetInputFields();
        currentEditingIndex = -1;
        inputGrid.setVisible(true);
    }
    
    @FXML
    private void handleCancel() {
        inputGrid.setVisible(false);
        resetInputFields();
    }

    @FXML
    private void handleRefresh() {
        updateBudgetBalance();//刷新balance
    }
    
    @FXML
    private void handleConfirm() {
        try {
            String budgetName = budgetNameField.getText().trim();
            double plannedAmount = Double.parseDouble(plannedAmountField.getText());
            double actualAmount = Double.parseDouble(actualAmountField.getText());
            
            // Validate budget name
            if (budgetName.isEmpty()) {
                showAlert("Please enter a budget name");
                return;
            }
            
            if (plannedAmount <= 0) {
                showAlert("Planned amount must be greater than 0");
                return;
            }
            
            if (actualAmount < 0) {
                showAlert("Current amount cannot be negative");
                return;
            }
            
            // Validate planned amount must be greater than current amount
            if (plannedAmount <= actualAmount) {
                showAlert("Planned amount must be greater than current amount");
                return;
            }
            
            if (currentEditingIndex >= 0) {
                updateBudgetItem(currentEditingIndex, budgetName, plannedAmount, actualAmount);
            } else {
                addNewBudgetItem(budgetName, plannedAmount, actualAmount);
            }
            
            inputGrid.setVisible(false);
            resetInputFields();
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers");
        }
    }
    
    private void addNewBudgetItem(String budgetName, double plannedAmount, double actualAmount) {
        Budget budget = new Budget(budgetName, plannedAmount, actualAmount);
        budgets.add(budget);
        HBox budgetItem = createBudgetItem(budgetName, plannedAmount, actualAmount);
        budgetListContainer.getChildren().add(budgetItem);
        BudgetDataManager.saveBudgets(budgets);
        // 新增：重新加载数据并更新差额
        budgets = BudgetDataManager.loadBudgets(); // 重新加载最新数据
        updateBudgetBalance(); // 立即更新差额显示
    }
    
    private void updateBudgetItem(int index, String budgetName, double plannedAmount, double actualAmount) {
        Budget budget = budgets.get(index);
        budget.setName(budgetName);
        budget.setPlannedAmount(plannedAmount);
        budget.setActualAmount(actualAmount);
        HBox budgetItem = (HBox) budgetListContainer.getChildren().get(index);
        updateBudgetItemContent(budgetItem, budgetName, plannedAmount, actualAmount);
        BudgetDataManager.saveBudgets(budgets);
         // 新增：重新加载数据并更新差额
        budgets = BudgetDataManager.loadBudgets(); // 重新加载最新数据
        updateBudgetBalance(); // 立即更新差额显示
        
    }
    
    private HBox createBudgetItem(String budgetName, double plannedAmount, double actualAmount) {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER);
        
        VBox progressBox = new VBox(5);
        progressBox.setPrefWidth(300);
        
        Label nameLabel = new Label(budgetName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label progressLabel = new Label(String.format("目标: %.2f / 当前: %.2f", plannedAmount, actualAmount));
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(280);
        
        double progress = actualAmount / plannedAmount;
        progressBar.setProgress(Math.min(progress, 1.0));
        
        Label percentageLabel = new Label(String.format("%.1f%%", progress * 100));
        
        progressBox.getChildren().addAll(nameLabel, progressLabel, progressBar, percentageLabel);
        
        Button editButton = new Button("编辑");
        editButton.setOnAction(e -> {
            int index = budgetListContainer.getChildren().indexOf(container);
            currentEditingIndex = index;
            budgetNameField.setText(budgetName);
            plannedAmountField.setText(String.valueOf(plannedAmount));
            actualAmountField.setText(String.valueOf(actualAmount));
            inputGrid.setVisible(true);
        });

        Button deleteButton = new Button("删除");
        deleteButton.setOnAction(e -> {
            int index = budgetListContainer.getChildren().indexOf(container);
            budgets.remove(index);
            budgetListContainer.getChildren().remove(index);
            BudgetDataManager.saveBudgets(budgets);
            // 新增：重新加载数据并更新差额
            budgets = BudgetDataManager.loadBudgets(); // 重新加载最新数据
            updateBudgetBalance(); // 立即更新差额显示
        });
        
        container.getChildren().addAll(progressBox, editButton, deleteButton);
        return container;
    }
    
    private void updateBudgetItemContent(HBox container, String budgetName, double plannedAmount, double actualAmount) {
        VBox progressBox = (VBox) container.getChildren().get(0);
        Label nameLabel = (Label) progressBox.getChildren().get(0);
        Label progressLabel = (Label) progressBox.getChildren().get(1);
        ProgressBar progressBar = (ProgressBar) progressBox.getChildren().get(2);
        Label percentageLabel = (Label) progressBox.getChildren().get(3);
        
        nameLabel.setText(budgetName);
        progressLabel.setText(String.format("目标: %.2f / 当前: %.2f", plannedAmount, actualAmount));
        
        double progress = actualAmount / plannedAmount;
        progressBar.setProgress(Math.min(progress, 1.0));
        percentageLabel.setText(String.format("%.1f%%", progress * 100));
    }
    
    private void resetInputFields() {
        budgetNameField.setText("");
        plannedAmountField.setText("");
        actualAmountField.setText("");
        currentEditingIndex = -1;
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * 计算并显示预算差额
     */
    private void updateBudgetBalance() {
        // 确保每次计算时都加载最新数据
        List<Budget> latestBudgets = BudgetDataManager.loadBudgets();
        double totalPlanned = 0.0;
        double totalActual = 0.0;
        
        for (Budget budget : latestBudgets) {
            totalPlanned += budget.getPlannedAmount();
            totalActual += budget.getActualAmount();
        }
        
        double balance = totalPlanned - totalActual;
        budgetBalanceLabel.setText(String.format("Budget Balance: %.2f yuan", balance));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        budgets = BudgetDataManager.loadBudgets();
        for (Budget budget : budgets) {
            HBox budgetItem = createBudgetItem(budget.getName(), budget.getPlannedAmount(), budget.getActualAmount());
            budgetListContainer.getChildren().add(budgetItem);
        }
        updateBudgetBalance(); // 初始化时更新预算差额
    }
}