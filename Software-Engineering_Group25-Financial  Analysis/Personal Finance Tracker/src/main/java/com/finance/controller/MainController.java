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
 * Main Interface Controller
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
        
        // Initialize table columns
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // Initialize dropdown boxes
        categoryComboBox.setItems(FXCollections.observableArrayList("Income", "Expense"));
        typeComboBox.setItems(FXCollections.observableArrayList("Salary", "Bonus", "Food", "Shopping", "Transportation", "Housing", "Entertainment", "Others"));
        
        // Load data
        loadTransactions();
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
            String category = categoryComboBox.getValue();
            String type = typeComboBox.getValue();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionArea.getText();
            
            Transaction transaction = new Transaction(category, type, amount, description, LocalDateTime.now());
            transactionService.addTransaction(transaction);
            
            // Clear input fields
            categoryComboBox.setValue(null);
            typeComboBox.setValue(null);
            amountField.clear();
            descriptionArea.clear();
            
            // Refresh table
            loadTransactions();
            
        } catch (NumberFormatException e) {
            showAlert("Amount must be a valid number");
        } catch (Exception e) {
            showAlert("Failed to add transaction: " + e.getMessage());
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
}