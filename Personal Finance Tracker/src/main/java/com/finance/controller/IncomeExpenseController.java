package com.finance.controller;

import com.finance.util.CsvUtil;
import java.io.IOException;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import com.finance.result.ImportResult;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

/**
 * Income/Expense Interface Controller
 */
public class IncomeExpenseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(IncomeExpenseController.class);

  
    private final ObservableList<String> incomeTypes = 
        FXCollections.observableArrayList("Salary", "Bonus", "Others");
    private final ObservableList<String> expenseTypes = 
        FXCollections.observableArrayList("Food", "Shopping", "Transportation", "Housing", "Entertainment", "Others");
   

    private TransactionService transactionService;
    private ObservableList<Transaction> transactionList;

 
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
    
    // 日期范围筛选相关控件
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private Button filterButton;
    
    @FXML
    private Label periodIncomeLabel;
    
    @FXML
    private Label periodExpenseLabel;
    
    @FXML
    private Label periodBalanceLabel;
    
    @FXML
    private DatePicker singleDatePicker;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 为singleDatePicker添加监听逻辑，实现选择日期后自动筛选并刷新transactionTable，仅显示所选日期的交易记录。
        if (singleDatePicker != null) {
            // 设置日期单元格工厂，只允许选择有交易记录的日期
            singleDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    boolean hasTransactions = false;
                    for (Transaction t : transactionList) {
                        if (t.getDate() != null && t.getDate().toLocalDate().isEqual(date)) {
                            hasTransactions = true;
                            break;
                        }
                    }
                    setDisable(!hasTransactions);
                    if (!hasTransactions) {
                        setStyle("-fx-background-color: #fafafa;");
                    }
                }
            });

            // 添加日期选择监听器
            singleDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    ObservableList<Transaction> filtered = FXCollections.observableArrayList();
                    for (Transaction t : transactionList) {
                        if (t.getDate() != null && t.getDate().toLocalDate().isEqual(newValue)) {
                            filtered.add(t);
                        }
                    }
                    transactionTable.setItems(filtered);
                } else {
                    transactionTable.setItems(transactionList);
                }
            });
        }
        
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
                    setText(formatter.format(item.toLocalDate()));
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

        // 初始化日期范围选择器
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        
        // 设置默认的日期范围（过去一个月）
        startDatePicker.setValue(oneMonthAgo);
        endDatePicker.setValue(today);
        
        // 限制开始日期不能超过今天
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(LocalDate.now()));
            }
        });
        
        // 限制结束日期不能超过今天，且不能早于开始日期
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = startDatePicker.getValue();
                setDisable(date.isAfter(LocalDate.now()) || 
                          (startDate != null && date.isBefore(startDate)));
            }
        });
        
        // 当开始日期变化时，更新结束日期的可选范围
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // 更新结束日期选择器
                endDatePicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(date.isAfter(LocalDate.now()) || date.isBefore(newValue));
                    }
                });
                
                // 如果当前选择的结束日期早于新的开始日期，则更新结束日期
                if (endDatePicker.getValue() != null && endDatePicker.getValue().isBefore(newValue)) {
                    endDatePicker.setValue(newValue);
                }
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
        
        // 初始化时间段统计信息
        updatePeriodSummary(startDatePicker.getValue(), endDatePicker.getValue());
    }
    
    /**
     * Load transaction records
     */
    private void loadTransactions() {
        transactionList.clear();
        // 加载所有交易记录
        transactionList.addAll(transactionService.getAllTransactions());
        // 初始化时只显示当天的交易记录
        ObservableList<Transaction> todayTransactions = FXCollections.observableArrayList();
        LocalDate today = LocalDate.now();
        for (Transaction t : transactionList) {
            if (t.getDate() != null && t.getDate().toLocalDate().isEqual(today)) {
                todayTransactions.add(t);
            }
        }
        transactionTable.setItems(todayTransactions);
    }
    
    /**
     * Add new transaction record
     */
    @FXML
    private void handleAddTransaction() {
        try {
            
             // 获取用户选择的日期
             if (datePicker.getValue() == null) {
                showAlert("Please select a date range.");
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
    showAlert("Please select a type of Income or Expense.");
    return;
}
if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
    showAlert("The amount cannot be empty.");
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
            showAlert("The amount format is incorrect,Please enter valid digits(like:199.99)");
        } catch (Exception e) {
            logger.error("Fail to add the transcation.", e);
            showAlert("Fail to add the transcation: " + e.getMessage());
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
     * 更新指定时间段的统计信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    private void updatePeriodSummary(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return;
        }
        
        try {
            double periodIncome = transactionService.calculateTotalByCategoryAndDateRange("Income", startDate, endDate);
            double periodExpense = transactionService.calculateTotalByCategoryAndDateRange("Expense", startDate, endDate);
            double periodBalance = periodIncome - periodExpense;
            
            periodIncomeLabel.setText(String.format("¥%.2f", periodIncome));
            periodExpenseLabel.setText(String.format("¥%.2f", periodExpense));
            periodBalanceLabel.setText(String.format("¥%.2f", periodBalance));
            
            // 更新表格显示，只显示该时间段内的交易记录
            transactionList.clear();
            transactionList.addAll(transactionService.getTransactionsByDateRange(startDate, endDate));
            transactionTable.setItems(transactionList);
        } catch (Exception e) {
            logger.error("Failed to update the time period statistics information.", e);
            showAlert("Failed to update the time period statistics information.: " + e.getMessage());
        }
    }
    
    /**
     * 处理日期范围筛选
     */
    @FXML
    private void handleFilterByDateRange() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) {
            showAlert("Please select the start date and end date.");
            return;
        }
        
        if (endDate.isBefore(startDate)) {
            showAlert("The end date cannot be earlier than the start date");
            return;
        }
        
        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            showAlert("The screening time cannot exceed the current date");
            return;
        }
        
        updatePeriodSummary(startDate, endDate);
    }

    
    /**
     * Show alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // 应用CSS样式
        DialogPane dialogPane = alert.getDialogPane();
        if (getClass().getResource("/css/styles.css") != null) {
            dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("error-alert");
        }
        
        // 设置英文按钮
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);
        
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
        // 创建编辑对话框
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaction");
        dialog.setHeaderText("Edit transaction details");
        
        // 应用CSS样式
        DialogPane dialogPane = dialog.getDialogPane();
        if (getClass().getResource("/css/styles.css") != null) {
            dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("edit-dialog");
        }
        
        // 设置按钮
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(saveButton, cancelButton);
        
        // 创建表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        
        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList("Income", "Expense"));
        ComboBox<String> typeBox = new ComboBox<>();
        TextField amountBox = new TextField();
        TextArea descriptionBox = new TextArea();
        
        // 设置初始值
        categoryBox.setValue(transaction.getCategory());
        typeBox.setValue(transaction.getType());
        amountBox.setText(String.valueOf(transaction.getAmount()));
        descriptionBox.setText(transaction.getDescription());
        
        // 添加类型联动
        categoryBox.setOnAction(e -> {
            if ("Income".equals(categoryBox.getValue())) {
                typeBox.setItems(incomeTypes);
            } else if ("Expense".equals(categoryBox.getValue())) {
                typeBox.setItems(expenseTypes);
            }
            typeBox.getSelectionModel().selectFirst();
        });
        
        // 触发一次以设置正确的类型列表
        categoryBox.fireEvent(new javafx.event.ActionEvent());
        
        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryBox, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeBox, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountBox, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionBox, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // 处理结果
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButton) {
            try {
                // 获取更新的值
                String category = categoryBox.getValue();
                String type = typeBox.getValue();
                double amount = Double.parseDouble(amountBox.getText());
                String description = descriptionBox.getText();
                
                // 更新交易对象
                transaction.setCategory(category);
                transaction.setType(type);
                transaction.setAmount(amount);
                transaction.setDescription(description);
                
                // 更新数据库
                transactionService.updateTransaction(transaction);
                
                // 刷新表格
                loadTransactions();
                updateSummary();
                
            } catch (NumberFormatException e) {
                showAlert("The amount format is incorrect. Please enter significant figures (example: 199.99).");
            } catch (Exception e) {
                showAlert("Failed to update transaction: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handle delete transaction
     */
    private void handleDeleteTransaction(Transaction transaction) {
        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete Transaction");
            confirmAlert.setContentText("Are you sure you want to delete this transaction?");
            
            // 应用CSS样式
            DialogPane dialogPane = confirmAlert.getDialogPane();
            URL cssUrl = getClass().getResource("/css/styles.css");
            if (cssUrl != null) {
                dialogPane.getStylesheets().add(cssUrl.toExternalForm());
                dialogPane.getStyleClass().add("confirmation-dialog");
            }
            
            // 设置英文按钮
            ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmAlert.getButtonTypes().setAll(deleteButton, cancelButton);
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == deleteButton) {
                if (transaction != null) {
                    transactionService.deleteTransaction(transaction.getId());
                    loadTransactions();
                    updateSummary();
                    // 更新时间段统计信息
                    if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                        updatePeriodSummary(startDatePicker.getValue(), endDatePicker.getValue());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error deleting transaction", e);
            showAlert("Failed to delete transaction: " + e.getMessage());
        }
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
    private void handleExportCSV(javafx.event.ActionEvent event) {
        // 原文件选择器逻辑保持不变
                FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());
    
        if (file != null) {
            try {
                CsvUtil.exportTableToCSV(transactionTable, file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Success");
                alert.setHeaderText(null);
                alert.setContentText("Data exported successfully!");
                
                // Apply CSS styles
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                dialogPane.getStyleClass().add("dialog-pane");
                
                // Set English button text
                ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(okButton);
                
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Error");
                alert.setHeaderText("Export Failed");
                alert.setContentText("Error exporting data: " + e.getMessage());
                
                // Apply CSS styles
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                dialogPane.getStyleClass().add("error-alert");
                
                // Set English button text
                ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(okButton);
                
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleImportCSV() {
        // 添加CSV格式提示弹窗
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("CSV Format Requirements");
        confirmAlert.setHeaderText("Please ensure the CSV file contains the following columns in the correct order");
        
        // Create example text area
        TextArea exampleText = new TextArea(
            "Category,Type,Amount,Description,Date\n" +
            "Income,Salary,5000.00,Monthly salary,2023-08-01\n" +
            "Expense,Food,35.50,Lunch at restaurant,2023-08-02"
        );
        exampleText.setEditable(false);
        exampleText.setWrapText(true);
        exampleText.setMaxWidth(Double.MAX_VALUE);
        exampleText.setPrefRowCount(5);
        
        // Apply CSS styles
        DialogPane confirmDialogPane = confirmAlert.getDialogPane();
        confirmDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        confirmDialogPane.getStyleClass().add("dialog-pane");
        confirmDialogPane.setContent(exampleText);
        
        // Set English button text
        ButtonType confirmOkButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType confirmCancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmAlert.getButtonTypes().setAll(confirmOkButton, confirmCancelButton);
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == confirmOkButton) {
                // 原文件选择器逻辑保持不变
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select CSV File");
                
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
                File file = fileChooser.showOpenDialog(transactionTable.getScene().getWindow());

                if (file != null) {
                    try {
                        // CSV解析逻辑
                        List<Transaction> importedTransactions = CsvUtil.parseCSV(file);

                        // 创建预览对话框
                        Dialog<ButtonType> dialog = new Dialog<>();
                        dialog.setTitle("Import Preview");
                        dialog.setHeaderText(String.format("Found %d records ready for import", importedTransactions.size()));
                        
                        // Add preview table
                        TableView<Transaction> previewTable = createPreviewTable(importedTransactions);
                        dialog.getDialogPane().setContent(previewTable);
                        
                        // Apply CSS styles
                        DialogPane previewDialogPane = dialog.getDialogPane();

                        previewDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        previewDialogPane.getStyleClass().add("dialog-pane");
                        
                        // Set English button text
                        ButtonType previewOkButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        ButtonType previewCancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                        dialog.getDialogPane().getButtonTypes().addAll(previewOkButton, previewCancelButton);
                        
                        if (dialog.showAndWait().orElse(ButtonType.CANCEL).getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            ImportResult result = transactionService.batchImport(importedTransactions);
                            loadTransactions();
                            updateSummary();
                            // 更新时间段统计信息
                            updatePeriodSummary(startDatePicker.getValue(), endDatePicker.getValue());
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Import Success");
                            successAlert.setHeaderText(String.format("Import finished.\nSuccessfully imported: %d\nSkipped duplicates: %d", result.getImportedCount(), result.getSkippedCount()));
                            
                            // Apply CSS styles
                            DialogPane successDialogPane = successAlert.getDialogPane();
                            successDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                            successDialogPane.getStyleClass().add("success-alert");

                            
                            // Set English button text
                            ButtonType successOkButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                            successAlert.getButtonTypes().setAll(successOkButton);
                            
                            successAlert.showAndWait();
                        }
                    } catch (Exception e) {
                        showAlert("CSV import failed: " + e.getMessage());
                    }
                }
            } else {
                // 用户取消或关闭对话框时直接返回
                return;
            }
        });
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
