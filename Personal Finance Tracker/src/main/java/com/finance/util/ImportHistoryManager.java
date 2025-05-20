package com.finance.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ImportHistoryManager {
    private static final String HISTORY_FILE = "UserInfo/import_history.json";
    private static Set<String> importedHashes = new HashSet<>();
    private static Gson gson = new Gson();

    static {
        loadHistory();
    }

    private static void loadHistory() {
        try (FileReader reader = new FileReader(HISTORY_FILE)) {
            importedHashes = gson.fromJson(reader, new TypeToken<Set<String>>(){}.getType());
        } catch (Exception e) {
            // 首次运行时文件不存在是正常情况
        }
    }

    public static boolean isFileImported(String hash) {
        return importedHashes.contains(hash);
    }

    public static void addImportRecord(String hash, String filename) {
        importedHashes.add(hash);
        saveHistory();
    }

    private static void saveHistory() {
        try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
            gson.toJson(importedHashes, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}