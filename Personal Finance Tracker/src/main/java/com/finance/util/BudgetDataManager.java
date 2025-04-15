package com.finance.util;

import com.finance.model.Budget;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDataManager {
    private static final String DATA_FILE = "data/budgets.dat";
    
    public static void saveBudgets(List<Budget> budgets) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(budgets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Budget> loadBudgets() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            return (List<Budget>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}