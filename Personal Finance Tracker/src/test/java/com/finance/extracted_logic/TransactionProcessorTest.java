package com.finance.extracted_logic;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.finance.model.Transaction;

class TransactionProcessorTest {
    
    private TransactionProcessor processor;
    private List<Transaction> testTransactions;
    
    @BeforeEach
    void setUp() {
        testTransactions = new ArrayList<>();
        processor = new TransactionProcessor(testTransactions);
    }
    
    @Test
    void testAddTransaction() {
        Transaction t = new Transaction("Income", "Salary", 100.0, "Test income", LocalDate.now().atStartOfDay());
        processor.addTransaction(t);
        
        assertEquals(1, testTransactions.size());
        assertEquals(t, testTransactions.get(0));
    }
    
    @Test
    void testDeleteTransaction() {
        Transaction t = new Transaction("Income", "Salary", 100.0, "Test income", LocalDate.now().atStartOfDay());
        t.setId(1L);
        testTransactions.add(t);
        
        processor.deleteTransaction(1L);
        assertTrue(testTransactions.isEmpty());
    }
    
    @Test
    void testUpdateTransaction() {
        Transaction original = new Transaction("Income", "Salary", 100.0, "Test income", LocalDate.now().atStartOfDay());
        original.setId(1L);
        testTransactions.add(original);
        
        Transaction updated = new Transaction("Expense", "Food", 200.0, "Test expense", LocalDate.now().atStartOfDay());
        updated.setId(1L);
        processor.updateTransaction(updated);
        
        assertEquals(1, testTransactions.size());
        assertEquals(updated.getAmount(), testTransactions.get(0).getAmount());
    }
    
    @Test
    void testGetTransactionsByDate() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        testTransactions.add(new Transaction("Income", "Salary", 100.0, "Test income", today.atStartOfDay()));
        testTransactions.add(new Transaction("Expense", "Food", 50.0, "Test expense", yesterday.atStartOfDay()));
        
        List<Transaction> result = processor.getTransactionsByDate(today);
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getAmount());
    }
    
    @Test
    void testGetTransactionsByDateRange() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        
        testTransactions.add(new Transaction("Income", "Salary", 100.0, "Test income", start.plusDays(1).atStartOfDay()));
        testTransactions.add(new Transaction("Expense", "Food", 50.0, "Test expense", end.minusDays(1).atStartOfDay()));
        testTransactions.add(new Transaction("Income", "Bonus", 200.0, "Test bonus", end.plusDays(1).atStartOfDay()));
        
        List<Transaction> result = processor.getTransactionsByDateRange(start, end);
        assertEquals(2, result.size());
    }
    
    @Test
    void testCalculateTotalByCategory() {
        testTransactions.add(new Transaction("Income", "Salary", 100.0, "Test income", LocalDate.now().atStartOfDay()));
        testTransactions.add(new Transaction("Expense", "Food", 50.0, "Test expense", LocalDate.now().atStartOfDay()));
        testTransactions.add(new Transaction("Income", "Bonus", 75.0, "Test bonus", LocalDate.now().atStartOfDay()));
        
        double totalIncome = processor.calculateTotalByCategory("Income");
        assertEquals(175.0, totalIncome);
    }
    
    @Test
    void testCalculateBalance() {
        testTransactions.add(new Transaction("Income", "Salary", 100.0, "Test income", LocalDate.now().atStartOfDay()));
        testTransactions.add(new Transaction("Expense", "Food", -50.0, "Test expense", LocalDate.now().atStartOfDay()));
        
        double balance = processor.calculateBalance();
        assertEquals(50.0, balance);
    }
    
    @Test
    void testCalculateBalanceByDateRange() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        
        testTransactions.add(new Transaction("Income", "Salary", 100.0, "Test income", start.plusDays(1).atStartOfDay()));
        testTransactions.add(new Transaction("Expense", "Food", -50.0, "Test expense", end.minusDays(1).atStartOfDay()));
        testTransactions.add(new Transaction("Income", "Bonus", 200.0, "Test bonus", end.plusDays(1).atStartOfDay()));
        
        double balance = processor.calculateBalanceByDateRange(start, end);
        assertEquals(50.0, balance);
    }
    
    @Test
    void testCalculateTotalByCategoryAndDateRange() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        
        testTransactions.add(new Transaction("Income", "Salary", 100.0, "Test income", start.plusDays(1).atStartOfDay()));
        testTransactions.add(new Transaction("Expense", "Food", 50.0, "Test expense", end.minusDays(1).atStartOfDay()));
        testTransactions.add(new Transaction("Income", "Bonus", 200.0, "Test bonus", end.plusDays(1).atStartOfDay()));
        
        double totalIncome = processor.calculateTotalByCategoryAndDateRange("Income", start, end);
        assertEquals(100.0, totalIncome);
    }
}