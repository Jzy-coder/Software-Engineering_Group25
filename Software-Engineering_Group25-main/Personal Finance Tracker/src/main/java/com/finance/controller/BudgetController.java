package com.finance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import com.finance.model.Budget;
import com.finance.util.BudgetDataManager;

public class BudgetController implements Initializable {
    @FXML
    private Button addButton;
    
    @FXML
    private GridPane inputGrid;
    
    @FXML
    private TextField plannedAmountField;
    
    @FXML
    private TextField actualAmountField;
    
    @FXML
    private VBox budgetListContainer;
    
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
    private void handleConfirm() {
        try {
            double plannedAmount = Double.parseDouble(plannedAmountField.getText());
            double actualAmount = Double.parseDouble(actualAmountField.getText());
            
            if (plannedAmount <= 0) {
                showAlert("计划金额必须大于0");
                return;
            }
            
            if (actualAmount < 0) {
                showAlert("当前金额不能为负数");
                return;
            }
            
            if (currentEditingIndex >= 0) {
                updateBudgetItem(currentEditingIndex, plannedAmount, actualAmount);
            } else {
                addNewBudgetItem(plannedAmount, actualAmount);
            }
            
            inputGrid.setVisible(false);
            resetInputFields();
        } catch (NumberFormatException e) {
            showAlert("请输入有效的数字");
        }
    }
    
    private void addNewBudgetItem(double plannedAmount, double actualAmount) {
        Budget budget = new Budget(plannedAmount, actualAmount);
        budgets.add(budget);
        HBox budgetItem = createBudgetItem(plannedAmount, actualAmount);
        budgetListContainer.getChildren().add(budgetItem);
        BudgetDataManager.saveBudgets(budgets);
    }
    
    private void updateBudgetItem(int index, double plannedAmount, double actualAmount) {
        Budget budget = budgets.get(index);
        budget.setPlannedAmount(plannedAmount);
        budget.setActualAmount(actualAmount);
        HBox budgetItem = (HBox) budgetListContainer.getChildren().get(index);
        updateBudgetItemContent(budgetItem, plannedAmount, actualAmount);
        BudgetDataManager.saveBudgets(budgets);
    }
    
    private HBox createBudgetItem(double plannedAmount, double actualAmount) {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER);
        
        VBox progressBox = new VBox(5);
        progressBox.setPrefWidth(300);
        
        Label progressLabel = new Label(String.format("目标: %.2f / 当前: %.2f", plannedAmount, actualAmount));
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(280);
        
        double progress = actualAmount / plannedAmount;
        progressBar.setProgress(Math.min(progress, 1.0));
        
        Label percentageLabel = new Label(String.format("%.1f%%", progress * 100));
        
        progressBox.getChildren().addAll(progressLabel, progressBar, percentageLabel);
        
        Button editButton = new Button("编辑");
        editButton.setOnAction(e -> {
            int index = budgetListContainer.getChildren().indexOf(container);
            currentEditingIndex = index;
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
        });
        
        container.getChildren().addAll(progressBox, editButton, deleteButton);
        return container;
    }
    
    private void updateBudgetItemContent(HBox container, double plannedAmount, double actualAmount) {
        VBox progressBox = (VBox) container.getChildren().get(0);
        Label progressLabel = (Label) progressBox.getChildren().get(0);
        ProgressBar progressBar = (ProgressBar) progressBox.getChildren().get(1);
        Label percentageLabel = (Label) progressBox.getChildren().get(2);
        
        progressLabel.setText(String.format("目标: %.2f / 当前: %.2f", plannedAmount, actualAmount));
        
        double progress = actualAmount / plannedAmount;
        progressBar.setProgress(Math.min(progress, 1.0));
        percentageLabel.setText(String.format("%.1f%%", progress * 100));
    }
    
    private void resetInputFields() {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        budgets = BudgetDataManager.loadBudgets();
        for (Budget budget : budgets) {
            HBox budgetItem = createBudgetItem(budget.getPlannedAmount(), budget.getActualAmount());
            budgetListContainer.getChildren().add(budgetItem);
        }
    }
}