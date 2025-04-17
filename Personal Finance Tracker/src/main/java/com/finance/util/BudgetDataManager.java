package com.finance.util;

import com.finance.model.Budget;
import com.finance.gui.LoginManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDataManager {
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data";
    
    private static String getDataFilePath() {
        String username = LoginManager.getCurrentUsername();
        return DATA_DIR + "/budgets_" + username + ".dat";
    }
    
    public static void saveBudgets(List<Budget> budgets) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(getDataFilePath()))) {
            oos.writeObject(budgets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Budget> loadBudgets() {
        String filePath = getDataFilePath();
        if (!new File(filePath).exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return (List<Budget>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
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