package com.finance.dao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.finance.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

/**
 * Transaction Data Access Object, responsible for data persistence operations
 */
public class TransactionDAO {
    private static final String DATA_DIR = "target/data";
    private static final String FILE_NAME_TEMPLATE = "%s_transactions.json";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Gson gson;
    private File dataFile;
    private String currentUsername;
    
    /**
     * Custom TypeAdapter for LocalDateTime to resolve Java module system limitations
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DATE_TIME_FORMATTER));
        }
        
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), DATE_TIME_FORMATTER);
        }
    }
    
    public TransactionDAO() {
        // Create custom TypeAdapter for LocalDateTime
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        
        // Ensure data directory exists
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Get current logged-in user
        this.currentUsername = com.finance.gui.LoginManager.getCurrentUsername();
        initializeDataFile();
    }
    
    /**
     * Initialize user's data file
     */
    private void initializeDataFile() {
        // Create corresponding data file using username
        String fileName = String.format(FILE_NAME_TEMPLATE, currentUsername);
        dataFile = new File(DATA_DIR, fileName);
        
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                // Initialize empty transaction list
                saveToFile(new ArrayList<>());
            } catch (IOException e) {
                System.err.println("Error creating transaction data file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Update current user and switch data file
     */
    public void switchUser(String username, boolean isRename) {
        String oldUsername = this.currentUsername;
        this.currentUsername = username;

        if (isRename && oldUsername != null && !oldUsername.equals(username)) {
            // 用户名重命名场景：重命名数据文件
            String oldFileName = String.format(FILE_NAME_TEMPLATE, oldUsername);
            File oldFile = new File(DATA_DIR, oldFileName);
            
            if (oldFile.exists()) {
                String newFileName = String.format(FILE_NAME_TEMPLATE, username);
                File newFile = new File(DATA_DIR, newFileName);
                
                if (oldFile.renameTo(newFile)) {
                    dataFile = newFile;
                } else {
                    System.err.println("文件名更新失败，保持原有文件");
                    this.currentUsername = oldUsername; // 回滚用户名
                }
            }
        } else {
            // 账户切换场景：直接加载新用户文件
            initializeDataFile();
        }
    }
    
    /**
     * 复制文件
     */
    private void copyFile(File source, File dest) throws IOException {
        try (FileReader fr = new FileReader(source);
             FileWriter fw = new FileWriter(dest)) {
            char[] buffer = new char[1024];
            int length;
            while ((length = fr.read(buffer)) > 0) {
                fw.write(buffer, 0, length);
            }
        }
    }
    
    
    /**
     * 获取所有交易记录
     */
    public List<Transaction> getAllTransactions() {
        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<List<Transaction>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.err.println("读取交易记录错误: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Save new transaction
     */
    public void save(Transaction transaction) {
        List<Transaction> transactions = getAllTransactions();
        long maxId = transactions.stream()
                .filter(t -> t.getId() != null)
                .mapToLong(Transaction::getId)
                .max()
                .orElse(0L);
        transaction.setId(maxId + 1);
        
        transactions.add(transaction);
        try {
            saveToFile(transactions);
        } catch (IOException e) {
            throw new RuntimeException("保存到文件失败：" + e.getMessage(), e);
        }
    }    
    
    /**
    * Update a transaction and persist changes
    */
public void update(Transaction transaction) {
    List<Transaction> transactions = getAllTransactions();
    for (int i = 0; i < transactions.size(); i++) {
        if (transactions.get(i).getId().equals(transaction.getId())) {
            transactions.set(i, transaction);
            break;
        }
    }
    try {
        saveToFile(transactions);
    } catch (IOException e) {
        throw new RuntimeException("更新交易失败：" + e.getMessage(), e);
    }
}

/**
 * Delete transaction by ID
 */
public void deleteById(Long id) {
    List<Transaction> transactions = getAllTransactions();
    transactions.removeIf(t -> t.getId().equals(id));
    try {
        saveToFile(transactions);
    } catch (IOException e) {
        throw new RuntimeException("删除交易失败：" + e.getMessage(), e);
    }
}

/**
 * Batch insert transactions with duplication check
 */
public void batchInsert(List<Transaction> newTransactions) {
    List<Transaction> existing = getAllTransactions();

    // 使用HashSet去重（基于交易日期、类型和金额）
    Set<String> uniqueKeys = existing.stream()
        .map(t -> t.getDate().toString() + t.getType() + t.getAmount())
        .collect(Collectors.toSet());

    List<Transaction> merged = new ArrayList<>(existing);
    merged.addAll(newTransactions.stream()
        .filter(t -> !uniqueKeys.contains(t.getDate().toString() + t.getType() + t.getAmount()))
        .collect(Collectors.toList()));

    try {
        saveToFile(merged);
    } catch (IOException e) {
        throw new RuntimeException("批量添加交易失败：" + e.getMessage(), e);
    }
}
    
    /**
     * Save transaction list to file
     */
    public void saveToFile(List<Transaction> transactions) throws IOException {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            System.err.println("Error saving transactions to file: " + e.getMessage());
        }
    }
    
    /**
     * Clear cache by reloading data from file
     */
    public void clearCache() {
        // Simply reload data from file to clear any in-memory caching
        getAllTransactions();
    }
}