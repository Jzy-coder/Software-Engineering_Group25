package com.finance.extracted_logic;

import java.util.List;

public class BudgetValidationLogic {

    public static boolean validatePlannedAmount(double plannedAmount) {
        return plannedAmount > 0;
    }
    
    public static boolean validateActualAmount(double actualAmount, double plannedAmount) {
        return actualAmount >= 0 && actualAmount < plannedAmount;
    }
    
    public static boolean validatePlans(List<String> plans) {
        return plans != null && !plans.isEmpty();
    }
    
    public static void validateInput(String name, double plannedAmount, double actualAmount) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!validatePlannedAmount(plannedAmount)) {
            throw new IllegalArgumentException("Planned amount must > 0");
        }
        if (actualAmount < 0) {
            throw new IllegalArgumentException("Actual amount cannot be negative");
        }
        if (!validateActualAmount(actualAmount, plannedAmount)) {
            throw new IllegalArgumentException("Planned must > Actual");
        }
    }
    
    public static void validateSerialization(List<String> plans) {
        if (!validatePlans(plans)) {
            throw new IllegalArgumentException("Plans cannot be empty");
        }
    }
    
    public static void validateGetSetMethods(String name, double plannedAmount, double actualAmount) {
        validateInput(name, plannedAmount, actualAmount);
    }
}