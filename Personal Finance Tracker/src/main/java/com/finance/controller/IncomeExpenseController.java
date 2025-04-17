package com.finance.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import com.finance.util.CSVParser;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Callback;

/**
 * Income/Expense Interface Controller
 */
public class IncomeExpenseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(IncomeExpenseController.class);

    // ▼▼▼▼▼▼▼▼▼ 新增代码：定义分类选项 ▼▼▼▼▼▼▼▼▼
    private final ObservableList<String> incomeTypes = 
        FXCollections.observableArrayList("Salary", "Bonus", "Others");
    private final ObservableList<String> expenseTypes = 
        FXCollections.observableArrayList("Food", "Shopping", "Transportation", "Housing", "Entertainment", "Others");
    // ▲▲▲▲▲▲▲▲▲▲ 新增结束 ▲▲▲▲▲▲▲▲▲▲

    private TransactionService transactionService;
    private ObservableList<Transaction> transactionList;

    // ▼▼▼▼▼▼▼▼▼▼ 新增代码 ▼▼▼▼▼▼▼▼▼▼
    @FXML
    private DatePicker datePicker;
    // ▲▲▲▲▲▲▲▲▲▲ 新增结束 ▲▲▲▲▲▲▲▲▲▲
    
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
      
        
        // Load data
        loadTransactions();
        updateSummary();
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
             if (datePicker.getValue() == null) {
                showAlert("请选择交易日期");
                return;
            }
            LocalDate selectedDate = datePicker.getValue();
            LocalDateTime transactionDate = selectedDate.atStartOfDay(); // 转换为当天零点时间

            String category = categoryComboBox.getValue();
            String type = typeComboBox.getValue();
            double amount = Math.abs(Double.parseDouble(amountField.getText()));
            String description = descriptionArea.getText();
            
            // 非空校验
if (category == null || type == null) {
    showAlert("请选择收支类别和类型");
    return;
}
if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
    showAlert("金额不能为空");
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
            showAlert("金额格式错误，请输入有效数字（示例：199.99）");
        } catch (Exception e) {
            logger.error("添加交易记录失败", e);
            showAlert("添加交易失败: " + e.getMessage());
        }
    }
    
     /**
     * 更新总结信息，包括今天的总支出。
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
     * Show alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Setup edit button column
     */
    private void setupEditColumn() {
        Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>> cellFactory = column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            {
                editButton.setOnAction((event) -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    handleEditTransaction(transaction);
                });
            }
            
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        };
        
        editColumn.setCellFactory(cellFactory);
    }
    
    /**
     * Setup delete button column
     */
    private void setupDeleteColumn() {
        Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>> cellFactory = column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setOnAction((event) -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    handleDeleteTransaction(transaction);
                });
            }
            
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };
        
        deleteColumn.setCellFactory(cellFactory);
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
                showAlert("金额格式错误，请输入有效数字（示例：199.99）");
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
    
    @FXML
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择CSV文件");
        
        // 设置微信默认路径
        Path wechatPath = Paths.get(System.getProperty("user.home"), "Documents", "WeChat Files");
        if (wechatPath.toFile().exists()) {
            fileChooser.setInitialDirectory(wechatPath.toFile());
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV文件", "*.csv"));
        File file = fileChooser.showOpenDialog(transactionTable.getScene().getWindow());

        if (file != null) {
            try {
                // CSV解析逻辑
                List<Transaction> importedTransactions = new CSVParser(transactionService).parseWeChatCSV(file, transactionService.getAllTransactions());
                
                // 创建预览对话框
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("导入预览");
                dialog.setHeaderText(String.format("共发现%d条待导入记录", importedTransactions.size()));
                
                // 添加预览表格
                TableView<Transaction> previewTable = createPreviewTable(importedTransactions);
                dialog.getDialogPane().setContent(previewTable);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                
                if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    transactionService.batchImport(importedTransactions);
                    loadTransactions();
                    updateSummary();
                    showAlert("成功导入" + importedTransactions.size() + "条记录");

                }
            } catch (Exception e) {
                showAlert("CSV文件解析失败: " + e.getMessage());
            }
        }
    }

    private TableView<Transaction> createPreviewTable(List<Transaction> transactions) {
        TableView<Transaction> previewTable = new TableView<>();
        
        // 定义表格列
        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(cellData.getValue().getDate().format(formatter));
        });
        
        // 添加列到表格
        previewTable.getColumns().addAll(categoryCol, typeCol, amountCol, dateCol);
        
        // 设置数据源
        previewTable.setItems(FXCollections.observableArrayList(transactions));
        
        // 设置表格样式
        previewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        previewTable.setPrefHeight(400);
        
        return previewTable;
    }

   
}
