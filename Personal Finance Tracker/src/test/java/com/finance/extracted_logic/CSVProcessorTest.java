package com.finance.extracted_logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CSVProcessorTest {
    
    @Test
    void testMapChineseType() {
        assertEquals("Food", CSVProcessor.mapChineseType("餐饮"));
        assertEquals("Shopping", CSVProcessor.mapChineseType("购物"));
        assertEquals("Transportation", CSVProcessor.mapChineseType("交通"));
        assertEquals("Housing", CSVProcessor.mapChineseType("住房"));
        assertEquals("Entertainment", CSVProcessor.mapChineseType("娱乐"));
        assertEquals("Salary", CSVProcessor.mapChineseType("工资"));
        assertEquals("Bonus", CSVProcessor.mapChineseType("奖金"));
        assertEquals("Others", CSVProcessor.mapChineseType("其他"));
    }
    
    @Test
    void testIsValidTransaction() {
        assertTrue(CSVProcessor.isValidTransaction("Income", "Salary"));
        assertTrue(CSVProcessor.isValidTransaction("Expense", "Food"));
        assertFalse(CSVProcessor.isValidTransaction("Income", "Invalid"));
        assertFalse(CSVProcessor.isValidTransaction("Expense", "Invalid"));
    }
    
    @Test
    void testCleanAmountString() {
        assertEquals("100.50", CSVProcessor.cleanAmountString("¥100.50"));
        assertEquals("1000", CSVProcessor.cleanAmountString("1,000"));
        assertEquals("123.45", CSVProcessor.cleanAmountString("123.45元"));
    }
    
    @Test
    void testExtractDateOnly() {
        assertEquals("2023-01-15", CSVProcessor.extractDateOnly("2023-01-15 12:30:45"));
        assertEquals("2023-01", CSVProcessor.extractDateOnly("2023-01"));
    }
    
    @Test
    void testDetermineCategory() {
        assertEquals("Expense", CSVProcessor.determineCategory("/餐饮"));
        assertEquals("Income", CSVProcessor.determineCategory("工资"));
    }
}