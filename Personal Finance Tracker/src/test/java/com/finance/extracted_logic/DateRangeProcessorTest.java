package com.finance.extracted_logic;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeProcessorTest {
    private static final LocalDate DATE_1 = LocalDate.of(2023, 1, 1);
    private static final LocalDate DATE_2 = LocalDate.of(2023, 1, 2);
    private static final LocalDate DATE_3 = LocalDate.of(2023, 1, 3);
    private static final List<LocalDate> AVAILABLE_DATES = Arrays.asList(DATE_1, DATE_2, DATE_3);

    @Test
    void processDateRange_ValidDates_ReturnsDateRange() {
        Optional<DateRangeProcessor.DateRange> result = 
            DateRangeProcessor.processDateRange(AVAILABLE_DATES, DATE_1, DATE_3);
        
        assertTrue(result.isPresent());
        assertEquals(DATE_1, result.get().getStartDate());
        assertEquals(DATE_3, result.get().getEndDate());
    }

    @Test
    void processDateRange_ReverseDates_ReturnsCorrectOrder() {
        Optional<DateRangeProcessor.DateRange> result = 
            DateRangeProcessor.processDateRange(AVAILABLE_DATES, DATE_3, DATE_1);
        
        assertTrue(result.isPresent());
        assertEquals(DATE_1, result.get().getStartDate());
        assertEquals(DATE_3, result.get().getEndDate());
    }

    @Test
    void processDateRange_InvalidStartDate_ReturnsEmpty() {
        Optional<DateRangeProcessor.DateRange> result = 
            DateRangeProcessor.processDateRange(AVAILABLE_DATES, LocalDate.of(2023, 1, 4), DATE_3);
        
        assertFalse(result.isPresent());
    }

    @Test
    void processDateRange_InvalidEndDate_ReturnsEmpty() {
        Optional<DateRangeProcessor.DateRange> result = 
            DateRangeProcessor.processDateRange(AVAILABLE_DATES, DATE_1, LocalDate.of(2023, 1, 4));
        
        assertFalse(result.isPresent());
    }

    @Test
    void processDateRange_EmptyAvailableDates_ReturnsEmpty() {
        Optional<DateRangeProcessor.DateRange> result = 
            DateRangeProcessor.processDateRange(Collections.emptyList(), DATE_1, DATE_3);
        
        assertFalse(result.isPresent());
    }

    @Test
    void processDateRange_NullDates_ReturnsEmpty() {
        Optional<DateRangeProcessor.DateRange> result = 
            DateRangeProcessor.processDateRange(AVAILABLE_DATES, null, null);
        
        assertFalse(result.isPresent());
    }
}