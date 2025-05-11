package com.finance.util;

import com.finance.model.Budget;
import com.finance.gui.LoginManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDataManager {
     private static final String DATA_DIR = "data";
     private static final String BUDGET_FILE = "budget.dat";
    
    
     // ���浥Ԥ�����
    public static void saveBudget(Budget budget) {
    File dataDir = new File(DATA_DIR);
    if (!dataDir.exists()) {
        dataDir.mkdirs();
    }
    File budgetFile = new File(DATA_DIR, BUDGET_FILE);
    System.out.println("���ݴ洢·��: " + budgetFile.getAbsolutePath());
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(budgetFile))) {
        oos.writeObject(budget);
    } catch (IOException e) {
        System.err.println("����ʧ�ܣ�·��: " + budgetFile.getAbsolutePath());
        e.printStackTrace();
    }
}

    // ���ص�Ԥ�����
    public static Budget loadBudget() {
        File budgetFile = new File(DATA_DIR, BUDGET_FILE);
        if (!budgetFile.exists()) {
            return null; // �ļ�������ʱ���� null
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(budgetFile))) {
            return (Budget) ois.readObject(); // ��ȡ���� Budget ����
        } catch (IOException | ClassNotFoundException e) {
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
