package com.finance.extracted_logic;

import com.finance.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionCreatorTest {

    private TransactionCreator transactionCreator;

    @BeforeEach
    void setUp() {
        transactionCreator = new TransactionCreator();
    }

    @Test
    void testCreateTransaction_validInput_shouldSucceed() {
        Transaction transaction = transactionCreator.createTransaction("Income", "Salary", 1000.0, "Monthly salary", LocalDateTime.now());
        assertNotNull(transaction);
        assertEquals("Income", transaction.getCategory());
        assertEquals("Salary", transaction.getType());
        assertEquals(1000.0, transaction.getAmount());
        assertEquals("Monthly salary", transaction.getDescription());
        assertNotNull(transaction.getDate());
    }

    @Test
    void testCreateTransaction_validInputExpense_shouldSucceed() {
        Transaction transaction = transactionCreator.createTransaction("Expense", "Groceries", 50.0, "Weekly groceries", LocalDateTime.now());
        assertNotNull(transaction);
        assertEquals("Expense", transaction.getCategory());
        assertEquals("Groceries", transaction.getType());
        assertEquals(50.0, transaction.getAmount());
        assertEquals("Weekly groceries", transaction.getDescription());
        assertNotNull(transaction.getDate());
    }

    @Test
    void testCreateTransaction_nullDescription_shouldSucceed() {
        Transaction transaction = transactionCreator.createTransaction("Income", "Bonus", 500.0, null, LocalDateTime.now());
        assertNotNull(transaction);
        assertNull(transaction.getDescription());
    }

    @Test
    void testCreateTransaction_emptyDescription_shouldSucceed() {
        Transaction transaction = transactionCreator.createTransaction("Expense", "Utilities", 75.0, "", LocalDateTime.now());
        assertNotNull(transaction);
        assertEquals("", transaction.getDescription());
    }

    @Test
    void testCreateTransaction_nullCategory_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionCreator.createTransaction(null, "Salary", 1000.0, "Monthly salary", LocalDateTime.now());
        });
        assertEquals("Category cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCreateTransaction_emptyCategory_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionCreator.createTransaction("", "Salary", 1000.0, "Monthly salary", LocalDateTime.now());
        });
        assertEquals("Category cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCreateTransaction_nullType_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionCreator.createTransaction("Income", null, 1000.0, "Monthly salary", LocalDateTime.now());
        });
        assertEquals("Type cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCreateTransaction_emptyType_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionCreator.createTransaction("Income", "", 1000.0, "Monthly salary", LocalDateTime.now());
        });
        assertEquals("Type cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCreateTransaction_zeroAmount_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionCreator.createTransaction("Income", "Salary", 0.0, "Monthly salary", LocalDateTime.now());
        });
        assertEquals("Amount must be positive.", exception.getMessage());
    }

    @Test
    void testCreateTransaction_negativeAmount_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionCreator.createTransaction("Income", "Salary", -100.0, "Monthly salary", LocalDateTime.now());
        });
        assertEquals("Amount must be positive.", exception.getMessage());
    }

    @Test
    void testCreateTransaction_nullDate_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionCreator.createTransaction("Income", "Salary", 1000.0, "Monthly salary", null);
        });
        assertEquals("Date cannot be null.", exception.getMessage());
    }
}