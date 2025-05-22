package com.finance.extracted_logic;

import com.finance.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InvestmentChartDataProcessor {

    /**
     * Calculates the start date based on the end date and a given time period string.
     * @param endDate The end date.
     * @param timePeriod The time period string (e.g., "The recent week", "The recent month").
     * @return The calculated start date.
     */
    public static LocalDate getStartDate(LocalDate endDate, String timePeriod) {
        switch (timePeriod) {
            case "The recent week":
                return endDate.minusWeeks(1);
            case "The recent 15 days":
                return endDate.minusDays(15);
            case "The recent month":
                return endDate.minusMonths(1);
            case "The recent three months":
                return endDate.minusMonths(3);
            case "The recent half-year":
                return endDate.minusMonths(6);
            case "The recent year":
                return endDate.minusYears(1);
            default:
                // Default to one week if timePeriod is unrecognized
                return endDate.minusWeeks(1);
        }
    }

    /**
     * Generates a series name for a chart based on data type and category.
     * @param isIncome True if the data represents income.
     * @param isExpense True if the data represents expense.
     * @param isBalance True if the data represents balance.
     * @param category The specific category (e.g., "Salary", "Food", or "All").
     * @return The generated series name.
     */
    public static String getSeriesName(boolean isIncome, boolean isExpense, boolean isBalance, String category) {
        if (isBalance) {
            return "Balance";
        }
        String type = isIncome ? "Income" : (isExpense ? "Expense" : "");
        if (category == null || category.equals("All")) {
            return "Total " + type;
        }
        return type + " - " + category;
    }

    /**
     * Filters a transaction based on date range, type (income/expense), and category.
     * @param transaction The transaction to filter.
     * @param startDate The start date of the filter range.
     * @param endDate The end date of the filter range.
     * @param isIncome True to filter for income transactions.
     * @param isExpense True to filter for expense transactions.
     * @param category The specific category to filter by (null or "All" for no category filter).
     * @return True if the transaction matches the filter criteria, false otherwise.
     */
    public static boolean filterTransaction(Transaction transaction, LocalDate startDate, LocalDate endDate,
                                          boolean isIncome, boolean isExpense, String category) {
        LocalDate transactionDate = transaction.getDate().toLocalDate();
        boolean dateInRange = !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);

        if (!dateInRange) {
            return false;
        }

        // If isIncome or isExpense is true, we filter by type and category
        if (isIncome || isExpense) {
            String transactionMainCategory = transaction.getCategory(); // This should be "Income" or "Expense"
            boolean mainCategoryMatch = (isIncome && "Income".equals(transactionMainCategory)) ||
                                        (isExpense && "Expense".equals(transactionMainCategory));
            if (!mainCategoryMatch) {
                return false;
            }
            // Filter by specific type (subtype) if category is not "All"
            boolean subCategoryMatch = category == null || category.equals("All") || transaction.getType().equals(category);
            return subCategoryMatch;
        }

        // If neither isIncome nor isExpense is true, it implies balance calculation, so include all transactions in range.
        return true;
    }

    /**
     * Processes transactions to generate data for a line chart (tendency view).
     * @param transactions The list of all transactions.
     * @param startDate The start date for data aggregation.
     * @param endDate The end date for data aggregation.
     * @param timePeriod The time period string to determine aggregation granularity.
     * @param isIncome True if processing income data.
     * @param isExpense True if processing expense data.
     * @param isBalance True if processing balance data.
     * @param category The specific category to filter by.
     * @return A list of XYChart.Data points for the line chart.
     */
    public static List<XYChart.Data<String, Number>> processTendencyChartData(
            List<Transaction> transactions, LocalDate startDate, LocalDate endDate, String timePeriod,
            boolean isIncome, boolean isExpense, boolean isBalance, String category) {

        ObservableList<XYChart.Data<String, Number>> seriesData = FXCollections.observableArrayList();

        if (timePeriod.equals("The recent week") || timePeriod.equals("The recent 15 days") || timePeriod.equals("The recent month")) {
            // Aggregate by day
            Map<LocalDate, Double> dailyAmounts = transactions.stream()
                .filter(t -> filterTransaction(t, startDate, endDate, isIncome, isExpense, category))
                .collect(Collectors.groupingBy(
                    t -> t.getDate().toLocalDate(),
                    Collectors.summingDouble(t -> {
                        if (isBalance) {
                            return "Income".equals(t.getCategory()) ? t.getAmount() : -t.getAmount();
                        } else {
                            return t.getAmount();
                        }
                    })
                ));

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                seriesData.add(new XYChart.Data<>(date.toString(), dailyAmounts.getOrDefault(date, 0.0)));
            }
        } else if (timePeriod.equals("The recent three months") || timePeriod.equals("The recent half-year")) {
            // Aggregate by week
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                LocalDate weekEndDate = currentDate.plusDays(6);
                if (weekEndDate.isAfter(endDate)) {
                    weekEndDate = endDate;
                }
                final LocalDate currentWeekStart = currentDate;
                final LocalDate currentWeekEnd = weekEndDate;

                double weeklyAmount = transactions.stream()
                    .filter(t -> filterTransaction(t, currentWeekStart, currentWeekEnd, isIncome, isExpense, category))
                    .mapToDouble(t -> {
                        if (isBalance) {
                            return "Income".equals(t.getCategory()) ? t.getAmount() : -t.getAmount();
                        } else {
                            return t.getAmount();
                        }
                    })
                    .sum();
                String weekLabel = currentWeekStart.toString() + "\n" + currentWeekEnd.toString();
                seriesData.add(new XYChart.Data<>(weekLabel, weeklyAmount));
                currentDate = weekEndDate.plusDays(1);
            }
        } else if (timePeriod.equals("The recent year")) {
            // Aggregate by month
            Map<String, Double> monthlyAmounts = transactions.stream()
                .filter(t -> filterTransaction(t, startDate, endDate, isIncome, isExpense, category))
                .collect(Collectors.groupingBy(
                    t -> t.getDate().toLocalDate().getYear() + "-" + String.format("%02d", t.getDate().toLocalDate().getMonthValue()),
                    Collectors.summingDouble(t -> {
                        if (isBalance) {
                            return "Income".equals(t.getCategory()) ? t.getAmount() : -t.getAmount();
                        } else {
                            return t.getAmount();
                        }
                    })
                ));

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String monthKey = currentDate.getYear() + "-" + String.format("%02d", currentDate.getMonthValue());
                seriesData.add(new XYChart.Data<>(monthKey, monthlyAmounts.getOrDefault(monthKey, 0.0)));
                currentDate = currentDate.plusMonths(1).withDayOfMonth(1); // Ensure we iterate month by month correctly
                 if (currentDate.isAfter(endDate) && ! (currentDate.getYear() == endDate.getYear() && currentDate.getMonth() == endDate.getMonth())){
                    break; // Avoid processing an extra month if start date was not the 1st
                }
            }
        }
        return seriesData;
    }

    /**
     * Processes transactions to generate data for a pie chart.
     * @param transactions The list of all transactions.
     * @param startDate The start date for data aggregation.
     * @param endDate The end date for data aggregation.
     * @param dataType "Income" or "Expense", indicating the type of data for the pie chart.
     * @return An ObservableList of PieChart.Data for the pie chart.
     */
    public static ObservableList<PieChart.Data> processPieChartData(
            List<Transaction> transactions, LocalDate startDate, LocalDate endDate, String dataType) {

        Map<String, Double> aggregatedData = transactions.stream()
            .filter(t -> t.getDate().toLocalDate().compareTo(startDate) >= 0 &&
                         t.getDate().toLocalDate().compareTo(endDate) <= 0 &&
                         t.getCategory().equals(dataType))
            .collect(Collectors.groupingBy(
                Transaction::getType, // Group by sub-category like "Salary", "Food"
                Collectors.summingDouble(Transaction::getAmount)
            ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        aggregatedData.forEach((type, sum) -> pieChartData.add(new PieChart.Data(type, sum)));

        return pieChartData;
    }
}