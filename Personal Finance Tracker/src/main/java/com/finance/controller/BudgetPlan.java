package com.finance.controller;

import java.time.LocalDate;

public class BudgetPlan {
    private String planName;
    private double budgetAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String category;
    private double progress;
    private double doneAmount;

    public BudgetPlan(String planName, double budgetAmount, LocalDate startDate, LocalDate endDate, String category) {
        this.planName = planName;
        this.budgetAmount = budgetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.progress = 0.0; // 初始进度为0
        this.doneAmount = 0.0; // 初始已完成金额为0
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

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getDoneAmount() {
        return doneAmount;
    }

    public void setDoneAmount(double doneAmount) {
        this.doneAmount = doneAmount;
    }


}