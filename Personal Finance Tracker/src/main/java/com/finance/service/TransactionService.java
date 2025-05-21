package com.finance.service;

import java.time.LocalDate;
import java.util.List;

import com.finance.model.Transaction;
import com.finance.result.ImportResult;

public interface TransactionService {
    ImportResult batchImport(List<Transaction> transactions);
    void clearCache();
    void switchUser(String username, boolean isRename);
    List<Transaction> getAllTransactions();
    void addTransaction(Transaction transaction);
    void deleteTransaction(Long id);
    void updateTransaction(Transaction transaction);
    double calculateTotalByCategory(String category);
    double calculateBalance();
    long getNextTransactionId();
    /**
     * aquire the transactions by date
     * @param date 
     * @return 
     */
    List<Transaction> getTransactionsByDate(LocalDate date);
    
    /**
     * aquire the transactions by date
     * @param startDate 
     * @param endDate 
     * @return 
     */
    List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 
     * @param startDate 
     * @param endDate 
     * @return 
     */
    double calculateBalanceByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * @param category 
     * @param startDate 
     * @param endDate 
     * @return 
     */
    double calculateTotalByCategoryAndDateRange(String category, LocalDate startDate, LocalDate endDate);
}