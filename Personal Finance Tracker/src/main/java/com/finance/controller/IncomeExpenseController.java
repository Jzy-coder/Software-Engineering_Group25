package com.finance.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue; 

/**
 * Income/Expense Interface Controller
 */
public class IncomeExpenseController implements Initializable {

    // ▼▼▼▼▼▼▼▼▼ 新增代码：定义分类选项 ▼▼▼▼▼▼▼▼▼
    private final ObservableList<String> incomeTypes = 
        FXCollections.observableArrayList("Salary", "Bonus", "Others");
    private final ObservableList<String> expenseTypes = 
        FXCollections.observableArrayList("Food", "Shopping", "Transportation", "Housing", "Entertainment", "Others");
    // ▲▲▲▲▲▲▲▲▲ 新增结束 ▲▲▲▲▲▲▲▲▲

    private TransactionService transactionService;
    private ObservableList<Transaction> transactionList;

    // ▼▼▼▼▼▼▼▼▼▼ 新增代码 ▼▼▼▼▼▼▼▼▼▼
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private TableView<Transaction> transactionTable;
    
    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    
    @FXML
    private TableColumn<Transaction, String> typeColumn;
    
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    
    @FXML
    private TableColumn<Transaction, LocalDateTime> dateColumn;
    
    @FXML
    private TableColumn<Transaction, Void> editColumn;
    
    @FXML
    private TableColumn<Transaction, Void> deleteColumn;
    
    @FXML
    private ComboBox<String> categoryComboBox;
    
    @FXML
    private ComboBox<String> typeComboBox;
    
    @FXML
    private TextField amountField;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Button addButton;
    
    @FXML
    private Label totalIncomeLabel;
    
    @FXML
    private Label totalExpenseLabel;
    
    @FXML
    private Label balanceLabel;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private Label periodIncomeLabel;
    
    @FXML
    private Label periodExpenseLabel;
    
    @FXML
    private Label periodBalanceLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // 使用LoginManager中的TransactionService实例，确保用户数据隔离
        transactionService = com.finance.gui.LoginManager.getTransactionService();
        transactionList = FXCollections.observableArrayList();
        
        // Set custom placeholder for empty table
        Label placeholderLabel = new Label("There is no record of income and expense.");
        placeholderLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        transactionTable.setPlaceholder(placeholderLabel);
        
