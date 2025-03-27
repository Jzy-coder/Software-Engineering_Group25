package com.finance.model;

import java.time.LocalDateTime;

/**
 * Transaction Entity Class
 */
public class Transaction {
    private Long id;
    private String category;      // Category (Income/Expense)
    private String type;          // Type (Salary/Food/Shopping etc.)
    private double amount;        // Amount
    private String description;   // Description
    private LocalDateTime date;   // Transaction Date
    
    public Transaction() {
    }
    
    public Transaction(String category, String type, double amount, String description, LocalDateTime date) {
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}