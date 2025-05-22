package com.finance.extracted_logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.finance.model.Budget;

public class BudgetDataProcessor {
    private final Path dataDir;
    private static final String BUDGET_FILE_TEMPLATE = "%s_budget.dat";
    private static final String BUDGET_HISTORY_FILE_TEMPLATE = "%s_budget_history.dat";
    
    public BudgetDataProcessor(String dataDirPath) {
        this.dataDir = Paths.get(dataDirPath);
    }
    
    public void saveBudget(String username, Budget budget) throws IOException {
        Files.createDirectories(dataDir);
        Path budgetFile = dataDir.resolve(String.format(BUDGET_FILE_TEMPLATE, username));
        
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(budgetFile))) {
            oos.writeObject(budget);
        }
        
        if (budget != null) {
            addBudgetToHistory(username, budget);
        }
    }
    
    public Budget loadBudget(String username) throws IOException {
        Path budgetFile = dataDir.resolve(String.format(BUDGET_FILE_TEMPLATE, username));
        
        if (!Files.exists(budgetFile)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(budgetFile))) {
            return (Budget) ois.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load budget: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public void addBudgetToHistory(String username, Budget budget) throws IOException {
        List<Budget> history = loadBudgetHistory(username);
        Budget historicalBudget = new Budget(
            budget.getName(),
            budget.getPlannedAmount(),
            budget.getActualAmount()
        );
        historicalBudget.setPlans(budget.getPlans());
        history.add(historicalBudget);
        saveBudgetHistory(username, history);
    }
    
    public void handleUsernameChange(String oldUsername, String newUsername) throws IOException {
        Path oldBudgetFile = dataDir.resolve(String.format(BUDGET_FILE_TEMPLATE, oldUsername));
        Path newBudgetFile = dataDir.resolve(String.format(BUDGET_FILE_TEMPLATE, newUsername));
        
        if (Files.exists(oldBudgetFile)) {
            Files.move(oldBudgetFile, newBudgetFile);
        }
        
        Path oldHistoryFile = dataDir.resolve(String.format(BUDGET_HISTORY_FILE_TEMPLATE, oldUsername));
        Path newHistoryFile = dataDir.resolve(String.format(BUDGET_HISTORY_FILE_TEMPLATE, newUsername));
        
        if (Files.exists(oldHistoryFile)) {
            Files.move(oldHistoryFile, newHistoryFile);
        }
    }
    
    public void saveBudgetHistory(String username, List<Budget> budgetHistory) throws IOException {
        Files.createDirectories(dataDir);
        Path historyFile = dataDir.resolve(String.format(BUDGET_HISTORY_FILE_TEMPLATE, username));
        
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(historyFile))) {
            oos.writeObject(new ArrayList<>(budgetHistory));
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Budget> loadBudgetHistory(String username) throws IOException {
        Path historyFile = dataDir.resolve(String.format(BUDGET_HISTORY_FILE_TEMPLATE, username));
        
        if (!Files.exists(historyFile)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(historyFile))) {
            return (List<Budget>) ois.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load budget history: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}