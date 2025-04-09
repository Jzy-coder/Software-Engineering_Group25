package com.finance.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.Optional;

public class BudgetController implements Initializable {

    @FXML
    private Button addPlanButton;

    @FXML
    private TableView<BudgetPlan> budgetTable;

    @FXML
    private TableColumn<BudgetPlan, String> planNameColumn;

    @FXML
    private TableColumn<BudgetPlan, Double> budgetAmountColumn;

    @FXML
    private TableColumn<BudgetPlan, LocalDate> startDateColumn;

    @FXML
    private TableColumn<BudgetPlan, LocalDate> endDateColumn;

    @FXML
    private TableColumn<BudgetPlan, String> categoryColumn;

    @FXML
    private Label totalBudgetLabel;

    @FXML
    private Label remainingBudgetLabel;

    @FXML
    private Label usedBudgetLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
    }

    @FXML
    private TableColumn<BudgetPlan, Void> deleteColumn;

    private void initializeTable() {
        planNameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));
        budgetAmountColumn.setCellValueFactory(new PropertyValueFactory<>("budgetAmount"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // 设置类别列为ComboBox
        ObservableList<String> categories = FXCollections.observableArrayList("食品", "交通", "住房", "娱乐", "其他");
        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categories));
        categoryColumn.setOnEditCommit(event -> {
            BudgetPlan plan = event.getRowValue();
            plan.setCategory(event.getNewValue());
        });

        // 设置删除按钮列
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("删除");
            
            {
                deleteButton.setOnAction(event -> {
                    BudgetPlan plan = getTableView().getItems().get(getIndex());
                    handleDeletePlan(plan);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void handleDeletePlan(BudgetPlan plan) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认删除");
        confirmDialog.setHeaderText("确定要删除这个预算计划吗？");
        confirmDialog.setContentText("计划名称: " + plan.getPlanName());

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            budgetTable.getItems().remove(plan);
            updateBudgetSummary();
        }
    }

    @FXML
    private void handleAddPlan() {
        // 创建输入表单对话框
        Dialog<BudgetPlan> dialog = new Dialog<>();
        dialog.setTitle("添加预算计划");
        dialog.setHeaderText("请输入预算计划详情");

        // 设置对话框按钮
        ButtonType saveButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 创建表单网格
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 添加表单字段
        TextField planNameField = new TextField();
        TextField budgetAmountField = new TextField();
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("食品", "交通", "住房", "娱乐", "其他");

        grid.add(new Label("计划名称:"), 0, 0);
        grid.add(planNameField, 1, 0);
        grid.add(new Label("预算金额:"), 0, 1);
        grid.add(budgetAmountField, 1, 1);
        grid.add(new Label("开始日期:"), 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(new Label("结束日期:"), 0, 3);
        grid.add(endDatePicker, 1, 3);
        grid.add(new Label("类别:"), 0, 4);
        grid.add(categoryComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String planName = planNameField.getText();
                    double amount = Double.parseDouble(budgetAmountField.getText());
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();
                    String category = categoryComboBox.getValue();

                    if (planName.isEmpty() || startDate == null || endDate == null || category == null) {
                        showAlert("请填写所有必填字段");
                        return null;
                    }

                    return new BudgetPlan(planName, amount, startDate, endDate, category);
                } catch (NumberFormatException e) {
                    showAlert("请输入有效的预算金额");
                    return null;
                }
            }
            return null;
        });

        // 显示对话框并处理结果
        dialog.showAndWait().ifPresent(budgetPlan -> {
            if (budgetPlan != null) {
                budgetTable.getItems().add(budgetPlan);
                updateBudgetSummary();
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateBudgetSummary() {
        double totalBudget = 0;

        for (BudgetPlan plan : budgetTable.getItems()) {
            totalBudget += plan.getBudgetAmount();
        }

        totalBudgetLabel.setText(String.format("¥%.2f", totalBudget));
        usedBudgetLabel.setText(String.format("¥%.2f", 0.0));
        remainingBudgetLabel.setText(String.format("¥%.2f", totalBudget));
    }
}