package com.finance.extracted_logic;

import com.finance.model.Transaction;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVDataProcessorTest {
    
    private CSVDataProcessor processor;
    private File testFile;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        processor = new CSVDataProcessor();
        testFile = tempDir.resolve("test.csv").toFile();
    }
    
    @Test
    void shouldParseValidCSV() throws IOException, CsvValidationException {
        // Given
        String csvContent = "Category,Type,Amount,Description,Date\n" +
                "Food,Expense,100.0,Groceries,2023-01-01";
        writeToFile(testFile, csvContent);
        
        // When
        List<Transaction> transactions = processor.parseCSV(testFile);
        
        // Then
        assertEquals(1, transactions.size());
        Transaction t = transactions.get(0);
        assertEquals("Food", t.getCategory());
        assertEquals("Expense", t.getType());
        assertEquals(100.0, t.getAmount());
        assertEquals("Groceries", t.getDescription());
        assertEquals(LocalDate.of(2023, 1, 1), t.getDate().toLocalDate());
    }
    
    @Test
    void shouldThrowExceptionWhenMissingRequiredColumn() throws IOException {
        // Given
        String csvContent = "Category,Type,Amount,Description\n" +
                "Food,Expense,100.0,Groceries";
        writeToFile(testFile, csvContent);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> processor.parseCSV(testFile));
    }
    
    @Test
    void shouldThrowExceptionWhenInvalidDataFormat() throws IOException {
        // Given
        String csvContent = "Category,Type,Amount,Description,Date\n" +
                "Food,Expense,invalid,Groceries,2023-01-01";
        writeToFile(testFile, csvContent);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> processor.parseCSV(testFile));
    }
    
    @Test
    void shouldHandleEmptyFile() throws IOException, CsvValidationException {
        // Given
        writeToFile(testFile, "");
        
        // When
        List<Transaction> transactions = processor.parseCSV(testFile);
        
        // Then
        assertTrue(transactions.isEmpty());
    }
    
    private void writeToFile(File file, String content) throws IOException {
        try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
            writer.write(content);
        }
    }
}