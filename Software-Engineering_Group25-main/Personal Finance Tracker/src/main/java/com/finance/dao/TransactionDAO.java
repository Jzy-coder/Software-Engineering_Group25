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
 * 交易记录数据访问对象，负责数据的持久化操作
 */
public class TransactionDAO {
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "transactions.json";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Gson gson;
    private final File dataFile;
    
    /**
     * LocalDateTime的自定义TypeAdapter，解决Java模块系统限制问题
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
        // 创建LocalDateTime的自定义TypeAdapter
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
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
                System.err.println("Error creating transaction data file: " + e.getMessage());
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
            System.err.println("Error reading transactions from file: " + e.getMessage());
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
            System.err.println("Error saving transactions to file: " + e.getMessage());
        }
    }
}