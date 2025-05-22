package com.finance.extracted_logic;

import com.finance.model.Transaction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

class InvestmentChartDataProcessorTest {

    @Test
    void testGetStartDate() {
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        assertEquals(LocalDate.of(2023, 12, 24), InvestmentChartDataProcessor.getStartDate(endDate, "The recent week"));
        assertEquals(LocalDate.of(2023, 12, 16), InvestmentChartDataProcessor.getStartDate(endDate, "The recent 15 days"));
        assertEquals(LocalDate.of(2023, 11, 30), InvestmentChartDataProcessor.getStartDate(endDate, "The recent month"));
        assertEquals(LocalDate.of(2023, 9, 30), InvestmentChartDataProcessor.getStartDate(endDate, "The recent three months"));
        assertEquals(LocalDate.of(2023, 6, 30), InvestmentChartDataProcessor.getStartDate(endDate, "The recent half-year"));
        assertEquals(LocalDate.of(2022, 12, 31), InvestmentChartDataProcessor.getStartDate(endDate, "The recent year"));
        assertEquals(LocalDate.of(2023, 12, 24), InvestmentChartDataProcessor.getStartDate(endDate, "Invalid period")); // Test default case
    }

    @Test
    void testGetSeriesName() {
        assertEquals("Balance", InvestmentChartDataProcessor.getSeriesName(false, false, true, "All"));
        assertEquals("Total Income", InvestmentChartDataProcessor.getSeriesName(true, false, false, "All"));
        assertEquals("Income - Salary", InvestmentChartDataProcessor.getSeriesName(true, false, false, "Salary"));
        assertEquals("Total Expense", InvestmentChartDataProcessor.getSeriesName(false, true, false, "All"));
        assertEquals("Expense - Food", InvestmentChartDataProcessor.getSeriesName(false, true, false, "Food"));
        assertEquals("Total Income", InvestmentChartDataProcessor.getSeriesName(true, false, false, null)); // Test null category
    }

    @Test
    void testFilterTransaction() {
        Transaction incomeTransaction = new Transaction("Income", "Salary", 1000.0, "Monthly salary", LocalDateTime.of(2023, 1, 15, 0, 0));
        Transaction expenseTransaction = new Transaction("Expense", "Food", 50.0, "Groceries", LocalDateTime.of(2023, 1, 10, 0, 0));
        Transaction outOfRangeTransaction = new Transaction("Income", "Bonus", 200.0, "Annual bonus", LocalDateTime.of(2023, 2, 1, 0, 0));

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        // Test income filter
        assertTrue(InvestmentChartDataProcessor.filterTransaction(incomeTransaction, startDate, endDate, true, false, "All"));
        assertTrue(InvestmentChartDataProcessor.filterTransaction(incomeTransaction, startDate, endDate, true, false, "Salary"));
        assertFalse(InvestmentChartDataProcessor.filterTransaction(incomeTransaction, startDate, endDate, true, false, "Bonus"));
        assertFalse(InvestmentChartDataProcessor.filterTransaction(expenseTransaction, startDate, endDate, true, false, "All"));

        // Test expense filter
        assertTrue(InvestmentChartDataProcessor.filterTransaction(expenseTransaction, startDate, endDate, false, true, "All"));
        assertTrue(InvestmentChartDataProcessor.filterTransaction(expenseTransaction, startDate, endDate, false, true, "Food"));
        assertFalse(InvestmentChartDataProcessor.filterTransaction(expenseTransaction, startDate, endDate, false, true, "Shopping"));
        assertFalse(InvestmentChartDataProcessor.filterTransaction(incomeTransaction, startDate, endDate, false, true, "All"));

        // Test balance filter (should include all in range)
        assertTrue(InvestmentChartDataProcessor.filterTransaction(incomeTransaction, startDate, endDate, false, false, null));
        assertTrue(InvestmentChartDataProcessor.filterTransaction(expenseTransaction, startDate, endDate, false, false, null));

        // Test date range
        assertFalse(InvestmentChartDataProcessor.filterTransaction(outOfRangeTransaction, startDate, endDate, true, false, "All"));
    }

    @Test
    void testProcessTendencyChartData_Daily() {
        Transaction t1 = new Transaction("Income", "Salary", 100.0, "", LocalDateTime.of(2023, 1, 1, 0, 0));
        Transaction t2 = new Transaction("Income", "Salary", 150.0, "", LocalDateTime.of(2023, 1, 1, 0, 0));
        Transaction t3 = new Transaction("Expense", "Food", 50.0, "", LocalDateTime.of(2023, 1, 2, 0, 0));
        List<Transaction> transactions = Arrays.asList(t1, t2, t3);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 3);

        // Test income
        List<XYChart.Data<String, Number>> incomeData = InvestmentChartDataProcessor.processTendencyChartData(transactions, startDate, endDate, "The recent week", true, false, false, "Salary");
        assertEquals(3, incomeData.size());
        assertEquals(250.0, incomeData.get(0).getYValue().doubleValue()); // Jan 1
        assertEquals(0.0, incomeData.get(1).getYValue().doubleValue());   // Jan 2
        assertEquals(0.0, incomeData.get(2).getYValue().doubleValue());   // Jan 3

