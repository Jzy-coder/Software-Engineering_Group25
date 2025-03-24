package com.finance.dao;

import com.finance.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易记录数据访问对象，负责数据的持久化操作
 */
public class TransactionDAO {
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "transactions.json";
    private final Gson gson;
    private final File dataFile;
    
    public TransactionDAO() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        
        // 确保数据目录存在
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        dataFile = new File(dir, FILE_NAME);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                // 初始化空的交易列表
                saveToFile(new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 查找所有交易记录
     */
    public List<Transaction> findAll() {
        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<List<Transaction>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 保存新交易记录
     */
    public void save(Transaction transaction) {
        List<Transaction> transactions = findAll();
        // 生成新的ID
        long maxId = transactions.stream()
                .mapToLong(Transaction::getId)
                .max()
                .orElse(0L);
        transaction.setId(maxId + 1);
        
        transactions.add(transaction);
        saveToFile(transactions);
    }
    
    /**
     * 更新交易记录
     */
    public void update(Transaction transaction) {
        List<Transaction> transactions = findAll();
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(transaction.getId())) {
                transactions.set(i, transaction);
                break;
            }
        }
        saveToFile(transactions);
    }
    
    /**
     * 删除交易记录
     */
    public void delete(Long id) {
        List<Transaction> transactions = findAll();
        transactions.removeIf(t -> t.getId().equals(id));
        saveToFile(transactions);
    }
    
    /**
     * 将交易记录列表保存到文件
     */
    private void saveToFile(List<Transaction> transactions) {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}