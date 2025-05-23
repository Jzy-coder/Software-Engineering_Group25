package com.finance.extracted_logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.finance.model.Transaction;

public class TransactionProcessor {
    private List<Transaction> transactions;
    
    public TransactionProcessor(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
    
    public void deleteTransaction(Long id) {
        transactions.removeIf(t -> t.getId().equals(id));
    }
    
    public void updateTransaction(Transaction transaction) {
        deleteTransaction(transaction.getId());
        addTransaction(transaction);
    }
    
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactions.stream()
            .filter(t -> t.getDate().toLocalDate().isEqual(date))
            .toList();
    }
    
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        
        return transactions.stream()
            .filter(t -> {
                LocalDateTime transactionDate = t.getDate();
                return !transactionDate.isBefore(startDateTime) && !transactionDate.isAfter(endDateTime);
            })
            .toList();
    }
    
    public double calculateTotalByCategory(String category) {
        return transactions.stream()
            .filter(t -> t.getCategory().equals(category))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public double calculateBalance() {
        return transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public double calculateBalanceByDateRange(LocalDate startDate, LocalDate endDate) {
        return getTransactionsByDateRange(startDate, endDate).stream()
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public double calculateTotalByCategoryAndDateRange(String category, 
                                                     LocalDate startDate, 
                                                     LocalDate endDate) {
        return getTransactionsByDateRange(startDate, endDate).stream()
            .filter(t -> t.getCategory().equals(category))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
}