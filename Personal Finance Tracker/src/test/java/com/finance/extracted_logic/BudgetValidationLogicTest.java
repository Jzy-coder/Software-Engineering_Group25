package com.finance.extracted_logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class BudgetValidationLogicTest {
    
    @Test
    void testValidateInput_validInput() {
        // Test with valid inputs, no exception should be thrown
        assertDoesNotThrow(() -> BudgetValidationLogic.validateInput("Monthly Budget", 1000, 500));
    }
    
    @Test
    void testSerialization_Deserialization() {
        List<String> plans = List.of("Plan 1", "Plan 2");
        assertDoesNotThrow(() -> BudgetValidationLogic.validateSerialization(plans));
    }
    
    @Test
    void testGetSetMethods() {
        String name = "Test Budget";
        double planned = 1000.0;
        double actual = 500.0;
        assertDoesNotThrow(() -> BudgetValidationLogic.validateGetSetMethods(name, planned, actual));
    }

    @Test
    void testValidateInput_emptyName() {
        // Test with empty name
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BudgetValidationLogic.validateInput("", 1000, 500);
        });
        assertEquals("Name cannot be empty", exception.getMessage());
    }

    @Test
    void testValidateInput_nullName() {
        // Test with null name
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BudgetValidationLogic.validateInput(null, 1000, 500);
        });
        assertEquals("Name cannot be empty", exception.getMessage());
    }

    @Test
    void testValidateInput_trimmedEmptyName() {
        // Test with name containing only spaces
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BudgetValidationLogic.validateInput("   ", 1000, 500);
        });
        assertEquals("Name cannot be empty", exception.getMessage());
    }

    @Test
    void testValidateInput_plannedAmountZero() {
        // Test with planned amount as zero
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BudgetValidationLogic.validateInput("Test Budget", 0, 0);
        });
        assertEquals("Planned amount must > 0", exception.getMessage());
    }

    @Test
    void testValidateInput_plannedAmountNegative() {
        // Test with negative planned amount
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BudgetValidationLogic.validateInput("Test Budget", -100, 0);
        });
        assertEquals("Planned amount must > 0", exception.getMessage());
    }

    @Test
    void testValidateInput_actualAmountNegative() {
        // Test with negative actual amount
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BudgetValidationLogic.validateInput("Test Budget", 100, -50);
        });
        assertEquals("Actual amount cannot be negative", exception.getMessage());
    }

    @Test
    void testValidateInput_plannedEqualToActual() {
        // Test with planned amount equal to actual amount
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BudgetValidationLogic.validateInput("Test Budget", 100, 100);
        });
        assertEquals("Planned must > Actual", exception.getMessage());
    }
    
    @Test
    void testPlannedAmountValidation_Positive() {
        assertTrue(BudgetValidationLogic.validatePlannedAmount(1000.0));
    }
    
    @Test
    void testPlannedAmountValidation_Negative() {
        assertFalse(BudgetValidationLogic.validatePlannedAmount(-100.0));
    }
    
    @Test
    void testActualAmountValidation_Valid() {
        assertTrue(BudgetValidationLogic.validateActualAmount(500.0, 1000.0));
    }
    
    @Test
    void testActualAmountValidation_ExceedsPlanned() {
        assertFalse(BudgetValidationLogic.validateActualAmount(1500.0, 1000.0));
    }
    
    @Test
    void testPlansValidation_EmptyList() {
        assertFalse(BudgetValidationLogic.validatePlans(new ArrayList<>()));
    }
    
    @Test
    void testPlansValidation_ValidList() {
        List<String> plans = List.of("Plan 1", "Plan 2");
        assertTrue(BudgetValidationLogic.validatePlans(plans));
    }

    @Test
    void testValidateInput_plannedLessThanActual() {
        // Test with planned amount less than actual amount
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BudgetValidationLogic.validateInput("Test Budget", 100, 150);
        });
        assertEquals("Planned must > Actual", exception.getMessage());
    }

    @Test
    void testValidateInput_validInputWithZeroActual() {
        // Test with valid inputs where actual amount is zero
        assertDoesNotThrow(() -> BudgetValidationLogic.validateInput("Savings Goal", 500, 0));
    }
}