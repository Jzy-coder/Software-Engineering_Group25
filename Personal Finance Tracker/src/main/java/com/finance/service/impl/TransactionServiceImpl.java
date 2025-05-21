package com.finance.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.finance.dao.TransactionDAO;
import com.finance.model.Transaction;
import com.finance.service.TransactionService;

public class TransactionServiceImpl implements TransactionService {
    private TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    public void addTransaction(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(getNextTransactionId());
        }
        transactionDAO.save(transaction);
    }

    @Override
    public long getNextTransactionId() {
        return transactionDAO.getAllTransactions().stream()
                .mapToLong(Transaction::getId)
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public void batchImport(List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            if (transaction.getId() == null) {
                transaction.setId(getNextTransactionId());
            }
            transactionDAO.save(transaction);
        });
    }

    @Override
    public double calculateBalance() {
        List<Transaction> transactions = getAllTransactions();
        double totalIncome = transactions.stream()
            .filter(t -> "Income".equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        double totalExpense = transactions.stream()
            .filter(t -> "Expense".equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        return totalIncome - totalExpense;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        transactionDAO.update(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionDAO.deleteById(id);
    }

    @Override
    public double calculateTotalByCategory(String category) {
        return transactionDAO.getAllTransactions().stream()
            .filter(t -> category.equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    @Override
    public void switchUser(String username, boolean isRename) {
        transactionDAO.switchUser(username, isRename);
        clearCache();
    }

    @Override
    public void clearCache() {
        transactionDAO.clearCache();
    }

    /**
     * 根据日期获取交易记录
     * @param date 指定的日期
     * @return 当天的交易记录列表
     */
    @Override
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        // 获取所有交易记录并过滤出指定日期的记录
        return transactionDAO.getAllTransactions().stream()
                .filter(transaction -> transaction.getDate().toLocalDate().isEqual(date))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据日期范围获取交易记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期范围内的交易记录列表
     */
    @Override
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        // 将开始日期转换为当天的开始时间（00:00:00）
        LocalDateTime startDateTime = startDate.atStartOfDay();
        // 将结束日期转换为当天的结束时间（23:59:59.999999999）
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        
        // 获取所有交易记录并过滤出指定日期范围内的记录
        return transactionDAO.getAllTransactions().stream()
                .filter(transaction -> {
                    LocalDateTime transactionDate = transaction.getDate();
                    return !transactionDate.isBefore(startDateTime) && !transactionDate.isAfter(endDateTime);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 计算指定日期范围内的余额
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期范围内的余额
     */
    @Override
    public double calculateBalanceByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = getTransactionsByDateRange(startDate, endDate);
        double totalIncome = transactions.stream()
            .filter(t -> "Income".equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        double totalExpense = transactions.stream()
            .filter(t -> "Expense".equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        return totalIncome - totalExpense;
    }
    
    /**
     * 计算指定日期范围内某类别的总金额
     * @param category 类别（Income/Expense）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期范围内指定类别的总金额
     */
    @Override
    public double calculateTotalByCategoryAndDateRange(String category, LocalDate startDate, LocalDate endDate) {
        return getTransactionsByDateRange(startDate, endDate).stream()
            .filter(t -> category.equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
}
