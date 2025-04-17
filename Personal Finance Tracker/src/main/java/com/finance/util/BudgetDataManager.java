package com.finance.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finance.gui.LoginManager;
import com.finance.model.Budget;

public class BudgetDataManager {
    private static final Logger logger = LoggerFactory.getLogger(BudgetDataManager.class);
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME_TEMPLATE = "%s_budgets.dat";
    
    /**
     * 获取当前用户的预算数据文件路径
     */
    private static String getBudgetFilePath() {
        String username = LoginManager.getCurrentUsername();
        return DATA_DIR + File.separator + String.format(FILE_NAME_TEMPLATE, username);
    }
    
    public static void saveBudgets(List<Budget> budgets) {
        // 确保数据目录存在
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String filePath = getBudgetFilePath();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(budgets);
        } catch (IOException e) {
            logger.error("保存预算数据失败: {}", e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Budget> loadBudgets() {
        String filePath = getBudgetFilePath();
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return (List<Budget>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("加载预算数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 当用户重命名时，重命名对应的预算数据文件
     */
    public static void renameUserBudgetFile(String oldUsername, String newUsername) {
        String oldFilePath = DATA_DIR + File.separator + String.format(FILE_NAME_TEMPLATE, oldUsername);
        String newFilePath = DATA_DIR + File.separator + String.format(FILE_NAME_TEMPLATE, newUsername);
        
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) {
            File newFile = new File(newFilePath);
            if (!oldFile.renameTo(newFile)) {
                logger.error("预算文件重命名失败: {} -> {}", oldFilePath, newFilePath);
            }
        }
    }
}