package com.finance.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Tooltip;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

public class InvestmentController {
    @FXML
    private ComboBox<String> viewTypeComboBox;
    @FXML
    private StackPane contentPane;
    // Both view and its components have been removed
    @FXML
    private VBox singleTendencyView;
    @FXML
    private VBox singleComparisonView;

    // Both view radio buttons and category combo box have been removed

    @FXML
    private RadioButton singleIncomeRadio;
    @FXML
    private RadioButton singleExpenseRadio;
    @FXML
    private RadioButton singleBalanceRadio;
    @FXML
    private ComboBox<String> singleCategoryComboBox;

    // New Comparison View Controls
    @FXML
    private RadioButton comparisonIncomeRadio;
    @FXML
    private RadioButton comparisonExpenseRadio;
    @FXML
    private ToggleGroup comparisonDataTypeToggle;
    @FXML
    private DatePicker leftStartDatePicker;
    @FXML
    private DatePicker leftEndDatePicker;
    @FXML
    private DatePicker rightStartDatePicker;
    @FXML
    private DatePicker rightEndDatePicker;
    @FXML
    private PieChart leftPieChart;
    @FXML
    private PieChart rightPieChart;

    private final ObservableList<String> incomeCategories = FXCollections.observableArrayList("All", "Salary", "Bonus", "Others");
    private final ObservableList<String> expenseCategories = FXCollections.observableArrayList("All", "Food", "Shopping", "Transportation", "Housing", "Entertainment", "Others");

    // Both view line chart has been removed

    @FXML
    private LineChart<String, Number> singleTendencyLineChart;

    // Both view time period combo box has been removed

    @FXML
    private ComboBox<String> singleTimePeriodComboBox;

    private TransactionService transactionService;

