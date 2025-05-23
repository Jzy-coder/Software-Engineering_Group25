package com.finance.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Manages the history of imported files to prevent duplicate imports.
 */
public class ImportHistoryManager {
    private static final String HISTORY_FILE = "UserInfo/import_history.json";
    private static Set<String> importedHashes = new HashSet<>();
    private static Gson gson = new Gson();

    static {
        loadHistory();
    }

    /**
     * Loads the import history from the JSON file.
     */
    private static void loadHistory() {
        try (FileReader reader = new FileReader(HISTORY_FILE)) {
            importedHashes = gson.fromJson(reader, new TypeToken<Set<String>>(){}.getType());
        } catch (Exception e) {
            // Ignore exception if file not found or empty
        }
    }

    /**
     * Checks if a file with the given hash has already been imported.
     * @param hash The MD5 hash of the file.
     * @return true if the file has been imported, false otherwise.
     */
    public static boolean isFileImported(String hash) {
        return importedHashes.contains(hash);
    }

    /**
     * Adds a record of an imported file to the history.
     * @param hash The MD5 hash of the imported file.
     * @param filename The name of the imported file.
     */
    public static void addImportRecord(String hash, String filename) {
        importedHashes.add(hash);
        saveHistory();
    }

    /**
     * Saves the current import history to the JSON file.
     */
    private static void saveHistory() {
        try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
            gson.toJson(importedHashes, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}