        // Initialize table columns
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // Format date column
        dateColumn.setCellFactory(column -> new TableCell<Transaction, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        
        // Setup edit and delete buttons
        setupEditColumn();
        setupDeleteColumn();
        
      // 设置默认日期为今天
      datePicker.setValue(LocalDate.now());

      // 限制可选日期范围为过去一个月内
      datePicker.setDayCellFactory(picker -> new DateCell() {
          @Override
          public void updateItem(LocalDate date, boolean empty) {
              super.updateItem(date, empty);
              LocalDate minDate = LocalDate.now().minusMonths(1);
              setDisable(date.isBefore(minDate) || date.isAfter(LocalDate.now()));
          }
      });

       // 添加下拉框联动 
       categoryComboBox.setItems(FXCollections.observableArrayList("Income", "Expense"));
      
       categoryComboBox.getSelectionModel().selectedItemProperty().addListener(
           (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
               if ("Income".equals(newValue)) {
                   typeComboBox.setItems(incomeTypes);
               } else if ("Expense".equals(newValue)) {
                   typeComboBox.setItems(expenseTypes);
               }
               typeComboBox.getSelectionModel().selectFirst();
           }
       );
       categoryComboBox.getSelectionModel().selectFirst();
       
       // 初始化时间段选择器
       startDatePicker.setValue(LocalDate.now().minusMonths(1));
       endDatePicker.setValue(LocalDate.now());
       
       // 添加时间段选择监听器
       startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updatePeriodSummary());
       endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updatePeriodSummary());
       
       // Load data
       loadTransactions();
       updateSummary();
       updatePeriodSummary();
    }
    
    /**
     * Load transaction records
     */
    private void loadTransactions() {
        transactionList.clear();
        transactionList.addAll(transactionService.getAllTransactions());
        transactionTable.setItems(transactionList);
    }
    
    /**
     * Add new transaction record
     */
    @FXML
    private void handleAddTransaction() {
        try {
            
             // 获取用户选择的日期
             LocalDate selectedDate = datePicker.getValue();
             LocalDateTime transactionDate = selectedDate.atStartOfDay(); // 转换为当天零点时间

            String category = categoryComboBox.getValue();
            String type = typeComboBox.getValue();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionArea.getText();
            
            if (category == null || type == null) {
                showAlert("Category and type must be selected");
                return;
            }
            
            Transaction transaction = new Transaction(category, type, amount, description, transactionDate);
            transactionService.addTransaction(transaction);
            
            // Clear input fields
            categoryComboBox.setValue(null);
            typeComboBox.setValue(null);
            amountField.clear();
            descriptionArea.clear();
            
            // Refresh table
            loadTransactions();
            updateSummary();
            
        } catch (NumberFormatException e) {
            showAlert("Amount must be a valid number");
        } catch (Exception e) {
            showAlert("Failed to add transaction: " + e.getMessage());
        }
    }
    
    /**
     * Update summary information
     */
    private void updateSummary() {
        double income = transactionService.calculateTotalByCategory("Income");
        double expense = transactionService.calculateTotalByCategory("Expense");
        double balance = income - expense;
        
        totalIncomeLabel.setText(String.format("¥%.2f", income));
        totalExpenseLabel.setText(String.format("¥%.2f", expense));
        balanceLabel.setText(String.format("¥%.2f", balance));
    }
    
    /**
     * Update period summary information
     */
    private void updatePeriodSummary() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            LocalDateTime startDate = startDatePicker.getValue().atStartOfDay();
            LocalDateTime endDate = endDatePicker.getValue().atTime(23, 59, 59);
            
            if (!startDate.isAfter(endDate)) {
                double periodIncome = transactionService.calculateTotalByCategoryAndDateRange("Income", startDate, endDate);
                double periodExpense = transactionService.calculateTotalByCategoryAndDateRange("Expense", startDate, endDate);
                double periodBalance = periodIncome - periodExpense;
                
                periodIncomeLabel.setText(String.format("¥%.2f", periodIncome));
                periodExpenseLabel.setText(String.format("¥%.2f", periodExpense));
                periodBalanceLabel.setText(String.format("¥%.2f", periodBalance));
            }
        }
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void setupEditColumn() {
        editColumn.setCellFactory(column -> new TableCell<Transaction, Void>() {
            private final Button editButton = new Button("Edit");
            
            {
                editButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    handleEditTransaction(transaction);
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
    }
    
    private void setupDeleteColumn() {
        deleteColumn.setCellFactory(column -> new TableCell<Transaction, Void>() {
            private final Button deleteButton = new Button("Delete");
            
            {
                deleteButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    handleDeleteTransaction(transaction);
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
    
    /**
     * Handle edit transaction
     */
    private void handleEditTransaction(Transaction transaction) {
        // Populate form fields with transaction data
        categoryComboBox.setValue(transaction.getCategory());
        typeComboBox.setValue(transaction.getType());
        amountField.setText(String.valueOf(transaction.getAmount()));
        descriptionArea.setText(transaction.getDescription());
        
        // Add visual feedback for edit mode
        categoryComboBox.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px");
        typeComboBox.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px");
        amountField.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px");
        descriptionArea.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px");
        
        // Ensure form fields are visible
        categoryComboBox.requestFocus();
        
        // Change add button to update button
        addButton.setText("Update Transaction");
        
        // Set onAction to update instead of add
        addButton.setOnAction(event -> {
            try {
                // Get updated values
                String category = categoryComboBox.getValue();
                String type = typeComboBox.getValue();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionArea.getText();
                
                // Update transaction object
                transaction.setCategory(category);
                transaction.setType(type);
                transaction.setAmount(amount);
                transaction.setDescription(description);
                
                // Update in database
                transactionService.updateTransaction(transaction);
                
                // Reset form
                resetForm();
                
                // Refresh table
                loadTransactions();
                updateSummary();
                
            } catch (NumberFormatException e) {
                showAlert("Amount must be a valid number");
            } catch (Exception e) {
                showAlert("Failed to update transaction: " + e.getMessage());
            }
        });
    }
    
    /**
     * Handle delete transaction
     */
    private void handleDeleteTransaction(Transaction transaction) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this transaction?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete from database
                    transactionService.deleteTransaction(transaction.getId());
                    
                    // Refresh table
                    loadTransactions();
                    updateSummary();
                    
                } catch (Exception e) {
                    showAlert("Failed to delete transaction: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Reset form to add mode
     */
    private void resetForm() {
        categoryComboBox.setValue(null);
        typeComboBox.setValue(null);
        amountField.clear();
        descriptionArea.clear();
        
        // Reset form styles
        categoryComboBox.setStyle("");
        typeComboBox.setStyle("");
        amountField.setStyle("");
        descriptionArea.setStyle("");
        
        // Reset button text and action
        addButton.setText("Add New Transaction");
        addButton.setOnAction((event) -> handleAddTransaction());
    }
}