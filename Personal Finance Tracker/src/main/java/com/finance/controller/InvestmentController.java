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
    private ComboBox<String> comparisonTimePeriodComboBox;
    @FXML
    private Button refreshComparisonBtn;
    @FXML
    private PieChart incomePieChart;
    @FXML
    private PieChart expensePieChart;
    @FXML
    private TableView<Map.Entry<String, Double>> comparisonTable;

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
                updateComparisonCharts();
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
        // 设置日期选择器的默认值
        setupDatePickers();
        
        // 设置刷新按钮点击事件
        refreshComparisonBtn.setOnAction(event -> updateComparisonCharts());
        
        // 设置时间段选择监听器
        comparisonTimePeriodComboBox.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateComparisonCharts();
                }
            });
    }
    
    private void updateComparisonCharts() {
        String timePeriod = comparisonTimePeriodComboBox.getValue();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDate(endDate, timePeriod);
        
        // 获取交易数据
        List<Transaction> transactions = transactionService.getAllTransactions();
        
        // 更新收入饼图
        updatePieChart(incomePieChart, transactions, startDate, endDate, "Income");
        
        // 更新支出饼图
        updatePieChart(expensePieChart, transactions, startDate, endDate, "Expense");
    }
    
    private void updatePieChart(PieChart chart, List<Transaction> transactions,
    LocalDate startDate, LocalDate endDate, String category) {
chart.getData().clear();

// 分组统计金额
Map<String, Double> amountsByType = transactions.stream()
.filter(t -> !t.getDate().toLocalDate().isBefore(startDate)
&& !t.getDate().toLocalDate().isAfter(endDate)
&& t.getCategory().equals(category))
.collect(Collectors.groupingBy(
Transaction::getType,
Collectors.summingDouble(Transaction::getAmount)
));

// 转换为 PieChart.Data，只显示类型，隐藏金额
ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
for (Map.Entry<String, Double> entry : amountsByType.entrySet()) {
PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
pieChartData.add(data);
}

chart.setData(pieChartData);
chart.setTitle(category + " Distribution");

// 图表尺寸锁死
chart.setMinSize(350, 350);
chart.setPrefSize(350, 350);
chart.setMaxSize(350, 350);

// 隐藏默认标签（它们会撑大绘图区）
chart.setLabelsVisible(false);

// 设置统一缩放图形部分
Platform.runLater(() -> {
Node pie = chart.lookup(".chart-pie");
if (pie != null) {
pie.setScaleX(0.9);
pie.setScaleY(0.9);
}

// 为每个扇形添加 Tooltip
for (PieChart.Data data : chart.getData()) {
Node node = data.getNode();
if (node != null) {
String tooltipText = String.format("%s: %.2f", data.getName(), data.getPieValue());
Tooltip tooltip = new Tooltip(tooltipText);
Tooltip.install(node, tooltip);
}
}
});
}



    private void setupDatePickers() {
        // 设置默认日期范围（例如：最近一个月）
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        // 初始化Comparison视图的时间段选择器
        ObservableList<String> timePeriods = FXCollections.observableArrayList(
            "The recent week",
            "The recent 15 days", 
            "The recent month",
            "The recent three months",
            "The recent half-year",
            "The recent year"
        );
        comparisonTimePeriodComboBox.setItems(timePeriods);
        comparisonTimePeriodComboBox.getSelectionModel().selectFirst();
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
