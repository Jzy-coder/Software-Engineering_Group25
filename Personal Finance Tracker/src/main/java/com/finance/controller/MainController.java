package com.finance.controller;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * 主界面控制器
 */
public class MainController implements Initializable {

    private TransactionService transactionService;
    private ObservableList<Transaction> transactionList;
    
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
    private ComboBox<String> categoryComboBox;
    
    @FXML
    private ComboBox<String> typeComboBox;
    
    @FXML
    private TextField amountField;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Button addButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transactionService = new TransactionService();
        transactionList = FXCollections.observableArrayList();
        
        // 初始化表格列
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // 初始化下拉框
        categoryComboBox.setItems(FXCollections.observableArrayList("收入", "支出"));
        typeComboBox.setItems(FXCollections.observableArrayList("工资", "奖金", "餐饮", "购物", "交通", "住房", "娱乐", "其他"));
        
        // 加载数据
        loadTransactions();
    }
    
    /**
     * 加载交易记录数据
     */
    private void loadTransactions() {
        transactionList.clear();
        transactionList.addAll(transactionService.getAllTransactions());
        transactionTable.setItems(transactionList);
    }
    
    /**
     * 添加新交易记录
     */
    @FXML
    private void handleAddTransaction() {
        try {
            String category = categoryComboBox.getValue();
            String type = typeComboBox.getValue();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionArea.getText();
            
            Transaction transaction = new Transaction(category, type, amount, description, LocalDateTime.now());
            transactionService.addTransaction(transaction);
            
            // 清空输入框
            categoryComboBox.setValue(null);
            typeComboBox.setValue(null);
            amountField.clear();
            descriptionArea.clear();
            
            // 刷新表格
            loadTransactions();
            
        } catch (NumberFormatException e) {
            showAlert("金额必须是有效的数字");
        } catch (Exception e) {
            showAlert("添加交易记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 显示提示对话框
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}