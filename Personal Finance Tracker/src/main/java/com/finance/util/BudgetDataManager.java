package com.finance.util;

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
    private static final String BUDGET_FILE_TEMPLATE = "%s_budget.dat";
    private static final String BUDGET_HISTORY_FILE_TEMPLATE = "%s_budget_history.dat";
    
    
    public static void saveBudget(Budget budget) {
        try {
            Files.createDirectories(DATA_DIR); // Auto create directory
            String currentUsername = com.finance.gui.LoginManager.getCurrentUsername();
            Path budgetFile = DATA_DIR.resolve(String.format(BUDGET_FILE_TEMPLATE, currentUsername));
            
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(budgetFile))) {
                oos.writeObject(budget);
                System.out.println("Budget saved to: " + budgetFile.toAbsolutePath());
            }
            
        } catch (IOException e) {
            System.err.println("Failed to save budget: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @param budget 
     */
    public static void addBudgetToHistory(Budget budget) {
        if (budget == null) return;
        
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
        String currentUsername = com.finance.gui.LoginManager.getCurrentUsername();
        Path budgetFile = DATA_DIR.resolve(String.format(BUDGET_FILE_TEMPLATE, currentUsername));
        
        if (!Files.exists(budgetFile)) {
            System.out.println("No budget file found at: " + budgetFile.toAbsolutePath());
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(budgetFile))) {
            Budget budget = (Budget) ois.readObject();
            System.out.println("Budget loaded from: " + budgetFile.toAbsolutePath());
            return budget;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load budget: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void handleUsernameChange(String oldUsername, String newUsername) {
        // rename budget file
        Path oldBudgetFile = DATA_DIR.resolve(String.format(BUDGET_FILE_TEMPLATE, oldUsername));
        Path newBudgetFile = DATA_DIR.resolve(String.format(BUDGET_FILE_TEMPLATE, newUsername));
        
        if (Files.exists(oldBudgetFile)) {
            try {
                Files.move(oldBudgetFile, newBudgetFile);
                System.out.println("Budget file renamed from " + oldBudgetFile + " to " + newBudgetFile);
            } catch (IOException e) {
                System.err.println("Failed to rename budget file: " + e.getMessage());
            }
        }
        
        // handle history file renaming if neede
        Path oldHistoryFile = DATA_DIR.resolve(String.format(BUDGET_HISTORY_FILE_TEMPLATE, oldUsername));
        Path newHistoryFile = DATA_DIR.resolve(String.format(BUDGET_HISTORY_FILE_TEMPLATE, newUsername));
        
        if (Files.exists(oldHistoryFile)) {
            try {
                Files.move(oldHistoryFile, newHistoryFile);
                System.out.println("Budget history file renamed from " + oldHistoryFile + " to " + newHistoryFile);
            } catch (IOException e) {
                System.err.println("Failed to rename budget history file: " + e.getMessage());
            }
        }
    }
    
    public static void saveBudgetHistory(List<Budget> budgetHistory) {
        try {
            Files.createDirectories(DATA_DIR);
            String currentUsername = com.finance.gui.LoginManager.getCurrentUsername();
            Path historyFile = DATA_DIR.resolve(String.format(BUDGET_HISTORY_FILE_TEMPLATE, currentUsername));
            
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(historyFile))) {
                oos.writeObject(new ArrayList<>(budgetHistory));
                System.out.println("Budget history saved to: " + historyFile.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to save budget history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static List<Budget> loadBudgetHistory() {
        String currentUsername = com.finance.gui.LoginManager.getCurrentUsername();
        Path historyFile = DATA_DIR.resolve(String.format(BUDGET_HISTORY_FILE_TEMPLATE, currentUsername));
        
        if (!Files.exists(historyFile)) {
            System.out.println("No budget history file found at: " + historyFile.toAbsolutePath());
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(historyFile))) {
            @SuppressWarnings("unchecked")
            List<Budget> budgetHistory = (List<Budget>) ois.readObject();
            System.out.println("Budget history loaded from: " + historyFile.toAbsolutePath());
            return budgetHistory;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load budget history: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * updateBudgetInHistory
     * @param oldBudget 
     * @param newBudget 
     */
    public static void updateBudgetInHistory(Budget oldBudget, Budget newBudget) {
        if (oldBudget == null || newBudget == null) return;
        
        List<Budget> history = loadBudgetHistory();
        boolean found = false;
        
        // find and update the budget in history
        for (int i = 0; i < history.size(); i++) {
            Budget historyBudget = history.get(i);
            // ensure the budget is the same
            if (historyBudget.getName().equals(oldBudget.getName()) && 
                Math.abs(historyBudget.getPlannedAmount() - oldBudget.getPlannedAmount()) < 0.01 && 
                Math.abs(historyBudget.getActualAmount() - oldBudget.getActualAmount()) < 0.01) {
                
                // update the budget
                historyBudget.setName(newBudget.getName());
                historyBudget.setPlannedAmount(newBudget.getPlannedAmount());
                historyBudget.setActualAmount(newBudget.getActualAmount());
                historyBudget.setPlans(newBudget.getPlans());
                
                found = true;
                break;
            }
        }
        
        // if not found, add the new budget to history
        if (!found) {
            addBudgetToHistory(newBudget);
        } else {
            // store the updated history back to the file
            saveBudgetHistory(history);
        }
    }
}
