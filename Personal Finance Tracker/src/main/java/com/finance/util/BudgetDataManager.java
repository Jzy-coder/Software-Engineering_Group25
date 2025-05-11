package com.finance.util;

import com.finance.model.Budget;
import com.finance.gui.LoginManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDataManager {
     private static final String DATA_DIR = "data";
     private static final String BUDGET_FILE = "budget.dat";
    
    
     public static void saveBudget(Budget budget) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(new File(DATA_DIR, BUDGET_FILE)))
        ) {
            oos.writeObject(budget);
            System.out.println("Budget saved: " + budget); // 添加日志
        } catch (IOException e) {
            System.err.println("Failed to save budget: " + e.getMessage());
        }
    }

    public static Budget loadBudget() {
        File file = new File(DATA_DIR, BUDGET_FILE);
        if (!file.exists()) return null;
        
        try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(file))
        ) {
            Budget budget = (Budget) ois.readObject();
            System.out.println("Budget loaded: " + budget); // 添加日志
            return budget;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load budget: " + e.getMessage());
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