    @FXML
    private void initialize() {
        // initializeTendencyView();
        initializeComparisonControls();
        viewTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateView(newValue);
            }
        });
        
        // setupComparisonViewListeners();
        viewTypeComboBox.getSelectionModel().selectFirst();

        // setupComparisonViewListeners();
        setupRadioButtonListeners();

        // setupSingleTendencyViewListeners();
        singleIncomeRadio.setSelected(true);

        // initializeSingleTendencyView();
        singleCategoryComboBox.setItems(incomeCategories);
        singleCategoryComboBox.getSelectionModel().selectFirst();

        // initializeSingleTendencyView();
        transactionService = com.finance.gui.LoginManager.getTransactionService();

        // setupSingleTendencyViewListeners();
        ObservableList<String> timePeriods = FXCollections.observableArrayList(
            "The recent week",
            "The recent 15 days",
            "The recent month",
            "The recent three months",
            "The recent half-year",
            "The recent year"
        );
        singleTimePeriodComboBox.setItems(timePeriods);
        singleTimePeriodComboBox.getSelectionModel().selectFirst();

        
        setupSingleTendencyViewListeners();
        
        // after initializeComparisonControls();
        javafx.application.Platform.runLater(() -> {
            // updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(),
            updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(),
                singleIncomeRadio.isSelected(), singleExpenseRadio.isSelected(), singleBalanceRadio.isSelected(),
                singleCategoryComboBox.getValue());
            
            
            singleComparisonView.applyCss();
            singleComparisonView.layout();
            updateComparisonCharts();
        });
    }
    

    private void updateView(String viewType) {
        
        singleTendencyView.setVisible(false);
        singleComparisonView.setVisible(false);

        
        switch (viewType) {
            case "Tendency":
                singleTendencyView.setVisible(true);
                updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(),
                    singleIncomeRadio.isSelected(), singleExpenseRadio.isSelected(), singleBalanceRadio.isSelected(),
                    singleCategoryComboBox.getValue());
                break;
            case "Comparison":
                singleComparisonView.setVisible(true);
                // Force layout before updating charts to ensure proper rendering
                Platform.runLater(() -> {
                    singleComparisonView.applyCss();
                    singleComparisonView.layout();
                    updateComparisonCharts();
                });
                break;
        }
    }

    // setupTendencyViewListeners method has been removed as it was related to Both view

    private void setupSingleTendencyViewListeners() {
        
        singleTimePeriodComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateTendencyChart(singleTendencyLineChart, newValue, singleIncomeRadio.isSelected(),
                    singleExpenseRadio.isSelected(), singleBalanceRadio.isSelected(), singleCategoryComboBox.getValue());
            }
        });

        
        singleIncomeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                singleCategoryComboBox.setItems(incomeCategories);
                singleCategoryComboBox.getSelectionModel().selectFirst();
                updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(), true,
                    false, false, singleCategoryComboBox.getValue());
            }
        });

        singleExpenseRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                singleCategoryComboBox.setItems(expenseCategories);
                singleCategoryComboBox.getSelectionModel().selectFirst();
                updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(), false,
                    true, false, singleCategoryComboBox.getValue());
            }
        });

        singleBalanceRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                singleCategoryComboBox.setDisable(true);
                updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(), false,
                    false, true, null);
            } else {
                singleCategoryComboBox.setDisable(false);
            }
        });

        
        singleCategoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !singleBalanceRadio.isSelected()) {
                updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(),
                    singleIncomeRadio.isSelected(), singleExpenseRadio.isSelected(), false, newValue);
            }
        });
    }

    private void updateTendencyChart(LineChart<String, Number> chart, String timePeriod, boolean isIncome,
            boolean isExpense, boolean isBalance, String category) {
        
        chart.getData().clear();
        
        
        chart.setAnimated(false);

        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDate(endDate, timePeriod);

        
        chart.setCreateSymbols(true); 
        chart.setLegendVisible(true);
        chart.getXAxis().setLabel("Date");
        chart.getYAxis().setLabel("Amount");
        chart.setStyle("-fx-font-size: 12px; -fx-padding: 10 0 0 20;");
        
       
        if (timePeriod.equals("The recent three months") || timePeriod.equals("The recent half-year")) {
            chart.getXAxis().setTickLabelRotation(-90);
        } else {
            int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
            chart.getXAxis().setTickLabelRotation(daysBetween > 15 ? -45 : 0);
        }

       
        List<Transaction> transactions = transactionService.getAllTransactions();

      
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(getSeriesName(isIncome, isExpense, isBalance, category));

      
        if (timePeriod.equals("The recent week") || timePeriod.equals("The recent 15 days") || timePeriod.equals("The recent month")) {
       
            Map<LocalDate, Double> dailyAmounts = transactions.stream()
                .filter(t -> filterTransaction(t, startDate, endDate, isIncome, isExpense, category))
                .collect(Collectors.groupingBy(
                    t -> t.getDate().toLocalDate(),
                    Collectors.summingDouble(t -> isBalance ? (t.getCategory().equals("Income") ? t.getAmount() : -t.getAmount()) : t.getAmount())
                ));

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                double amount = dailyAmounts.getOrDefault(date, 0.0);
                series.getData().add(new XYChart.Data<>(date.toString(), amount));
            }
        } else if (timePeriod.equals("The recent three months") || timePeriod.equals("The recent half-year")) {
    
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                LocalDate weekEndDate = currentDate.plusDays(6);
                if (weekEndDate.isAfter(endDate)) {
                    weekEndDate = endDate;
                }

                final LocalDate weekStart = currentDate;
                final LocalDate weekEnd = weekEndDate;

                double weeklyAmount = transactions.stream()
                    .filter(t -> filterTransaction(t, weekStart, weekEnd, isIncome, isExpense, category))
                    .mapToDouble(t -> isBalance ? (t.getCategory().equals("Income") ? t.getAmount() : -t.getAmount()) : t.getAmount())
                    .sum();

                String weekLabel = weekStart.toString() + "\n" + weekEnd.toString();
                series.getData().add(new XYChart.Data<>(weekLabel, weeklyAmount));

                currentDate = weekEndDate.plusDays(1);
            }
        } else if (timePeriod.equals("The recent year")) {

            Map<String, Double> monthlyAmounts = transactions.stream()
                .filter(t -> filterTransaction(t, startDate, endDate, isIncome, isExpense, category))
                .collect(Collectors.groupingBy(
                    t -> t.getDate().toLocalDate().getYear() + "-" + String.format("%02d", t.getDate().toLocalDate().getMonthValue()),
                    Collectors.summingDouble(t -> isBalance ? (t.getCategory().equals("Income") ? t.getAmount() : -t.getAmount()) : t.getAmount())
                ));

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String monthKey = currentDate.getYear() + "-" + String.format("%02d", currentDate.getMonthValue());
                double amount = monthlyAmounts.getOrDefault(monthKey, 0.0);
                series.getData().add(new XYChart.Data<>(monthKey, amount));
                currentDate = currentDate.plusMonths(1);
            }
        }

        
        chart.getData().add(series);
        

        String seriesColor = getSeriesColor(isIncome, isExpense, isBalance);
        String lineStyle = "-fx-stroke-width: 2.5px;";
        
    
        series.getNode().setStyle(lineStyle + seriesColor);
        

        for (XYChart.Data<String, Number> data : series.getData()) {

            javafx.application.Platform.runLater(() -> {
                if (data.getNode() != null) {

                    data.getNode().setStyle(seriesColor + "-fx-background-radius: 5px; -fx-padding: 5px;");
                    

                    javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
                    shadow.setColor(javafx.scene.paint.Color.GRAY);
                    shadow.setRadius(5);
                    

                    data.getNode().setOnMouseEntered(event -> {
                        data.getNode().setEffect(shadow);
                        data.getNode().setScaleX(1.2);
                        data.getNode().setScaleY(1.2);
                    });

                    data.getNode().setOnMouseExited(event -> {
                        data.getNode().setEffect(null);
                        data.getNode().setScaleX(1);
                        data.getNode().setScaleY(1);
                    });
                    

                    data.getNode().setOnMouseClicked(event -> {
                        String message = String.format("%s: %.2f", data.getXValue(), data.getYValue().doubleValue());
                        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(message);
                        javafx.scene.control.Tooltip.install(data.getNode(), tooltip);
                    });
                }
            });
        }
        

        chart.layout();
        

        chart.setAnimated(true);
    }
    
    /**
     * aquired
     */
    private String getSeriesColor(boolean isIncome, boolean isExpense, boolean isBalance) {
        if (isBalance) {
            return "-fx-stroke: #8A2BE2; -fx-background-color: #8A2BE2, white;"; 
        } else if (isIncome) {
            return "-fx-stroke: #2E8B57; -fx-background-color: #2E8B57, white;"; 
        } else if (isExpense) {
            return "-fx-stroke: #CD5C5C; -fx-background-color: #CD5C5C, white;"; 
        }
        return "-fx-stroke: #1E90FF; -fx-background-color: #1E90FF, white;"; 
    }

    private LocalDate getStartDate(LocalDate endDate, String timePeriod) {
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
                return endDate.minusWeeks(1);
        }
    }

    private String getSeriesName(boolean isIncome, boolean isExpense, boolean isBalance, String category) {
        if (isBalance) {
            return "Balance";
        } else if (isIncome) {
            return category.equals("All") ? "Total Income" : "Income - " + category;
        } else if (isExpense) {
            return category.equals("All") ? "Total Expense" : "Expense - " + category;
        }
        return "";
    }

    private boolean filterTransaction(Transaction transaction, LocalDate startDate, LocalDate endDate,
            boolean isIncome, boolean isExpense, String category) {
        LocalDate transactionDate = transaction.getDate().toLocalDate();
        boolean dateInRange = !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);

        if (!dateInRange) {
            return false;
        }

        if (isIncome || isExpense) {
            boolean categoryMatch = transaction.getCategory().equals(isIncome ? "Income" : "Expense");
            boolean typeMatch = category.equals("All") || transaction.getType().equals(category);
            return categoryMatch && typeMatch;
        }

        
        return true;
    }

    
    private void updateComparisonCharts() {
        Platform.runLater(() -> {
            boolean showIncome = comparisonIncomeRadio.isSelected();
            LocalDate leftStartDate = leftStartDatePicker.getValue();
            LocalDate leftEndDate = leftEndDatePicker.getValue();
            LocalDate rightStartDate = rightStartDatePicker.getValue();
            LocalDate rightEndDate = rightEndDatePicker.getValue();
            
            List<Transaction> transactions = transactionService.getAllTransactions();
            
            // Update left pie chart
            updatePieChart(leftPieChart, transactions, leftStartDate, leftEndDate, 
                          showIncome ? "Income" : "Expense");
            
            // Update right pie chart
            updatePieChart(rightPieChart, transactions, rightStartDate, rightEndDate,
                          showIncome ? "Income" : "Expense");
            
            // Set chart titles
            leftPieChart.setTitle((showIncome ? "Income" : "Expense") + " - " + 
                leftStartDate + " to " + leftEndDate);
            rightPieChart.setTitle((showIncome ? "Income" : "Expense") + " - " + 
                rightStartDate + " to " + rightEndDate);
        });
    }

    private void initializeComparisonControls() {
        // Set default dates (one month before today to today for both pickers)
        LocalDate now = LocalDate.now();
        LocalDate oneMonthBefore = now.minusMonths(1);
        leftStartDatePicker.setValue(oneMonthBefore);
        leftEndDatePicker.setValue(now);
        rightStartDatePicker.setValue(oneMonthBefore);
        rightEndDatePicker.setValue(now);

        // Initialize pie charts with proper settings
        Platform.runLater(() -> {
            leftPieChart.setLabelsVisible(false);
            leftPieChart.setLegendVisible(false);
            leftPieChart.setMinSize(350, 350);
            leftPieChart.setPrefSize(350, 350);
            leftPieChart.setMaxSize(350, 350);
            
            rightPieChart.setLabelsVisible(false);
            rightPieChart.setLegendVisible(false);
            rightPieChart.setMinSize(350, 350);
            rightPieChart.setPrefSize(350, 350);
            rightPieChart.setMaxSize(350, 350);
            
            // Force initial layout and update
            singleComparisonView.applyCss();
            singleComparisonView.layout();
            updateComparisonCharts();
        });
        
        // Initialize pie charts with proper settings
        leftPieChart.setLabelsVisible(false);
        leftPieChart.setLegendVisible(false);
        leftPieChart.setMinSize(350, 350);
        leftPieChart.setPrefSize(350, 350);
        leftPieChart.setMaxSize(350, 350);
        
        rightPieChart.setLabelsVisible(false);
        rightPieChart.setLegendVisible(false);
        rightPieChart.setMinSize(350, 350);
        rightPieChart.setPrefSize(350, 350);
        rightPieChart.setMaxSize(350, 350);

        // Add date validation to prevent selecting future dates or dates before start date
        leftStartDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0);
            }
        });
        
        leftEndDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0 || 
                          date.compareTo(leftStartDatePicker.getValue()) < 0);
            }
        });
        rightStartDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0);
            }
        });
        
        rightEndDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0 || 
                          date.compareTo(rightStartDatePicker.getValue()) < 0);
            }
        });

        // Also validate when start dates change
        leftStartDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && leftEndDatePicker.getValue() != null && 
                leftEndDatePicker.getValue().compareTo(newVal) < 0) {
                leftEndDatePicker.setValue(newVal);
            }
        });
        rightStartDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && rightEndDatePicker.getValue() != null && 
                rightEndDatePicker.getValue().compareTo(newVal) < 0) {
                rightEndDatePicker.setValue(newVal);
            }
        });
        
        // Setup date picker listeners
        leftStartDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateLeftPieChart();
        });
        leftEndDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateLeftPieChart();
        });
        rightStartDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateRightPieChart();
        });
        rightEndDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateRightPieChart();
        });
        
        // Setup radio button listeners
        comparisonIncomeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) updateComparisonCharts();
        });
        comparisonExpenseRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) updateComparisonCharts();
        });
    }

    private void updateLeftPieChart() {
        Platform.runLater(() -> {
            boolean showIncome = comparisonIncomeRadio.isSelected();
            LocalDate startDate = leftStartDatePicker.getValue();
            LocalDate endDate = leftEndDatePicker.getValue();
            List<Transaction> transactions = transactionService.getAllTransactions();
            updatePieChart(leftPieChart, transactions, startDate, endDate, 
                showIncome ? "Income" : "Expense");
            leftPieChart.setTitle((showIncome ? "Income" : "Expense") + " - " + 
                startDate + " to " + endDate);
        });
    }

    private void updateRightPieChart() {
        Platform.runLater(() -> {
            boolean showIncome = comparisonIncomeRadio.isSelected();
            LocalDate startDate = rightStartDatePicker.getValue();
            LocalDate endDate = rightEndDatePicker.getValue();
            List<Transaction> transactions = transactionService.getAllTransactions();
            updatePieChart(rightPieChart, transactions, startDate, endDate,
                showIncome ? "Income" : "Expense");
            rightPieChart.setTitle((showIncome ? "Income" : "Expense") + " - " + 
                startDate + " to " + endDate);
        });
    }
    
    private void updatePieChart(PieChart chart, List<Transaction> transactions,
            LocalDate startDate, LocalDate endDate, String category) {
        // Keep animations enabled but control the timing
        chart.setAnimated(true);
        
        // Ensure chart is properly initialized
        chart.setLabelsVisible(false);
        chart.setMinSize(350, 350);
        chart.setPrefSize(350, 350);
        chart.setMaxSize(350, 350);
        chart.getData().clear();

        // Filter and group transactions
        Map<String, Double> amountsByType = transactions.stream()
            .filter(t -> !t.getDate().toLocalDate().isBefore(startDate)
                && !t.getDate().toLocalDate().isAfter(endDate)
                && t.getCategory().equals(category))
            .collect(Collectors.groupingBy(
                Transaction::getType,
                Collectors.summingDouble(Transaction::getAmount)
            ));

        // Calculate total amount for percentage calculation
        double totalAmount = amountsByType.values().stream().mapToDouble(Double::doubleValue).sum();

        // Create new pie chart data
        ObservableList<PieChart.Data> newData = FXCollections.observableArrayList();
        
        if (amountsByType.isEmpty()) {
            // Add placeholder data when no transactions exist
            PieChart.Data noData = new PieChart.Data("No Data", 1);
            newData.add(noData);
            chart.setTitle(category + " (No Data)");
        } else {
            // Add actual transaction data
            for (Map.Entry<String, Double> entry : amountsByType.entrySet()) {
                newData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            chart.setTitle(category + " Distribution");
        }

        // Set new data with animation
        chart.setData(newData);
        
        // Set fixed chart size
        chart.setMinSize(350, 350);
        chart.setPrefSize(350, 350);
        chart.setMaxSize(350, 350);
        
        // Hide default labels
        chart.setLabelsVisible(false);

        // Define color palette for pie slices and legend
        String[] colors = {
            "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40",
            "#8A2BE2", "#2E8B57", "#CD5C5C", "#1E90FF"
        };

        Platform.runLater(() -> {
            // Force layout calculation first
            chart.applyCss();
            chart.layout();
            
            // Scale pie chart
            Node pie = chart.lookup(".chart-pie");
            if (pie != null) {
                pie.setScaleX(0.9);
                pie.setScaleY(0.9);
            }

            // Apply consistent colors to pie slices and tooltips
            for (int i = 0; i < chart.getData().size(); i++) {
                PieChart.Data data = chart.getData().get(i);
                Node node = data.getNode();
                if (node != null) {
                    String color = colors[i % colors.length];
                    node.setStyle("-fx-pie-color: " + color + ";");
                    
                    String tooltipText = amountsByType.isEmpty() ? 
                        "No transactions found" :
                        String.format("%s: %.2f", data.getName(), data.getPieValue());
                    Tooltip tooltip = new Tooltip(tooltipText);
                    Tooltip.install(node, tooltip);
                }
            }

            // Create legend container below the chart
            VBox legendContainer = new VBox(5);
            legendContainer.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
            
            // Add legend title
            Label legendTitle = new Label("Detailed Breakdown:");
            legendTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            legendContainer.getChildren().add(legendTitle);
            
            // Add legend items for each category with matching colors
            if (!amountsByType.isEmpty()) {
                int i = 0;
                for (Map.Entry<String, Double> entry : amountsByType.entrySet()) {
                    double percentage = (entry.getValue() / totalAmount) * 100;
                    HBox legendItem = new HBox(10);
                    
                    // Add color indicator with matching pie slice color
                    Rectangle colorRect = new Rectangle(15, 15);
                    String color = colors[i % colors.length];
                    colorRect.setStyle("-fx-fill: " + color + ";");
                    
                    // Add category name and amount
                    Label categoryLabel = new Label(entry.getKey() + ": ");
                    categoryLabel.setStyle("-fx-text-fill: " + color + ";");
                    Label amountLabel = new Label(String.format("¥%.2f (%.1f%%)", entry.getValue(), percentage));
                    
                    legendItem.getChildren().addAll(colorRect, categoryLabel, amountLabel);
                    legendContainer.getChildren().add(legendItem);
                    i++;
                }
            }
            
            // Add legend container to the parent of the chart
            if (chart.getParent() instanceof VBox) {
                VBox parent = (VBox) chart.getParent();
                // Remove existing legend if present
                parent.getChildren().removeIf(node -> node instanceof VBox && node != chart);
                parent.getChildren().add(legendContainer);
            }
        });
    }
    
    private void setupRadioButtonListeners() {
        // setup listeners for single radio buttons
        ChangeListener<Boolean> singleRadioListener = (observable, oldValue, newValue) -> {
            if (newValue) {
                ObservableList<String> items = FXCollections.observableArrayList();
                if (singleIncomeRadio.isSelected()) {
                    singleCategoryComboBox.setDisable(false);
                    items.setAll(incomeCategories);
                } else if (singleExpenseRadio.isSelected()) {
                    singleCategoryComboBox.setDisable(false);
                    items.setAll(expenseCategories);
                } else if (singleBalanceRadio.isSelected()) {
                    singleCategoryComboBox.setDisable(true);
                    items.clear();
                }
                singleCategoryComboBox.setItems(items);
                if (!items.isEmpty()) {
                    singleCategoryComboBox.getSelectionModel().selectFirst();
                }
            }
        };

        // add listeners to single radio buttons
        singleIncomeRadio.selectedProperty().addListener(singleRadioListener);
        singleExpenseRadio.selectedProperty().addListener(singleRadioListener);
        singleBalanceRadio.selectedProperty().addListener(singleRadioListener);
    }
}
