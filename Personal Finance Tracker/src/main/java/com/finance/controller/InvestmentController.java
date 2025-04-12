package com.finance.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;

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

    // Comparison View Controls - Left Side
    @FXML
    private DatePicker leftStartDate;
    @FXML
    private DatePicker leftEndDate;
    @FXML
    private RadioButton leftIncomeRadio;
    @FXML
    private RadioButton leftExpenseRadio;
    @FXML
    private ListView<String> leftCategoryList;
    @FXML
    private PieChart leftPieChart;

    // Comparison View Controls - Right Side
    @FXML
    private DatePicker rightStartDate;
    @FXML
    private DatePicker rightEndDate;
    @FXML
    private RadioButton rightIncomeRadio;
    @FXML
    private RadioButton rightExpenseRadio;
    @FXML
    private ListView<String> rightCategoryList;
    @FXML
    private PieChart rightPieChart;

    // Single Comparison View Controls - Left Side
    @FXML
    private DatePicker singleLeftStartDate;
    @FXML
    private DatePicker singleLeftEndDate;
    @FXML
    private RadioButton singleLeftIncomeRadio;
    @FXML
    private RadioButton singleLeftExpenseRadio;
    @FXML
    private ListView<String> singleLeftCategoryList;
    @FXML
    private PieChart singleLeftPieChart;

    // Single Comparison View Controls - Right Side
    @FXML
    private DatePicker singleRightStartDate;
    @FXML
    private DatePicker singleRightEndDate;
    @FXML
    private RadioButton singleRightIncomeRadio;
    @FXML
    private RadioButton singleRightExpenseRadio;
    @FXML
    private ListView<String> singleRightCategoryList;
    @FXML
    private PieChart singleRightPieChart;

    private final ObservableList<String> incomeCategories = FXCollections.observableArrayList("All", "Salary", "Bonus", "Others");
    private final ObservableList<String> expenseCategories = FXCollections.observableArrayList("All", "Food", "Shopping", "Transportation", "Housing", "Entertainment", "Others");

    // Both view line chart has been removed

    @FXML
    private LineChart<String, Number> singleTendencyLineChart;

    // Both view time period combo box has been removed

    @FXML
    private ComboBox<String> singleTimePeriodComboBox;

    // 饼图样式常量
    private static final String PIE_CHART_STYLE = "-fx-pref-width: 400px; -fx-pref-height: 400px; " +
                                   "-fx-min-width: 400px; -fx-min-height: 400px; " +
                                   "-fx-max-width: 400px; -fx-max-height: 400px; " +
                                   "-fx-padding: 0; -fx-background-insets: 0; " +
                                   "-fx-background-radius: 200px;";
                                   
    // 饼图数据样式
    private static final String PIE_DATA_STYLE = "-fx-pie-label-visible: true; " +
                                  "-fx-label-line-length: 10; " +
                                  "-fx-start-angle: 90; " +
                                  "-fx-clockwise: false;";
                                  
    /**
     * 初始化饼图样式
     */
    private void initializePieChartStyle(PieChart chart) {
        // 先清除现有样式和数据
        chart.setStyle("");
        chart.getData().clear();
        
        // 设置基本样式
        chart.setStyle(PIE_CHART_STYLE + "-fx-font-size: 12px; -fx-padding: 10 0 0 20;");
        chart.setMinSize(400, 400);
        chart.setPrefSize(400, 400);
        chart.setMaxSize(400, 400);
        chart.setLegendVisible(false);
        chart.setLabelsVisible(true);
        chart.setLabelLineLength(10);
        chart.setStartAngle(90);
        chart.setClockwise(false);
        
        // 确保图表不会被自动调整大小
        chart.setMinWidth(400);
        chart.setMinHeight(400);
        chart.setMaxWidth(400);
        chart.setMaxHeight(400);
        
        // 强制应用CSS样式
        chart.applyCss();
        chart.layout();
        
        // 添加强制尺寸监听器
        chart.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() != 300) {
                chart.setPrefWidth(400);
                chart.setMinWidth(400);
                chart.setMaxWidth(400);
                chart.applyCss();
                chart.layout();
            }
        });
        chart.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() != 300) {
                chart.setPrefHeight(400);
                chart.setMinHeight(400);
                chart.setMaxHeight(400);
                chart.applyCss();
                chart.layout();
            }
        });
        
        // 添加数据变化监听器
        chart.getData().addListener((javafx.collections.ListChangeListener.Change<? extends PieChart.Data> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (PieChart.Data data : c.getAddedSubList()) {
                        // 为新添加的数据节点设置样式
                        javafx.application.Platform.runLater(() -> {
                            if (data.getNode() != null) {
                                data.getNode().setStyle(PIE_DATA_STYLE);
                                // 添加鼠标悬停效果
                                data.getNode().setOnMouseEntered(event -> {
                                    data.getNode().setStyle(PIE_DATA_STYLE + "-fx-background-color: derive(-fx-pie-color, -20%);");
                                });
                                data.getNode().setOnMouseExited(event -> {
                                    data.getNode().setStyle(PIE_DATA_STYLE);
                                });
                            }
                        });
                    }
                }
            }
            // 强制更新布局
            chart.applyCss();
            chart.layout();
        });
        
        // 确保样式立即生效
        javafx.application.Platform.runLater(() -> {
            chart.setPrefWidth(300);
            chart.setPrefHeight(300);
            chart.applyCss();
            chart.layout();
        });
    }
    
    private void updatePieChartStyle(PieChart chart) {
        // 先清除现有样式
        chart.setStyle("");
        
        // 保存当前数据
        ObservableList<PieChart.Data> currentData = FXCollections.observableArrayList(chart.getData());
        chart.getData().clear();
        
        // 重新应用样式
        chart.setStyle(PIE_CHART_STYLE);
        chart.setMinSize(400, 400);
        chart.setPrefSize(400, 400);
        chart.setMaxSize(400, 400);
        chart.setLegendVisible(false);
        chart.setLabelsVisible(true);
        chart.setLabelLineLength(10);
        chart.setStartAngle(90);
        chart.setClockwise(false);
        
        // 确保图表不会被自动调整大小
        chart.setMinWidth(400);
        chart.setMinHeight(400);
        chart.setMaxWidth(400);
        chart.setMaxHeight(400);
        
        // 强制应用样式
        chart.applyCss();
        chart.layout();
        
        // 恢复数据并应用样式
        javafx.application.Platform.runLater(() -> {
            // 重新添加数据
            chart.getData().addAll(currentData);
            
            // 为每个数据节点应用样式
            for (PieChart.Data data : chart.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle(PIE_DATA_STYLE);
                }
            }
            
            // 确保尺寸和样式设置生效
            chart.setPrefWidth(400);
            chart.setPrefHeight(400);
            chart.setMinWidth(400);
            chart.setMinHeight(400);
            chart.setMaxWidth(400);
            chart.setMaxHeight(400);
            chart.applyCss();
            chart.layout();
        });
    }

    private void setupEmptyPieChartStyle(PieChart chart) {
        // 确保基础样式已应用
        initializePieChartStyle(chart);
        
        // 清除现有数据
        chart.getData().clear();
        
        // 添加空数据
        PieChart.Data emptyData = new PieChart.Data("No Data", 1);
        
        // 在UI线程中设置空数据样式
        javafx.application.Platform.runLater(() -> {
            // 添加数据
            chart.getData().add(emptyData);
            
            if (emptyData.getNode() != null) {
                // 设置空数据的样式
                emptyData.getNode().setStyle("-fx-pie-color: #E0E0E0; -fx-opacity: 0.7;");
                
                // 添加提示文本
                Tooltip tooltip = new Tooltip("No data available");
                tooltip.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
                Tooltip.install(emptyData.getNode(), tooltip);
                
                // 添加鼠标悬停效果
                emptyData.getNode().setOnMouseEntered(e -> 
                    emptyData.getNode().setStyle("-fx-pie-color: #D3D3D3; -fx-opacity: 0.9;"));
                emptyData.getNode().setOnMouseExited(e -> 
                    emptyData.getNode().setStyle("-fx-pie-color: #E0E0E0; -fx-opacity: 0.7;"));
            }
            
            // 强制应用样式
            chart.applyCss();
            chart.layout();
        });
    }

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
            // 更新饼图数据
            updatePieCharts();
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

    private void initializeComparisonControls() {
        // 初始化类别列表
        initializeCategoryList(leftCategoryList, leftIncomeRadio, leftExpenseRadio);
        initializeCategoryList(rightCategoryList, rightIncomeRadio, rightExpenseRadio);
        initializeCategoryList(singleLeftCategoryList, singleLeftIncomeRadio, singleLeftExpenseRadio);
        initializeCategoryList(singleRightCategoryList, singleRightIncomeRadio, singleRightExpenseRadio);

        // 设置日期选择器的默认值
        setupDatePickers();
        
        // 设置比较视图的监听器
        setupComparisonViewListeners();
        
        // 初始化所有饼图样式
        // 统一初始化所有饼图样式和尺寸
        initializePieChartStyle(leftPieChart);
        initializePieChartStyle(rightPieChart);
        initializePieChartStyle(singleLeftPieChart);
        initializePieChartStyle(singleRightPieChart);
        
        leftPieChart.setMinSize(300, 300);
        leftPieChart.setPrefSize(300, 300);
        leftPieChart.setMaxSize(300, 300);
        
        rightPieChart.setMinSize(300, 300);
        rightPieChart.setPrefSize(300, 300);
        rightPieChart.setMaxSize(300, 300);
        
        singleLeftPieChart.setMinSize(300, 300);
        singleLeftPieChart.setPrefSize(300, 300);
        singleLeftPieChart.setMaxSize(300, 300);
        
        singleRightPieChart.setMinSize(300, 300);
        singleRightPieChart.setPrefSize(300, 300);
        singleRightPieChart.setMaxSize(300, 300);
        
        // 初始化饼图
        javafx.application.Platform.runLater(() -> {
            updatePieCharts();
            
            // 确保饼图大小固定
            leftPieChart.setStyle(PIE_CHART_STYLE);
            rightPieChart.setStyle(PIE_CHART_STYLE);
            singleLeftPieChart.setStyle(PIE_CHART_STYLE);
            singleRightPieChart.setStyle(PIE_CHART_STYLE);
        });
    }

    private void initializeCategoryList(ListView<String> listView, RadioButton incomeRadio, RadioButton expenseRadio) {
        // 设置多选模式
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 添加收入/支出单选按钮的监听器
        incomeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listView.setItems(FXCollections.observableArrayList(incomeCategories));
            }
        });

        expenseRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listView.setItems(FXCollections.observableArrayList(expenseCategories));
            }
        });

        // 设置初始类别列表
        listView.setItems(FXCollections.observableArrayList(incomeCategories));
    }

    private void setupDatePickers() {
        // 设置默认日期范围（例如：最近一个月）
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        leftStartDate.setValue(startDate);
        leftEndDate.setValue(endDate);
        rightStartDate.setValue(startDate);
        rightEndDate.setValue(endDate);
        singleLeftStartDate.setValue(startDate);
        singleLeftEndDate.setValue(endDate);
        singleRightStartDate.setValue(startDate);
        singleRightEndDate.setValue(endDate);
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
    
    /**
     * 设置比较视图的监听器
     */
    private void setupComparisonViewListeners() {
        // 初始化所有饼图样式
        // 统一初始化所有饼图样式和尺寸
        initializePieChartStyle(leftPieChart);
        initializePieChartStyle(rightPieChart);
        initializePieChartStyle(singleLeftPieChart);
        initializePieChartStyle(singleRightPieChart);
        
        leftPieChart.setMinSize(300, 300);
        leftPieChart.setPrefSize(300, 300);
        leftPieChart.setMaxSize(300, 300);
        
        rightPieChart.setMinSize(300, 300);
        rightPieChart.setPrefSize(300, 300);
        rightPieChart.setMaxSize(300, 300);
        
        singleLeftPieChart.setMinSize(300, 300);
        singleLeftPieChart.setPrefSize(300, 300);
        singleLeftPieChart.setMaxSize(300, 300);
        
        singleRightPieChart.setMinSize(300, 300);
        singleRightPieChart.setPrefSize(300, 300);
        singleRightPieChart.setMaxSize(300, 300);
        // 添加日期选择器监听器
        leftStartDate.valueProperty().addListener((observable, oldValue, newValue) -> updateLeftPieChart());
        leftEndDate.valueProperty().addListener((observable, oldValue, newValue) -> updateLeftPieChart());
        rightStartDate.valueProperty().addListener((observable, oldValue, newValue) -> updateRightPieChart());
        rightEndDate.valueProperty().addListener((observable, oldValue, newValue) -> updateRightPieChart());
        singleLeftStartDate.valueProperty().addListener((observable, oldValue, newValue) -> updateSingleLeftPieChart());
        singleLeftEndDate.valueProperty().addListener((observable, oldValue, newValue) -> updateSingleLeftPieChart());
        singleRightStartDate.valueProperty().addListener((observable, oldValue, newValue) -> updateSingleRightPieChart());
        singleRightEndDate.valueProperty().addListener((observable, oldValue, newValue) -> updateSingleRightPieChart());
        
        // 添加单选按钮监听器
        leftIncomeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                leftCategoryList.setItems(FXCollections.observableArrayList(incomeCategories));
                leftCategoryList.getSelectionModel().selectFirst();
                updateLeftPieChart();
            }
        });
        
        leftExpenseRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                leftCategoryList.setItems(FXCollections.observableArrayList(expenseCategories));
                leftCategoryList.getSelectionModel().selectFirst();
                updateLeftPieChart();
            }
        });
        
        rightIncomeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                rightCategoryList.setItems(FXCollections.observableArrayList(incomeCategories));
                rightCategoryList.getSelectionModel().selectFirst();
                updateRightPieChart();
            }
        });
        
        rightExpenseRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                rightCategoryList.setItems(FXCollections.observableArrayList(expenseCategories));
                rightCategoryList.getSelectionModel().selectFirst();
                updateRightPieChart();
            }
        });
        
        singleLeftIncomeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                singleLeftCategoryList.setItems(FXCollections.observableArrayList(incomeCategories));
                singleLeftCategoryList.getSelectionModel().selectFirst();
                updateSingleLeftPieChart();
            }
        });
        
        singleLeftExpenseRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                singleLeftCategoryList.setItems(FXCollections.observableArrayList(expenseCategories));
                singleLeftCategoryList.getSelectionModel().selectFirst();
                updateSingleLeftPieChart();
            }
        });
        
        singleRightIncomeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                singleRightCategoryList.setItems(FXCollections.observableArrayList(incomeCategories));
                singleRightCategoryList.getSelectionModel().selectFirst();
                updateSingleRightPieChart();
            }
        });
        
        singleRightExpenseRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                singleRightCategoryList.setItems(FXCollections.observableArrayList(expenseCategories));
                singleRightCategoryList.getSelectionModel().selectFirst();
                updateSingleRightPieChart();
            }
        });
        
        // 添加类别列表选择监听器
        leftCategoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateLeftPieChart();
            }
        });
        
        rightCategoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateRightPieChart();
            }
        });
        
        singleLeftCategoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateSingleLeftPieChart();
            }
        });
        
        singleRightCategoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateSingleRightPieChart();
            }
        });
    }
    
    /**
     * 更新所有饼图数据
     */
    private void updateLeftPieChart() {
        updateSinglePieChart(leftPieChart, leftStartDate.getValue(), leftEndDate.getValue(), 
                leftIncomeRadio.isSelected(), leftExpenseRadio.isSelected(), leftCategoryList.getSelectionModel().getSelectedItems());
    }
    
    private void updateRightPieChart() {
        updateSinglePieChart(rightPieChart, rightStartDate.getValue(), rightEndDate.getValue(), 
                rightIncomeRadio.isSelected(), rightExpenseRadio.isSelected(), rightCategoryList.getSelectionModel().getSelectedItems());
    }
    
    private void updateSingleLeftPieChart() {
        updateSinglePieChart(singleLeftPieChart, singleLeftStartDate.getValue(), singleLeftEndDate.getValue(), 
                singleLeftIncomeRadio.isSelected(), singleLeftExpenseRadio.isSelected(), singleLeftCategoryList.getSelectionModel().getSelectedItems());
    }
    
    private void updateSingleRightPieChart() {
        updateSinglePieChart(singleRightPieChart, singleRightStartDate.getValue(), singleRightEndDate.getValue(), 
                singleRightIncomeRadio.isSelected(), singleRightExpenseRadio.isSelected(), singleRightCategoryList.getSelectionModel().getSelectedItems());
    }
    
    /**
     * 设置空数据饼图的样式
     * @param pieChart 要设置样式的饼图
     */
    
    
    /**
     * 更新单个饼图的数据和样式
     * 
     * @param pieChart 要更新的饼图控件
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param isIncome 是否为收入类型
     * @param isExpense 是否为支出类型
     * @param selectedCategories 选中的类别列表
     */
    private void updateSinglePieChart(PieChart pieChart, LocalDate startDate, LocalDate endDate,
            boolean isIncome, boolean isExpense, ObservableList<String> selectedCategories) {
        // 参数验证
        if (pieChart == null) {
            return;
        }
        
        // 清除现有数据
        pieChart.getData().clear();
        
        // 检查日期和类别选择是否有效
        if (startDate == null || endDate == null) {
            pieChart.setTitle("No Date Selected");
            pieChart.getData().add(new PieChart.Data("No Data", 1));
            setupEmptyPieChartStyle(pieChart);
            return;
        }
        
        // 确保结束日期不小于开始日期
        if (endDate.isBefore(startDate)) {
            pieChart.setTitle("Invalid Date Range");
            pieChart.getData().add(new PieChart.Data("End date is before start date", 1));
            setupEmptyPieChartStyle(pieChart);
            return;
        }
        
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            pieChart.setTitle(isIncome ? "Income Distribution (No Categories Selected)" : "Expense Distribution (No Categories Selected)");
            pieChart.getData().add(new PieChart.Data("No Categories Selected", 1));
            setupEmptyPieChartStyle(pieChart);
            return;
        }
        
        // 检查是否至少有一个单选按钮被选中
        if (!isIncome && !isExpense) {
            pieChart.setTitle("No Type Selected");
            pieChart.getData().add(new PieChart.Data("Please select Income or Expense", 1));
            setupEmptyPieChartStyle(pieChart);
            return;
        }

        try {
            // 获取并过滤交易数据
            List<Transaction> filteredTransactions = transactionService.getAllTransactions().stream()
                .filter(t -> {
                    try {
                        LocalDate transactionDate = t.getDate().toLocalDate();
                        return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
                    } catch (Exception e) {
                        System.err.println("Error processing transaction date: " + e.getMessage());
                        return false;
                    }
                })
                .filter(t -> {
                    try {
                        if (isIncome) {
                            return t.getCategory() != null && t.getCategory().equals("Income") && 
                                   (selectedCategories.contains("All") || 
                                    (t.getType() != null && selectedCategories.contains(t.getType())));
                        } else if (isExpense) {
                            return t.getCategory() != null && t.getCategory().equals("Expense") && 
                                   (selectedCategories.contains("All") || 
                                    (t.getType() != null && selectedCategories.contains(t.getType())));
                        }
                        return false;
                    } catch (Exception e) {
                        System.err.println("Error processing transaction type: " + e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

            // 按类型分组并计算总额
            Map<String, Double> groupedData = filteredTransactions.stream()
                .collect(Collectors.groupingBy(
                    Transaction::getType,
                    Collectors.summingDouble(t -> Math.abs(t.getAmount()))
                ));

            // 创建饼图数据
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            double totalAmount = groupedData.values().stream().mapToDouble(Double::doubleValue).sum();
            
            for (Map.Entry<String, Double> entry : groupedData.entrySet()) {
                try {
                    double percentage = (entry.getValue() / totalAmount) * 100;
                    String label = String.format("%s (%.2f - %.1f%%)", entry.getKey(), entry.getValue(), percentage);
                    pieChartData.add(new PieChart.Data(label, entry.getValue()));
                } catch (Exception e) {
                    System.err.println("Error formatting pie chart data: " + e.getMessage());
                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            }

            // 如果没有数据，添加"No Data"项
            if (pieChartData.isEmpty()) {
                pieChart.setTitle(isIncome ? "No Income Data" : "No Expense Data");
                pieChartData.add(new PieChart.Data("No Data", 1));
                pieChart.setData(pieChartData);
                setupEmptyPieChartStyle(pieChart);
                return;
            }

            // 先设置样式和尺寸
            pieChart.setStyle(PIE_CHART_STYLE + "-fx-font-size: 12px;");
            pieChart.setMinSize(300, 300);
            pieChart.setPrefSize(300, 300);
            pieChart.setMaxSize(300, 300);
            pieChart.setClockwise(false);
            pieChart.setStartAngle(5);
            pieChart.setLabelsVisible(true);
            pieChart.setLabelLineLength(20);
            pieChart.setLegendVisible(true);
            pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);
            
            // 强制应用样式
            pieChart.applyCss();
            pieChart.layout();
            
            // 设置饼图数据
            pieChart.setData(pieChartData);
            pieChart.setTitle(isIncome ? "Income Distribution" : "Expense Distribution");
            
            // 再次强制应用样式和尺寸
            updatePieChartStyle(pieChart);
            
            // 添加尺寸监听器
            pieChart.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() != 300) {
                    pieChart.setPrefWidth(300);
                    pieChart.setMinWidth(300);
                    pieChart.setMaxWidth(300);
                }
            });
            pieChart.heightProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() != 300) {
                    pieChart.setPrefHeight(300);
                    pieChart.setMinHeight(300);
                    pieChart.setMaxHeight(300);
                }
            });
            
            // 设置动画效果
            pieChart.setAnimated(false); // 先禁用动画
            javafx.application.Platform.runLater(() -> {
                pieChart.setAnimated(true); // 数据加载完成后启用动画
            });

            // 为每个数据项添加点击事件和样式
            pieChartData.forEach(data -> {
                // 使用Platform.runLater确保节点已创建
                javafx.application.Platform.runLater(() -> {
                    Node node = data.getNode();
                    if (node != null) {
                        // 设置基本样式
                        node.setStyle("-fx-border-width: 1; -fx-border-color: white;");

                        // 创建阴影效果
                        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
                        shadow.setColor(javafx.scene.paint.Color.GRAY);
                        shadow.setRadius(5);

                        // 添加鼠标进入效果
                        node.setOnMouseEntered(event -> {
                            node.setEffect(shadow);
                            node.setScaleX(1.1);
                            node.setScaleY(1.1);
                        });

                        // 添加鼠标离开效果
                        node.setOnMouseExited(event -> {
                            node.setEffect(null);
                            node.setScaleX(1);
                            node.setScaleY(1);
                        });

                        // 添加点击效果
                        node.setOnMouseClicked(event -> {
                            try {
                                String tooltipText = data.getName() + "\nAmount: " + String.format("%.2f", data.getPieValue());
                                Tooltip tooltip = new Tooltip(tooltipText);
                                Tooltip.install(node, tooltip);
                                tooltip.show(node, event.getScreenX(), event.getScreenY() + 15);
                                
                                // 3秒后自动隐藏
                                new java.util.Timer().schedule(
                                    new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            javafx.application.Platform.runLater(() -> tooltip.hide());
                                        }
                                    },
                                    3000
                                );
                            } catch (Exception e) {
                                System.err.println("Error showing tooltip: " + e.getMessage());
                                showAlert("Selection: " + data.getName() + " - Amount: " + String.format("%.2f", data.getPieValue()));
                            }
                        });
                    }
                });
            });
        } catch (Exception e) {
            System.err.println("Error updating pie chart: " + e.getMessage());
            e.printStackTrace();
            
            // 出现异常时显示错误信息
            pieChart.setTitle("Error Loading Data");
            pieChart.getData().clear();
            pieChart.getData().add(new PieChart.Data("Error", 1));
            setupEmptyPieChartStyle(pieChart);
        }
        // 这段代码已被移除，使用上面的try-catch块中的代码
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePieCharts() {
        // Update left pie chart
        updateLeftPieChart();
    
        // Update right pie chart
        updateRightPieChart();
    
        // Update single comparison view pie charts
        updateSingleLeftPieChart();
        updateSingleRightPieChart();
    }


        }
      