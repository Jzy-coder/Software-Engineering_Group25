package com.finance.extracted_logic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class DateRangeProcessor {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static class DateRange {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public DateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        @Override
        public String toString() {
            return startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER);
        }
    }

    public static Optional<DateRange> processDateRange(List<LocalDate> availableDates, 
                                                     LocalDate initialStartDate, 
                                                     LocalDate initialEndDate) {
        if (initialStartDate == null || initialEndDate == null || 
            !availableDates.contains(initialStartDate) || 
            !availableDates.contains(initialEndDate)) {
            return Optional.empty();
        }

        // Validate date order
        if (initialStartDate.isAfter(initialEndDate)) {
            LocalDate temp = initialStartDate;
            initialStartDate = initialEndDate;
            initialEndDate = temp;
        }

        return Optional.of(new DateRange(initialStartDate, initialEndDate));
    }
}