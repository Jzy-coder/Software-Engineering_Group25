package com.finance.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.finance.dao.TransactionDAO;
import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import com.finance.result.ImportResult;

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
    public ImportResult batchImport(List<Transaction> transactions) {
        List<Transaction> existingTransactions = transactionDAO.getAllTransactions();
        int importedCount = 0;
        int skippedCount = 0;

        for (Transaction transaction : transactions) {
            // Simple duplicate check based on key fields (excluding ID)
            boolean isDuplicate = existingTransactions.stream().anyMatch(existing ->
                existing.getDate().equals(transaction.getDate()) &&
                existing.getCategory().equals(transaction.getCategory()) &&
                existing.getType().equals(transaction.getType()) &&
                existing.getAmount() == transaction.getAmount() &&
                existing.getDescription().equals(transaction.getDescription())
            );

            if (!isDuplicate) {
                if (transaction.getId() == null) {
                    transaction.setId(getNextTransactionId());
                }
                transactionDAO.save(transaction);
                importedCount++;
            } else {
                skippedCount++;
            }
        }
        return new ImportResult(importedCount, skippedCount);
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
     * aquired by TransactionDAO
     * @param date 
     * @return 
     */
    @Override
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        // aquired by TransactionDAO
        return transactionDAO.getAllTransactions().stream()
                .filter(transaction -> transaction.getDate().toLocalDate().isEqual(date))
                .collect(Collectors.toList());
    }
    
    /**
     * aquired by TransactionDAO
     * @param startDate 
     * @param endDate 
     * @return 
     */
    @Override
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        // transformed by TransactionDAO
        LocalDateTime startDateTime = startDate.atStartOfDay();
        // transformed by TransactionDAO
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        
        // aquired by TransactionDAO
        return transactionDAO.getAllTransactions().stream()
                .filter(transaction -> {
                    LocalDateTime transactionDate = transaction.getDate();
                    return !transactionDate.isBefore(startDateTime) && !transactionDate.isAfter(endDateTime);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * evaluated by TransactionDAO
     * @param startDate 
     * @param endDate 
     * @return 
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
     * 计evaluated by TransactionDAO
     * @param category 
     * @param startDate 
     * @param endDate 
     * @return 
     */
    @Override
    public double calculateTotalByCategoryAndDateRange(String category, LocalDate startDate, LocalDate endDate) {
        return getTransactionsByDateRange(startDate, endDate).stream()
            .filter(t -> category.equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
}
