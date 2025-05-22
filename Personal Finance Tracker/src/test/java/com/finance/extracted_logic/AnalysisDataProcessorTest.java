package com.finance.extracted_logic;

import com.finance.model.Transaction;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisDataProcessorTest {

    private AnalysisDataProcessor dataProcessor;
    private List<Transaction> sampleTransactions;

    @BeforeEach
    void setUp() {
        dataProcessor = new AnalysisDataProcessor();
        sampleTransactions = new ArrayList<>();
        // Sample data
        sampleTransactions.add(new Transaction("Income", "Salary", 1000.0, "Monthly salary", LocalDateTime.of(2023, 1, 15, 0, 0)));
        sampleTransactions.add(new Transaction("Expense", "Food", 50.0, "Lunch", LocalDateTime.of(2023, 1, 15, 0, 0)));
        sampleTransactions.add(new Transaction("Income", "Bonus", 200.0, "Performance bonus", LocalDateTime.of(2023, 1, 20, 0, 0)));
        sampleTransactions.add(new Transaction("Expense", "Shopping", 120.0, "New clothes", LocalDateTime.of(2023, 1, 20, 0, 0)));
        sampleTransactions.add(new Transaction("Expense", "Food", 30.0, "Dinner", LocalDateTime.of(2023, 1, 22, 0, 0)));
        sampleTransactions.add(new Transaction("Income", "Salary", 1000.0, "Monthly salary", LocalDateTime.of(2023, 2, 15, 0, 0)));
    }

    @Test
    void testCreatePieChartData_Income_SingleDate() {
        LocalDate selectedDate = LocalDate.of(2023, 1, 15);
        ObservableList<PieChart.Data> pieChartData = dataProcessor.createPieChartData(sampleTransactions, true, true, selectedDate, null, null);
        assertEquals(1, pieChartData.size());
        assertEquals("Salary", pieChartData.get(0).getName());
        assertEquals(1000.0, pieChartData.get(0).getPieValue(), 0.01);
    }

    @Test
    void testCreatePieChartData_Expense_SingleDate() {
        LocalDate selectedDate = LocalDate.of(2023, 1, 20);
        ObservableList<PieChart.Data> pieChartData = dataProcessor.createPieChartData(sampleTransactions, false, true, selectedDate, null, null);
        assertEquals(1, pieChartData.size());
        assertEquals("Shopping", pieChartData.get(0).getName());
        assertEquals(120.0, pieChartData.get(0).getPieValue(), 0.01);
    }

    @Test
    void testCreatePieChartData_Income_DateRange() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        ObservableList<PieChart.Data> pieChartData = dataProcessor.createPieChartData(sampleTransactions, true, false, null, startDate, endDate);
        assertEquals(2, pieChartData.size()); // Salary and Bonus
        // Order might vary, so check for presence and values
        assertTrue(pieChartData.stream().anyMatch(d -> d.getName().equals("Salary") && Math.abs(d.getPieValue() - 1000.0) < 0.01));
        assertTrue(pieChartData.stream().anyMatch(d -> d.getName().equals("Bonus") && Math.abs(d.getPieValue() - 200.0) < 0.01));
    }

    @Test
    void testCreatePieChartData_Expense_DateRange() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 21); // Exclude the 22nd
        ObservableList<PieChart.Data> pieChartData = dataProcessor.createPieChartData(sampleTransactions, false, false, null, startDate, endDate);
        assertEquals(2, pieChartData.size()); // Food and Shopping
        assertTrue(pieChartData.stream().anyMatch(d -> d.getName().equals("Food") && Math.abs(d.getPieValue() - 50.0) < 0.01));
        assertTrue(pieChartData.stream().anyMatch(d -> d.getName().equals("Shopping") && Math.abs(d.getPieValue() - 120.0) < 0.01));
    }

    @Test
    void testCreatePieChartData_NoData() {
        LocalDate selectedDate = LocalDate.of(2023, 3, 1); // No transactions on this date
        ObservableList<PieChart.Data> pieChartData = dataProcessor.createPieChartData(sampleTransactions, true, true, selectedDate, null, null);
        assertEquals(1, pieChartData.size());
        assertEquals("No Data", pieChartData.get(0).getName());
        assertEquals(1.0, pieChartData.get(0).getPieValue(), 0.01);
    }

    @Test
    void testCreateBarChartSeries_Income_SingleDate() {
        LocalDate selectedDate = LocalDate.of(2023, 1, 15);
        XYChart.Series<String, Number> barChartSeries = dataProcessor.createBarChartSeries(sampleTransactions, true, true, selectedDate, null, null);
        assertEquals("Income", barChartSeries.getName());
        assertEquals(1, barChartSeries.getData().size());
        assertEquals("Salary", barChartSeries.getData().get(0).getXValue());
        assertEquals(1000.0, barChartSeries.getData().get(0).getYValue().doubleValue(), 0.01);
    }

    @Test
    void testCreateBarChartSeries_Expense_DateRange() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        XYChart.Series<String, Number> barChartSeries = dataProcessor.createBarChartSeries(sampleTransactions, false, false, null, startDate, endDate);
        assertEquals("Expense", barChartSeries.getName());
        assertEquals(2, barChartSeries.getData().size()); // Food and Shopping (Food from 15th and 22nd are grouped)
        // Order might vary
        assertTrue(barChartSeries.getData().stream().anyMatch(d -> d.getXValue().equals("Food") && Math.abs(d.getYValue().doubleValue() - (50.0+30.0)) < 0.01));
        assertTrue(barChartSeries.getData().stream().anyMatch(d -> d.getXValue().equals("Shopping") && Math.abs(d.getYValue().doubleValue() - 120.0) < 0.01));
    }

    @Test
    void testCreateBarChartSeries_NoData() {
        LocalDate selectedDate = LocalDate.of(2023, 3, 1);
        XYChart.Series<String, Number> barChartSeries = dataProcessor.createBarChartSeries(sampleTransactions, true, true, selectedDate, null, null);
        assertEquals("Income", barChartSeries.getName());
        assertTrue(barChartSeries.getData().isEmpty());
    }
}