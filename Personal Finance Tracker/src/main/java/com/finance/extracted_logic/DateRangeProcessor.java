package com.finance.extracted_logic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Processes and represents a date range with a start and end date.
 */
public class DateRangeProcessor {

    /**
     * Represents a date range.
     */
    public static class DateRange {
        private final LocalDate startDate;
        private final LocalDate endDate;

        /**
         * Constructs a new DateRange.
         * @param startDate The start date of the range.
         * @param endDate The end date of the range.
         */
        public DateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        /**
         * Gets the start date of the range.
         * @return The start date.
         */
        public LocalDate getStartDate() {
            return startDate;
        }

        /**
         * Gets the end date of the range.
         * @return The end date.
         */
        public LocalDate getEndDate() {
            return endDate;
        }
    }


    /**
     * Processes a list of available dates to find a valid date range.
     * The start and end dates must be present in the available dates.
     * If the start date is after the end date, they will be swapped.
     *
     * @param availableDates The list of available dates.
     * @param startDate      The desired start date.
     * @param endDate        The desired end date.
     * @return An Optional containing the DateRange if both dates are valid and present, otherwise empty.
     */
    public static Optional<DateRange> processDateRange(List<LocalDate> availableDates, LocalDate startDate, LocalDate endDate) {
        if (availableDates == null || availableDates.isEmpty() || startDate == null || endDate == null) {
            return Optional.empty();
        }

        LocalDate effectiveStartDate = startDate;
        LocalDate effectiveEndDate = endDate;

        // Swap dates if start is after end
        if (effectiveStartDate.isAfter(effectiveEndDate)) {
            LocalDate temp = effectiveStartDate;
            effectiveStartDate = effectiveEndDate;
            effectiveEndDate = temp;
        }

        // Check if both dates are in the available dates list
        boolean startDateFound = availableDates.contains(effectiveStartDate);
        boolean endDateFound = availableDates.contains(effectiveEndDate);

        if (startDateFound && endDateFound) {
            return Optional.of(new DateRange(effectiveStartDate, effectiveEndDate));
        } else {
            return Optional.empty();
        }
    }
}