        // Test expense
        List<XYChart.Data<String, Number>> expenseData = InvestmentChartDataProcessor.processTendencyChartData(transactions, startDate, endDate, "The recent week", false, true, false, "Food");
        assertEquals(3, expenseData.size());
        assertEquals(0.0, expenseData.get(0).getYValue().doubleValue());   // Jan 1
        assertEquals(50.0, expenseData.get(1).getYValue().doubleValue());  // Jan 2
        assertEquals(0.0, expenseData.get(2).getYValue().doubleValue());   // Jan 3

        // Test balance
        List<XYChart.Data<String, Number>> balanceData = InvestmentChartDataProcessor.processTendencyChartData(transactions, startDate, endDate, "The recent week", false, false, true, null);
        assertEquals(3, balanceData.size());
        assertEquals(250.0, balanceData.get(0).getYValue().doubleValue()); // Jan 1 (100+150)
        assertEquals(-50.0, balanceData.get(1).getYValue().doubleValue());// Jan 2 (-50)
        assertEquals(0.0, balanceData.get(2).getYValue().doubleValue());   // Jan 3
    }

    @Test
    void testProcessTendencyChartData_Weekly() {
        Transaction t1 = new Transaction("Income", "Salary", 700.0, "", LocalDateTime.of(2023, 1, 1, 0, 0)); // Week 1
        Transaction t2 = new Transaction("Expense", "Food", 100.0, "", LocalDateTime.of(2023, 1, 8, 0, 0)); // Week 2
        List<Transaction> transactions = Arrays.asList(t1, t2);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 14);

        List<XYChart.Data<String, Number>> balanceData = InvestmentChartDataProcessor.processTendencyChartData(transactions, startDate, endDate, "The recent three months", false, false, true, null);
        assertEquals(2, balanceData.size());
        assertEquals(700.0, balanceData.get(0).getYValue().doubleValue());
        assertEquals(-100.0, balanceData.get(1).getYValue().doubleValue());
    }

    @Test
    void testProcessTendencyChartData_Monthly() {
        Transaction t1 = new Transaction("Income", "Salary", 3000.0, "", LocalDateTime.of(2023, 1, 15, 0, 0)); // Jan
        Transaction t2 = new Transaction("Expense", "Rent", 1000.0, "", LocalDateTime.of(2023, 2, 5, 0, 0));  // Feb
        List<Transaction> transactions = Arrays.asList(t1, t2);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 2, 28);

        List<XYChart.Data<String, Number>> balanceData = InvestmentChartDataProcessor.processTendencyChartData(transactions, startDate, endDate, "The recent year", false, false, true, null);
        // Depending on how months are keyed, this might be 2 or more if iterating by adding 1 month
        // For this test, assuming it correctly groups by YYYY-MM
        assertTrue(balanceData.size() >= 2);
        // Check first month (Jan)
        XYChart.Data<String, Number> janData = balanceData.stream().filter(d -> d.getXValue().equals("2023-01")).findFirst().orElse(null);
        assertNotNull(janData);
        assertEquals(3000.0, janData.getYValue().doubleValue());
        // Check second month (Feb)
        XYChart.Data<String, Number> febData = balanceData.stream().filter(d -> d.getXValue().equals("2023-02")).findFirst().orElse(null);
        assertNotNull(febData);
        assertEquals(-1000.0, febData.getYValue().doubleValue());
    }


    @Test
    void testProcessPieChartData() {
        Transaction t1 = new Transaction("Income", "Salary", 1000.0, "", LocalDateTime.of(2023, 1, 15, 0, 0));
        Transaction t2 = new Transaction("Income", "Bonus", 200.0, "", LocalDateTime.of(2023, 1, 20, 0, 0));
        Transaction t3 = new Transaction("Income", "Salary", 500.0, "", LocalDateTime.of(2023, 1, 25, 0, 0));
        Transaction t4 = new Transaction("Expense", "Food", 50.0, "", LocalDateTime.of(2023, 1, 10, 0, 0));
        Transaction t5 = new Transaction("Expense", "Shopping", 150.0, "", LocalDateTime.of(2023, 1, 12, 0, 0));
        List<Transaction> transactions = Arrays.asList(t1, t2, t3, t4, t5);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        // Test Income Pie Chart
        ObservableList<PieChart.Data> incomePieData = InvestmentChartDataProcessor.processPieChartData(transactions, startDate, endDate, "Income");
        assertEquals(2, incomePieData.size()); // Salary, Bonus
        assertTrue(incomePieData.stream().anyMatch(d -> d.getName().equals("Salary") && d.getPieValue() == 1500.0));
        assertTrue(incomePieData.stream().anyMatch(d -> d.getName().equals("Bonus") && d.getPieValue() == 200.0));

        // Test Expense Pie Chart
        ObservableList<PieChart.Data> expensePieData = InvestmentChartDataProcessor.processPieChartData(transactions, startDate, endDate, "Expense");
        assertEquals(2, expensePieData.size()); // Food, Shopping
        assertTrue(expensePieData.stream().anyMatch(d -> d.getName().equals("Food") && d.getPieValue() == 50.0));
        assertTrue(expensePieData.stream().anyMatch(d -> d.getName().equals("Shopping") && d.getPieValue() == 150.0));
    }
}