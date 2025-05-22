package com.finance.component;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * A custom dialog component for selecting a date range.
 * It allows users to pick a start and end date from a list of available dates.
 */
public class DateRangeSelector extends Dialog<DateRangeSelector.DateRange> {
    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * A simple class to hold the selected start and end dates.
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

        @Override
        public String toString() {
            return startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER);
        }
    }

    /**
     * Constructs a new DateRangeSelector dialog.
     *
     * @param availableDates A list of dates that are enabled for selection.
     * @param initialStartDate The initial date to set for the start date picker.
     * @param initialEndDate The initial date to set for the end date picker.
     */
    public DateRangeSelector(List<LocalDate> availableDates, LocalDate initialStartDate, LocalDate initialEndDate) {
        setTitle("Select Date Range");
        setHeaderText("Please select start and end dates with transaction data");

        // Create date pickers
        startDatePicker = new DatePicker(initialStartDate);
        endDatePicker = new DatePicker(initialEndDate);
        
        // Apply CSS styles
        startDatePicker.getStyleClass().add("date-picker");
        endDatePicker.getStyleClass().add("date-picker");

        // Set date cell factory to only enable dates with transactions
        Callback<DatePicker, DateCell> dayCellFactory = picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !availableDates.contains(date));
            }
        };

        startDatePicker.setDayCellFactory(dayCellFactory);
        endDatePicker.setDayCellFactory(dayCellFactory);

        // Add validation listeners
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && endDatePicker.getValue() != null && newVal.isAfter(endDatePicker.getValue())) {
                endDatePicker.setValue(newVal);
            }
        });

        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && startDatePicker.getValue() != null && newVal.isBefore(startDatePicker.getValue())) {
                startDatePicker.setValue(newVal);
            }
        });

        // Create layout
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Start Date:"),
            startDatePicker,
            new Label("End Date:"),
            endDatePicker
        );
        
        // Apply CSS styles to dialog
        getDialogPane().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        getDialogPane().getStyleClass().add("dialog-pane");

        // Set dialog content
        getDialogPane().setContent(content);

        // Add buttons
        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(selectButtonType, cancelButtonType);

        // Convert result
        setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return new DateRange(startDatePicker.getValue(), endDatePicker.getValue());
            }
            return null;
        });
    }

    /**
     * Displays the DateRangeSelector dialog and waits for the user's input.
     *
     * @param availableDates A list of dates that are enabled for selection.
     * @param initialStartDate The initial date to set for the start date picker.
     * @param initialEndDate The initial date to set for the end date picker.
     * @return An Optional containing the selected DateRange if the user clicks 'Select', otherwise an empty Optional.
     */
    public static Optional<DateRange> show(List<LocalDate> availableDates, LocalDate initialStartDate, LocalDate initialEndDate) {
        DateRangeSelector dialog = new DateRangeSelector(availableDates, initialStartDate, initialEndDate);
        return dialog.showAndWait();
    }
}