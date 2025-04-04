package com.finance.service;

import com.finance.dao.TransactionDAO;
import com.finance.event.TransactionEvent;
import com.finance.event.TransactionEventManager;
import com.finance.model.Transaction;

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
     * Get all transaction records
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionDAO.findAll();
        // 触发数据加载事件
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.LOADED));
        return transactions;
    }
    
    /**
     * Add new transaction record
     */
    public void addTransaction(Transaction transaction) {
        transactionDAO.save(transaction);
        // 触发交易添加事件
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.ADDED));
    }
    
    /**
     * Delete transaction record
     */
    public void deleteTransaction(Long id) {
        transactionDAO.delete(id);
        // 触发交易删除事件
        TransactionEventManager.getInstance().fireTransactionEvent(
            new TransactionEvent(this, TransactionEvent.EventType.DELETED));
    }
    
    /**
     * Update transaction record
     */
    public void updateTransaction(Transaction transaction) {
        transactionDAO.update(transaction);
        // 触发交易更新事件
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
}