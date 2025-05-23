package com.finance.extracted_logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.finance.dao.TransactionDAO;
import com.finance.model.Transaction;

public class TransactionServiceProcessor {
    public TransactionServiceProcessor(TransactionDAO transactionDAO) {
    }
    
    public long getNextTransactionId(List<Transaction> transactions) {
        return transactions.stream()
                .mapToLong(Transaction::getId)
                .max()
                .orElse(0L) + 1;
    }
    
    public double calculateBalance(List<Transaction> transactions) {
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
    
    public List<Transaction> filterTransactionsByDate(List<Transaction> transactions, LocalDate date) {
        return transactions.stream()
                .filter(transaction -> transaction.getDate().toLocalDate().isEqual(date))
                .collect(Collectors.toList());
    }
    
    public List<Transaction> filterTransactionsByDateRange(List<Transaction> transactions, 
            LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        
        return transactions.stream()
                .filter(transaction -> {
                    LocalDateTime transactionDate = transaction.getDate();
                    return !transactionDate.isBefore(startDateTime) && !transactionDate.isAfter(endDateTime);
                })
                .collect(Collectors.toList());
    }
    
    public double calculateTotalByCategory(List<Transaction> transactions, String category) {
        return transactions.stream()
            .filter(t -> category.equals(t.getCategory()))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
}