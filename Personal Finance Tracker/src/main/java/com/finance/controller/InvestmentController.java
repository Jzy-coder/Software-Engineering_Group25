package com.finance.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import javafx.animation.FadeTransition;
import javafx.util.Duration;
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
        // 初始化比较视图的控件
        initializeComparisonControls();
        viewTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateView(newValue);
            }
        });
        
        // 设置默认选项
        viewTypeComboBox.getSelectionModel().selectFirst();

        // 设置单选按钮监听器
        setupRadioButtonListeners();

        // 设置默认选中的单选按钮
        singleIncomeRadio.setSelected(true);

        // 初始化类别选择框并设置默认项
        singleCategoryComboBox.setItems(incomeCategories);
        singleCategoryComboBox.getSelectionModel().selectFirst();

        // 初始化TransactionService
        transactionService = com.finance.gui.LoginManager.getTransactionService();

        // 设置时间段选项
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

        // 添加监听器
        setupSingleTendencyViewListeners();
        
        // 在所有组件初始化完成后，使用JavaFX的Platform.runLater确保UI线程正确处理图表更新
        javafx.application.Platform.runLater(() -> {
            // 更新图表数据
            updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(),
                singleIncomeRadio.isSelected(), singleExpenseRadio.isSelected(), singleBalanceRadio.isSelected(),
                singleCategoryComboBox.getValue());
            
            // 预加载比较视图数据
            singleComparisonView.applyCss();
            singleComparisonView.layout();
            updateComparisonCharts();
        });
    }
    

    private void updateView(String viewType) {
        // 首先隐藏所有视图
        singleTendencyView.setVisible(false);
        singleComparisonView.setVisible(false);

        // 根据选择显示相应的视图
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
        // 添加时间段选择监听器
        singleTimePeriodComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateTendencyChart(singleTendencyLineChart, newValue, singleIncomeRadio.isSelected(),
                    singleExpenseRadio.isSelected(), singleBalanceRadio.isSelected(), singleCategoryComboBox.getValue());
            }
        });

        // 添加类型选择监听器
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

        // 添加类别选择监听器
        singleCategoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !singleBalanceRadio.isSelected()) {
                updateTendencyChart(singleTendencyLineChart, singleTimePeriodComboBox.getValue(),
                    singleIncomeRadio.isSelected(), singleExpenseRadio.isSelected(), false, newValue);
            }
        });
    }

    private void updateTendencyChart(LineChart<String, Number> chart, String timePeriod, boolean isIncome,
            boolean isExpense, boolean isBalance, String category) {
        // 清除现有数据
        chart.getData().clear();
        
        // 先禁用动画，以便在数据准备好后再启用
        chart.setAnimated(false);

        // 获取日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDate(endDate, timePeriod);

        // 预先设置图表样式
        chart.setCreateSymbols(true); // 显示数据点
        chart.setLegendVisible(true);
        chart.getXAxis().setLabel("Date");
        chart.getYAxis().setLabel("Amount");
        chart.setStyle("-fx-font-size: 12px; -fx-padding: 10 0 0 20;");
        
        // 根据时间段设置标签旋转角度
        if (timePeriod.equals("The recent three months") || timePeriod.equals("The recent half-year")) {
            chart.getXAxis().setTickLabelRotation(-90);
        } else {
            int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
            chart.getXAxis().setTickLabelRotation(daysBetween > 15 ? -45 : 0);
        }

        // 获取交易数据
        List<Transaction> transactions = transactionService.getAllTransactions();

        // 创建数据系列
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(getSeriesName(isIncome, isExpense, isBalance, category));

        // 根据时间段选择不同的数据聚合方式
        if (timePeriod.equals("The recent week") || timePeriod.equals("The recent 15 days") || timePeriod.equals("The recent month")) {
            // 按日显示数据
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
            // 按周聚合数据
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
            // 按月聚合数据
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

        // 添加数据系列到图表
        chart.getData().add(series);
        
        // 设置数据系列的颜色和样式
        String seriesColor = getSeriesColor(isIncome, isExpense, isBalance);
        String lineStyle = "-fx-stroke-width: 2.5px;";
        
        // 应用样式到数据系列
        series.getNode().setStyle(lineStyle + seriesColor);
        
        // 为数据点添加样式和交互效果
        for (XYChart.Data<String, Number> data : series.getData()) {
            // 确保数据点节点已创建
            javafx.application.Platform.runLater(() -> {
                if (data.getNode() != null) {
                    // 设置数据点样式
                    data.getNode().setStyle(seriesColor + "-fx-background-radius: 5px; -fx-padding: 5px;");
                    
                    // 添加鼠标悬停效果
                    javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
                    shadow.setColor(javafx.scene.paint.Color.GRAY);
                    shadow.setRadius(5);
                    
                    // 鼠标进入时添加阴影效果
                    data.getNode().setOnMouseEntered(event -> {
                        data.getNode().setEffect(shadow);
                        data.getNode().setScaleX(1.2);
                        data.getNode().setScaleY(1.2);
                    });
                    
                    // 鼠标离开时移除阴影效果
                    data.getNode().setOnMouseExited(event -> {
                        data.getNode().setEffect(null);
                        data.getNode().setScaleX(1);
                        data.getNode().setScaleY(1);
                    });
                    
                    // 鼠标点击时显示数据值
                    data.getNode().setOnMouseClicked(event -> {
                        String message = String.format("%s: %.2f", data.getXValue(), data.getYValue().doubleValue());
                        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(message);
                        javafx.scene.control.Tooltip.install(data.getNode(), tooltip);
                    });
                }
            });
        }
        
        // 强制布局更新
        chart.layout();
        
        // 启用动画效果
        chart.setAnimated(true);
    }
    
    /**
     * 根据数据类型获取适当的颜色样式
     */
    private String getSeriesColor(boolean isIncome, boolean isExpense, boolean isBalance) {
        if (isBalance) {
            return "-fx-stroke: #8A2BE2; -fx-background-color: #8A2BE2, white;"; // 紫色
        } else if (isIncome) {
            return "-fx-stroke: #2E8B57; -fx-background-color: #2E8B57, white;"; // 绿色
        } else if (isExpense) {
            return "-fx-stroke: #CD5C5C; -fx-background-color: #CD5C5C, white;"; // 红色
        }
        return "-fx-stroke: #1E90FF; -fx-background-color: #1E90FF, white;"; // 默认蓝色
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

        // 如果既不是收入也不是支出，说明是余额模式，返回true以包含所有交易
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
        leftEndDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0 || 
                          date.compareTo(leftStartDatePicker.getValue()) < 0);
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
        
        // Create a copy of current data for smooth transition
        ObservableList<PieChart.Data> oldData = FXCollections.observableArrayList(chart.getData());
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
    
    private String getColorForCategory(String category) {
        // Return different colors for different categories
        switch(category) {
            case "Salary": return "#2E8B57";
            case "Bonus": return "#3CB371";
            case "Food": return "#CD5C5C";
            case "Shopping": return "#DC143C";
            case "Transportation": return "#B22222";
            case "Housing": return "#8B0000";
            case "Entertainment": return "#FF6347";
            default: return "#1E90FF";
        }
    }




    private void setupRadioButtonListeners() {
        // 设置单独视图的单选按钮监听器
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

        // 添加监听器到单选按钮
        singleIncomeRadio.selectedProperty().addListener(singleRadioListener);
        singleExpenseRadio.selectedProperty().addListener(singleRadioListener);
        singleBalanceRadio.selectedProperty().addListener(singleRadioListener);
    }
}
