package com.finance.service;

import java.time.LocalDate;
import java.util.List;

import com.finance.model.Transaction;

public interface TransactionService {
    void batchImport(List<Transaction> transactions);
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
     * 根据日期获取交易记录
     * @param date 指定的日期
     * @return 当天的交易记录列表
     */
    List<Transaction> getTransactionsByDate(LocalDate date);
}