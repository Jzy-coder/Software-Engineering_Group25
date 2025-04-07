package com.finance.service;

import com.finance.dao.TransactionDAO;
import com.finance.event.TransactionEvent;
import com.finance.event.TransactionEventManager;
import com.finance.model.Transaction;
import java.time.LocalDateTime;

import java.util.List;

/**
 * Transaction Service class for handling business logic
 */
public class TransactionService {
    
    private TransactionDAO transactionDAO;
    
    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }
    
    /**
     * Clear transaction cache
     */
    public void clearCache() {
        // No implementation needed as we're directly reading from file each time
        // Just trigger a reload event
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.LOADED));
    }
    
    /**
     * Switch current user
     * @param username 用户名
     * @param isRename 是否为用户重命名场景
     */
    public void switchUser(String username, boolean isRename) {
        transactionDAO.updateCurrentUser(username, isRename);
        // Trigger data load event
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.LOADED));
    }
    
    /**
     * Get all transaction records
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionDAO.findAll();
        // Trigger data load event
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.LOADED));
        return transactions;
    }
    
    /**
     * Add new transaction record
     */
    public void addTransaction(Transaction transaction) {
        transactionDAO.save(transaction);
        // Trigger transaction add event
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.ADDED));
    }
    
    /**
     * Delete transaction record
     */
    public void deleteTransaction(Long id) {
        transactionDAO.delete(id);
        // Trigger transaction delete event
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.DELETED));
    }
    
    /**
     * Update transaction record
     */
    public void updateTransaction(Transaction transaction) {
        transactionDAO.update(transaction);
        // Trigger transaction update event
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.UPDATED));
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
    
    /**
     * Calculate total amount by category and date range
     */
    public double calculateTotalByCategoryAndDateRange(String category, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionDAO.findAll().stream()
                .filter(t -> t.getCategory().equals(category))
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}