package com.finance.component;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class DateRangeSelector extends Dialog<DateRangeSelector.DateRange> {
    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
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

    public DateRangeSelector(List<LocalDate> availableDates, LocalDate initialStartDate, LocalDate initialEndDate) {
        setTitle("Select Date Range");
        setHeaderText("Please select start and end dates with transaction data");

        // Create date pickers
        startDatePicker = new DatePicker(initialStartDate);
        endDatePicker = new DatePicker(initialEndDate);

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

        // Set dialog content
        getDialogPane().setContent(content);

        // Add buttons
        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        // Convert result
        setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return new DateRange(startDatePicker.getValue(), endDatePicker.getValue());
            }
            return null;
        });
    }

    public static Optional<DateRange> show(List<LocalDate> availableDates, LocalDate initialStartDate, LocalDate initialEndDate) {
        DateRangeSelector dialog = new DateRangeSelector(availableDates, initialStartDate, initialEndDate);
        return dialog.showAndWait();
    }
}