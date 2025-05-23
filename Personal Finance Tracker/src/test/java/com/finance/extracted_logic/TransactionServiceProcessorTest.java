package com.finance.extracted_logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.finance.dao.TransactionDAO;
import com.finance.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionServiceProcessorTest {
    
    private TransactionServiceProcessor processor;
    private TransactionDAO mockDao;
    
    @BeforeEach
    void setUp() {
        mockDao = mock(TransactionDAO.class);
        processor = new TransactionServiceProcessor(mockDao);
    }
    
    @Test
    void getNextTransactionId_EmptyList_ReturnsOne() {
        List<Transaction> emptyList = List.of();
        assertEquals(1L, processor.getNextTransactionId(emptyList));
    }
    
    @Test
    void getNextTransactionId_NonEmptyList_ReturnsMaxIdPlusOne() {
        Transaction t1 = new Transaction("Income", "", 100.0, "", LocalDateTime.now());
        t1.setId(1L);
        Transaction t2 = new Transaction("Expense", "", 50.0, "", LocalDateTime.now());
        t2.setId(5L);
        List<Transaction> transactions = Arrays.asList(t1, t2);
        
        assertEquals(6L, processor.getNextTransactionId(transactions));
    }
    
    @Test
    void calculateBalance_IncomeAndExpense_ReturnsCorrectBalance() {
        Transaction t1 = new Transaction("Income", "", 200.0, "", LocalDateTime.now());
        t1.setId(1L);
        Transaction t2 = new Transaction("Expense", "", 50.0, "", LocalDateTime.now());
        t2.setId(2L);
        Transaction t3 = new Transaction("Income", "", 100.0, "", LocalDateTime.now());
        t3.setId(3L);
        List<Transaction> transactions = Arrays.asList(t1, t2, t3);
        
        assertEquals(250.0, processor.calculateBalance(transactions));
    }
    
    @Test
    void filterTransactionsByDate_ReturnsOnlyMatchingDate() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        Transaction t1 = new Transaction("Income", "", 100.0, "", yesterday.atStartOfDay());
        t1.setId(1L);
        Transaction t2 = new Transaction("Expense", "", 50.0, "", today.atStartOfDay());
        t2.setId(2L);
        List<Transaction> transactions = Arrays.asList(t1, t2);
        
        List<Transaction> result = processor.filterTransactionsByDate(transactions, today);
        
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }
    
    @Test
    void filterTransactionsByDateRange_ReturnsOnlyInRange() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);
        LocalDate before = start.minusDays(1);
        LocalDate after = end.plusDays(1);
        
        Transaction t1 = new Transaction("Income", "", 100.0, "", before.atStartOfDay());
        t1.setId(1L);
        Transaction t2 = new Transaction("Expense", "", 50.0, "", start.atStartOfDay());
        t2.setId(2L);
        Transaction t3 = new Transaction("Income", "", 75.0, "", end.atStartOfDay());
        t3.setId(3L);
        Transaction t4 = new Transaction("Expense", "", 25.0, "", after.atStartOfDay());
        t4.setId(4L);
        List<Transaction> transactions = Arrays.asList(t1, t2, t3, t4);
        
        List<Transaction> result = processor.filterTransactionsByDateRange(transactions, start, end);
        
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getId() == 2L));
        assertTrue(result.stream().anyMatch(t -> t.getId() == 3L));
    }
    
    @Test
    void calculateTotalByCategory_ReturnsCorrectSum() {
        Transaction t1 = new Transaction("Income", "", 100.0, "", LocalDateTime.now());
        t1.setId(1L);
        Transaction t2 = new Transaction("Income", "", 50.0, "", LocalDateTime.now());
        t2.setId(2L);
        Transaction t3 = new Transaction("Expense", "", 75.0, "", LocalDateTime.now());
        t3.setId(3L);
        List<Transaction> transactions = Arrays.asList(t1, t2, t3);
        
        double incomeTotal = processor.calculateTotalByCategory(transactions, "Income");
        double expenseTotal = processor.calculateTotalByCategory(transactions, "Expense");
        
        assertEquals(150.0, incomeTotal);
        assertEquals(75.0, expenseTotal);
    }
}