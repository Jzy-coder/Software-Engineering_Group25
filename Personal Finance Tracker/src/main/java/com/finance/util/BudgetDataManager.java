package com.finance.util;

import com.finance.model.Budget;
import com.finance.gui.LoginManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BudgetDataManager {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path BUDGET_FILE = DATA_DIR.resolve("budget.dat");
    
    
    public static void saveBudget(Budget budget) {
        try {
            Files.createDirectories(DATA_DIR); // 自动创建目录
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(BUDGET_FILE))) {
                oos.writeObject(budget);
                System.out.println("Budget saved to: " + BUDGET_FILE.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to save budget: " + e.getMessage());
            e.printStackTrace();
        }
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

}
