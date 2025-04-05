package com.finance.service;

import com.finance.dao.TransactionDAO;
import com.finance.model.Transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Transaction Service class for handling business logic
 */
public class TransactionService {


    private LocalDate date;
    private TransactionDAO transactionDAO;
    
    // 修改构造函数
    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }

    // 修改 getter 和 setter
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Get all transaction records
     */
    public List<Transaction> getAllTransactions() {
        return transactionDAO.findAll();
    }
    
    /**
     * Add new transaction record
     */
    public void addTransaction(Transaction transaction) {
        transactionDAO.save(transaction);
    }
    
    /**
     * Delete transaction record
     */
    public void deleteTransaction(Long id) {
        transactionDAO.delete(id);
    }
    
    /**
     * Update transaction record
     */
    public void updateTransaction(Transaction transaction) {
        transactionDAO.update(transaction);
    }
    
    /**
     * Calculate total amount by category
     */
    public double calculateTotalByCategory(String category) {
        return transactionDAO.findAll().stream()
                .filter(t -> t.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    /**
     * Calculate balance (income - expense)
     */
    public double calculateBalance() {
        double income = calculateTotalByCategory("Income");
        double expense = calculateTotalByCategory("Expense");
        return income - expense;
    }
}