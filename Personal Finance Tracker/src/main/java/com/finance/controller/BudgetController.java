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
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import com.finance.util.BudgetDataManager;

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
    private TableColumn<BudgetPlan, Double> doneAmountColumn;

    @FXML
    private Label totalBudgetLabel;

    @FXML
    private Label remainingBudgetLabel;

    @FXML
    private Label usedBudgetLabel;

    @FXML
    private ProgressBar budgetProgressBar;

    @FXML
    private TextField doneAmountField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        loadBudgetData();
    }

    private void loadBudgetData() {
        List<BudgetPlan> plans = BudgetDataManager.loadBudgetPlans();
        budgetTable.setItems(FXCollections.observableArrayList(plans));
        updateBudgetSummary();
    }

    private void saveBudgetData() {
        List<BudgetPlan> plans = new ArrayList<>(budgetTable.getItems());
        BudgetDataManager.saveBudgetPlans(plans);
    }

    @FXML
    private TableColumn<BudgetPlan, Void> deleteColumn;

    @FXML
    private TableColumn<BudgetPlan, Void> editColumn;

    private void initializeTable() {
        planNameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));
        budgetAmountColumn.setCellValueFactory(new PropertyValueFactory<>("budgetAmount"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        doneAmountColumn.setCellValueFactory(new PropertyValueFactory<>("doneAmount"));

        // 设置类别列为ComboBox
        ObservableList<String> categories = FXCollections.observableArrayList("Food", "Transportation", "Housing", "Entertainment", "Others");
        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categories));
        categoryColumn.setOnEditCommit(event -> {
            BudgetPlan plan = event.getRowValue();
            plan.setCategory(event.getNewValue());
        });

        // 设置编辑按钮列
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            
            {
                editButton.setOnAction(event -> {
                    BudgetPlan plan = getTableView().getItems().get(getIndex());
                    handleEditPlan(plan);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        // 设置删除按钮列
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            
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
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Are you sure you want to delete this budget plan?");
        confirmDialog.setContentText("Plan Name: " + plan.getPlanName());

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            budgetTable.getItems().remove(plan);
            updateBudgetSummary();
            saveBudgetData();
        }
    }

    @FXML
    private void handleAddPlan() {
        Dialog<BudgetPlan> dialog = new Dialog<>();
        dialog.setTitle("Add Budget Plan");
        dialog.setHeaderText("Please enter budget plan details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField planNameField = new TextField();
        TextField budgetAmountField = new TextField();
        TextField doneAmountField = new TextField();
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        
        // Set default locale for DatePickers
        Locale.setDefault(Locale.ENGLISH);
        
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Food", "Transportation", "Housing", "Entertainment", "Others");

        grid.add(new Label("Plan Name:"), 0, 0);
        grid.add(planNameField, 1, 0);
        grid.add(new Label("Budget Amount:"), 0, 1);
        grid.add(budgetAmountField, 1, 1);
        grid.add(new Label("Done Amount:"), 0, 2);
        grid.add(doneAmountField, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDatePicker, 1, 4);
        grid.add(new Label("Category:"), 0, 5);
        grid.add(categoryComboBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String planName = planNameField.getText();
                    double amount = Double.parseDouble(budgetAmountField.getText());
                    double doneAmount = Double.parseDouble(doneAmountField.getText());
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();
                    String category = categoryComboBox.getValue();

                    if (planName.isEmpty() || startDate == null || endDate == null || category == null) {
                        showAlert("Please fill in all required fields");
                        return null;
                    }

                    BudgetPlan plan = new BudgetPlan(planName, amount, startDate, endDate, category);
                    plan.setDoneAmount(doneAmount);
                    return plan;
                } catch (NumberFormatException e) {
                    showAlert("Please enter a valid budget amount");
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
                saveBudgetData();
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

    private void handleEditPlan(BudgetPlan plan) {
        Dialog<BudgetPlan> dialog = new Dialog<>();
        dialog.setTitle("Edit Budget Plan");
        dialog.setHeaderText("Please modify budget plan details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField planNameField = new TextField(plan.getPlanName());
        TextField budgetAmountField = new TextField(String.valueOf(plan.getBudgetAmount()));
        TextField doneAmountField = new TextField(String.valueOf(plan.getDoneAmount()));
        DatePicker startDatePicker = new DatePicker(plan.getStartDate());
        DatePicker endDatePicker = new DatePicker(plan.getEndDate());
        
        // Set default locale for DatePickers
        Locale.setDefault(Locale.ENGLISH);
        
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Food", "Transportation", "Housing", "Entertainment", "Others");
        categoryComboBox.setValue(plan.getCategory());

        grid.add(new Label("计划名称:"), 0, 0);
        grid.add(planNameField, 1, 0);
        grid.add(new Label("预算金额:"), 0, 1);
        grid.add(budgetAmountField, 1, 1);
        grid.add(new Label("已完成金额:"), 0, 2);
        grid.add(doneAmountField, 1, 2);
        grid.add(new Label("开始日期:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("结束日期:"), 0, 4);
        grid.add(endDatePicker, 1, 4);
        grid.add(new Label("类别:"), 0, 5);
        grid.add(categoryComboBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String planName = planNameField.getText();
                    double amount = Double.parseDouble(budgetAmountField.getText());
                    double doneAmount = Double.parseDouble(doneAmountField.getText());
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();
                    String category = categoryComboBox.getValue();

                    if (planName.isEmpty() || startDate == null || endDate == null || category == null) {
                        showAlert("请填写所有必填字段");
                        return null;
                    }

                    BudgetPlan newPlan = new BudgetPlan(planName, amount, startDate, endDate, category);
                    newPlan.setDoneAmount(doneAmount);
                    return newPlan;
                } catch (NumberFormatException e) {
                    showAlert("请输入有效的预算金额");
                    return null;
                }
            }
            return null;
        });

        // 显示对话框并处理结果
        dialog.showAndWait().ifPresent(updatedPlan -> {
            if (updatedPlan != null) {
                int index = budgetTable.getItems().indexOf(plan);
                budgetTable.getItems().set(index, updatedPlan);
                updateBudgetSummary();
                saveBudgetData();
            }
        });
    }

    private void updateBudgetSummary() {
        double totalBudget = 0;
        double totalDone = 0;

        for (BudgetPlan plan : budgetTable.getItems()) {
            totalBudget += plan.getBudgetAmount();
            totalDone += plan.getDoneAmount();
        }

        double remainingBudget = totalBudget - totalDone;
        double progress = totalBudget > 0 ? totalDone / totalBudget : 0;

        // 更新进度条和标签
        budgetProgressBar.setProgress(progress);
        totalBudgetLabel.setText(String.format("¥%.2f / ¥%.2f", totalDone, totalBudget));
        usedBudgetLabel.setText(String.format("Used: ¥%.2f", totalDone));
        remainingBudgetLabel.setText(String.format("Remaining: ¥%.2f", remainingBudget));
    }
}