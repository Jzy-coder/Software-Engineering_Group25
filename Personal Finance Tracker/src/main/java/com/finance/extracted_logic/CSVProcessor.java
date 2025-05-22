package com.finance.extracted_logic;

import java.util.Set;

public class CSVProcessor {
    private static final Set<String> VALID_INCOME_TYPES = Set.of("Salary", "Bonus", "Others");
    private static final Set<String> VALID_EXPENSE_TYPES = Set.of("Food", "Shopping", "Transportation", "Housing", "Entertainment", "Others");

    public static String mapChineseType(String chineseType) {
        switch(chineseType) {
            case "餐饮": return "Food";
            case "购物": return "Shopping";
            case "交通": return "Transportation";
            case "住房": return "Housing";
            case "娱乐": return "Entertainment";
            case "工资": return "Salary";
            case "奖金": return "Bonus";
            default: return "Others";
        }
    }

    public static boolean isValidTransaction(String category, String type) {
        if (category.equals("Income")) {
            return VALID_INCOME_TYPES.contains(type);
        } else if (category.equals("Expense")) {
            return VALID_EXPENSE_TYPES.contains(type);
        }
        return false;
    }

    public static String cleanAmountString(String amountStr) {
        return amountStr.replaceAll("[^\\d.,]", "").replace(",", "").trim();
    }

    public static String extractDateOnly(String dateStr) {
        return dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
    }

    public static String determineCategory(String type) {
        return type.startsWith("/") ? "Expense" : "Income";
    }
}