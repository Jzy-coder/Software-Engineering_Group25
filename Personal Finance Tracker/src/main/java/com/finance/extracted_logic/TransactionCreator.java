package com.finance.extracted_logic;

import com.finance.model.Transaction;
import java.time.LocalDateTime;

/**
 * Responsible for creating Transaction objects.
 */
public class TransactionCreator {

    /**
     * Creates a new Transaction object.
     *
     * @param category    The category of the transaction (e.g., "Income", "Expense").
     * @param type        The type of the transaction (e.g., "Salary", "Food").
     * @param amount      The amount of the transaction.
     * @param description A description for the transaction (can be null or empty).
     * @param date        The date and time of the transaction.
     * @return A new Transaction object.
     * @throws IllegalArgumentException if category, type, or date is null, or if amount is not positive.
     */
    public Transaction createTransaction(String category, String type, double amount, String description, LocalDateTime date) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty.");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }

        // Description can be null or empty, so no specific validation for it here unless required.
        return new Transaction(category, type, amount, description, date);
    }
}