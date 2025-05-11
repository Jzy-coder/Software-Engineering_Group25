package com.finance.util;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.finance.model.Budget;

public class BudgetDataManager {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path BUDGET_FILE = DATA_DIR.resolve("budget.dat");
    private static final Path BUDGET_HISTORY_FILE = DATA_DIR.resolve("budget_history.dat");
    
    
    public static void saveBudget(Budget budget) {
        try {
            Files.createDirectories(DATA_DIR); // Auto create directory
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(BUDGET_FILE))) {
                oos.writeObject(budget);
                System.out.println("Budget saved to: " + BUDGET_FILE.toAbsolutePath());
            }
            
            // If budget is not null, add it to history
            if (budget != null) {
                addBudgetToHistory(budget);
            }
        } catch (IOException e) {
            System.err.println("Failed to save budget: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void addBudgetToHistory(Budget budget) {
        List<Budget> history = loadBudgetHistory();
        
        // Create a new Budget object to avoid reference issues
        Budget historicalBudget = new Budget(
            budget.getName(),
            budget.getPlannedAmount(),
            budget.getActualAmount()
        );
        
        // Set plans directly using the new overloaded method
        historicalBudget.setPlans(budget.getPlans());
        
        // Add to history and save
        history.add(historicalBudget);
        saveBudgetHistory(history);
    }

    public static Budget loadBudget() {
        if (!Files.exists(BUDGET_FILE)) {
            System.out.println("No budget file found at: " + BUDGET_FILE.toAbsolutePath());
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(BUDGET_FILE))) {
            Budget budget = (Budget) ois.readObject();
            System.out.println("Budget loaded from: " + BUDGET_FILE.toAbsolutePath());
            return budget;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load budget: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void handleUsernameChange(String oldUsername, String newUsername) {
        File oldFile = new File(DATA_DIR + "/budgets_" + oldUsername + ".dat");
        File newFile = new File(DATA_DIR + "/budgets_" + newUsername + ".dat");
        
        if (oldFile.exists()) {
            oldFile.renameTo(newFile);
        }
    }
    
    public static void saveBudgetHistory(List<Budget> budgetHistory) {
        try {
            Files.createDirectories(DATA_DIR);
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(BUDGET_HISTORY_FILE))) {
                oos.writeObject(new ArrayList<>(budgetHistory));
                System.out.println("Budget history saved to: " + BUDGET_HISTORY_FILE.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to save budget history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static List<Budget> loadBudgetHistory() {
        if (!Files.exists(BUDGET_HISTORY_FILE)) {
            System.out.println("No budget history file found at: " + BUDGET_HISTORY_FILE.toAbsolutePath());
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(BUDGET_HISTORY_FILE))) {
            List<Budget> budgetHistory = (List<Budget>) ois.readObject();
            System.out.println("Budget history loaded from: " + BUDGET_HISTORY_FILE.toAbsolutePath());
            return budgetHistory;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load budget history: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
