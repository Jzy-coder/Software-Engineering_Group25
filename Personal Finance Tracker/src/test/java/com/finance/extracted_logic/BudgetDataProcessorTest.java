package com.finance.extracted_logic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.finance.model.Budget;

class BudgetDataProcessorTest {
    
    @TempDir
    Path tempDir;
    
    private BudgetDataProcessor processor;
    private Budget testBudget;
    
    @BeforeEach
    void setUp() {
        processor = new BudgetDataProcessor(tempDir.toString());
        testBudget = new Budget("Test Budget", 1000.0, 800.0);
    }
    
    @Test
    void testSaveAndLoadBudget() throws IOException {
        // When
        processor.saveBudget("testUser", testBudget);
        Budget loaded = processor.loadBudget("testUser");
        
        // Then
        assertNotNull(loaded);
        assertEquals(testBudget.getName(), loaded.getName());
        assertEquals(testBudget.getPlannedAmount(), loaded.getPlannedAmount(), 0.001);
    }
    
    @Test
    void testLoadBudgetWhenNotExists() throws IOException {
        // When
        Budget loaded = processor.loadBudget("nonExistingUser");
        
        // Then
        assertNull(loaded);
    }
    
    @Test
    void testAddToAndLoadHistory() throws IOException {
        // Given
        List<Budget> history = new ArrayList<>();
        
        // When
        processor.addBudgetToHistory("testUser", testBudget);
        history = processor.loadBudgetHistory("testUser");
        
        // Then
        assertEquals(1, history.size());
        assertEquals(testBudget.getName(), history.get(0).getName());
    }
    
    @Test
    void testHandleUsernameChange() throws IOException {
        // Given
        processor.saveBudget("oldUser", testBudget);
        
        // When
        processor.handleUsernameChange("oldUser", "newUser");
        
        // Then
        assertNotNull(processor.loadBudget("newUser"));
        assertNull(processor.loadBudget("oldUser"));
    }
}