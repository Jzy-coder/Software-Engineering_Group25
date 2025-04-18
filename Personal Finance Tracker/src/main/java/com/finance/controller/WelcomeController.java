package com.finance.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import com.finance.gui.LoginManager;
import com.finance.model.Budget;
import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import com.finance.util.BudgetDataManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Welcome page controller
 */
public class WelcomeController implements Initializable {

    @FXML
    private Label userNameLabel;
    @FXML
    private Label totalTodayExpenditureLabel;
   
    @FXML
    private Label balanceLabel;
    @FXML
    private TableView<Transaction> expenditureDetailsTable;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    @FXML
    private TableColumn<Transaction, String> typeColumn;


    private TransactionService transactionService;
    private ObservableList<Transaction> todayExpenditureList;
 


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transactionService = com.finance.gui.LoginManager.getTransactionService(); // Assuming it's initialized in LoginManager

        todayExpenditureList = FXCollections.observableArrayList();
        loadUserName();
        loadExpenditureDetails();
        updateSummary();
    }

    /**
     * Load the user's name (hardcoded or fetched from a service)
     */
    private void loadUserName() {
        // 获取当前登录的用户名
        String currentUsername = LoginManager.getCurrentUsername();
        
        // 设置欢迎标签为当前用户名
        userNameLabel.setText("Hi, " + currentUsername + " ~");
    }


    /**
     * Load today's expenditure details into the table
     */
    private void loadExpenditureDetails() {
        // Get all transactions for today
        List<Transaction> todayTransactions = transactionService.getTransactionsByDate(LocalDate.now());

        todayExpenditureList.clear();
        todayExpenditureList.addAll(todayTransactions);

        // Populate table with today's expenditure details
        expenditureDetailsTable.setItems(todayExpenditureList);

        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

    }


    /**
     * Update the summary information (total expenditure and balance)
     */
    private void updateSummary() {

        
        double totalTodayExpenditure = calculateTotalTodayExpenditure();
        double balance = calculateBalance();

        totalTodayExpenditureLabel.setText("Today's total expenditure is: " + totalTodayExpenditure);
        balanceLabel.setText("Today's income/expense difference is: " + balance + " yuan"); 
    }    


    /**
     * Calculate today's total expenditure
     */
    private double calculateTotalTodayExpenditure() {
        double total = 0;
        for (Transaction transaction : todayExpenditureList) {
            if ("Expense".equals(transaction.getCategory())) {
                total += transaction.getAmount();
            }
        }
        return total;
    }


    /**
     * 计算当日收支差额（收入-支出）
     */
    private double calculateBalance() {
        double totalIncome = 0.0;
        double totalExpense = 0.0;
        
        for (Transaction transaction : todayExpenditureList) {
            if ("Income".equals(transaction.getCategory())) {
                totalIncome += transaction.getAmount();
            } else if ("Expense".equals(transaction.getCategory())) {
                totalExpense += transaction.getAmount();
            }
        }
        
        return totalIncome - totalExpense;
    }


}    



