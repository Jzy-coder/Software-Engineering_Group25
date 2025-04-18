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
    
    /**
     * 根据日期范围获取交易记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期范围内的交易记录列表
     */
    List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 计算指定日期范围内的余额
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期范围内的余额
     */
    double calculateBalanceByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 计算指定日期范围内某类别的总金额
     * @param category 类别（Income/Expense）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期范围内指定类别的总金额
     */
    double calculateTotalByCategoryAndDateRange(String category, LocalDate startDate, LocalDate endDate);
}