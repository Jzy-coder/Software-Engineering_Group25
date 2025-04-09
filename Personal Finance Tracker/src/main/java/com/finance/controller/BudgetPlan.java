package com.finance.controller;

import java.time.LocalDate;

public class BudgetPlan {
    private String planName;
    private double budgetAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String category;

    public BudgetPlan(String planName, double budgetAmount, LocalDate startDate, LocalDate endDate, String category) {
        this.planName = planName;
        this.budgetAmount = budgetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
    }

    // Getters and Setters
    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}