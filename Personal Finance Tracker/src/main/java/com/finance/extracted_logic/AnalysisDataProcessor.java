package com.finance.extracted_logic;

import com.finance.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalysisDataProcessor {

    /**
     * Filter transactions by type (income or expense) and date.
     * @param transactions List of all transactions.
     * @param isIncome True if filtering for income, false for expense.
     * @param isSingleDate True if filtering by a single date, false for a date range.
     * @param selectedDate The single date to filter by (if isSingleDate is true).
     * @param rangeStartDate The start date of the range to filter by (if isSingleDate is false).
     * @param rangeEndDate The end date of the range to filter by (if isSingleDate is false).
     * @return Filtered list of transactions.
     */
    private List<Transaction> filterTransactions(List<Transaction> transactions, boolean isIncome, boolean isSingleDate, 
                                                 LocalDate selectedDate, LocalDate rangeStartDate, LocalDate rangeEndDate) {
        return transactions.stream()
                .filter(t -> t.getCategory().equals(isIncome ? "Income" : "Expense"))
                .filter(t -> {
                    LocalDate transactionDate = t.getDate().toLocalDate();
                    if (isSingleDate) {
                        return transactionDate.equals(selectedDate);
                    } else {
                        return !transactionDate.isBefore(rangeStartDate) && 
                               !transactionDate.isAfter(rangeEndDate);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Create pie chart data based on filtered transactions.
     * @param transactions List of all transactions.
     * @param isIncome True if creating data for income, false for expense.
     * @param isSingleDate True if using a single date, false for a date range.
     * @param selectedDate The single date for data generation (if isSingleDate is true).
     * @param rangeStartDate The start date of the range for data generation (if isSingleDate is false).
     * @param rangeEndDate The end date of the range for data generation (if isSingleDate is false).
     * @return ObservableList of PieChart.Data.
     */
    public ObservableList<PieChart.Data> createPieChartData(List<Transaction> transactions, boolean isIncome, boolean isSingleDate, 
                                                            LocalDate selectedDate, LocalDate rangeStartDate, LocalDate rangeEndDate) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        
        List<Transaction> filteredTransactions = filterTransactions(transactions, isIncome, isSingleDate, selectedDate, rangeStartDate, rangeEndDate);
        
        // Group by transaction type
        Map<String, Double> groupedData = new HashMap<>();
        for (Transaction t : filteredTransactions) {
            String type = t.getType();
            groupedData.put(type, groupedData.getOrDefault(type, 0.0) + Math.abs(t.getAmount()));
        }
        
        // Convert to pie chart data
        for (Map.Entry<String, Double> entry : groupedData.entrySet()) {
            data.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        
        // If no data available, add a "No Data" item
        if (data.isEmpty()) {
            data.add(new PieChart.Data("No Data", 1));
        }
        
        return data;
    }

    /**
     * Create bar chart data series based on filtered transactions.
     * @param transactions List of all transactions.
     * @param isIncome True if creating data for income, false for expense.
     * @param isSingleDate True if using a single date, false for a date range.
     * @param selectedDate The single date for data generation (if isSingleDate is true).
     * @param rangeStartDate The start date of the range for data generation (if isSingleDate is false).
     * @param rangeEndDate The end date of the range for data generation (if isSingleDate is false).
     * @return XYChart.Series containing bar chart data.
     */
    public XYChart.Series<String, Number> createBarChartSeries(List<Transaction> transactions, boolean isIncome, boolean isSingleDate, 
                                                               LocalDate selectedDate, LocalDate rangeStartDate, LocalDate rangeEndDate) {
        List<Transaction> filteredTransactions = filterTransactions(transactions, isIncome, isSingleDate, selectedDate, rangeStartDate, rangeEndDate);
        
        // Group by type and calculate amount
        Map<String, Double> groupedData = filteredTransactions.stream()
                .collect(Collectors.groupingBy(
                    Transaction::getType,
                    Collectors.summingDouble(t -> Math.abs(t.getAmount()))
                ));
        
        // Create data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(isIncome ? "Income" : "Expense"); // Set series name based on type
        
        // Add data points
        groupedData.forEach((type, amount) -> {
            series.getData().add(new XYChart.Data<>(type, amount));
        });
        
        return series;
    }
}