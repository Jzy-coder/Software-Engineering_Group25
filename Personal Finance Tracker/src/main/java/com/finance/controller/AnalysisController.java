package com.finance.controller;

import com.finance.event.TransactionEvent;
import com.finance.event.TransactionEventListener;
import com.finance.event.TransactionEventManager;
import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import com.finance.component.DateRangeSelector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.DropShadow;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AnalysisController implements Initializable, TransactionEventListener {

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private RadioButton incomeRadio;

    @FXML
    private RadioButton expenseRadio;

    @FXML
    private Button dateButton;

    @FXML
    private Label dateLabel;

    @FXML
    private PieChart pieChart;

    @FXML
    private StackPane chartContainer;

    @FXML
    private ToggleGroup typeGroup;

    @FXML
    private ToggleGroup dateSelectionGroup;

    @FXML
    private RadioButton singleDateRadio;

    @FXML
    private RadioButton dateRangeRadio;

    private TransactionService transactionService;
    private ObservableList<Transaction> transactions;
    private LocalDate selectedDate;
    private LocalDate rangeStartDate;
    private LocalDate rangeEndDate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 使用LoginManager中的TransactionService实例，确保用户数据隔离
        transactionService = com.finance.gui.LoginManager.getTransactionService();
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());

        // 初始化模型下拉框
        modelComboBox.setItems(FXCollections.observableArrayList("Pie Chart", "Bar Chart"));
        modelComboBox.getSelectionModel().selectFirst();

        // 设置初始选择日期为有交易数据的日期中的最新日期，如果没有则使用当前日期
        List<LocalDate> availableDates = getAvailableTransactionDates();
        if (!availableDates.isEmpty()) {
            // 选择最新的日期
            selectedDate = availableDates.get(availableDates.size() - 1);
            rangeStartDate = availableDates.get(0);
            rangeEndDate = availableDates.get(availableDates.size() - 1);
        } else {
            // 如果没有交易数据，使用当前日期
            selectedDate = LocalDate.now();
            rangeStartDate = LocalDate.now();
            rangeEndDate = LocalDate.now();
        }
        
        // 设置日期标签
        updateDateLabel();

        // 添加监听器
        modelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateChartData());
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateChartData());
        dateSelectionGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            dateButton.setText(singleDateRadio.isSelected() ? "Select Date" : "Select Date Range");
            updateDateLabel();
            updateChartData();
        });

        // 初始化图表
        updateChartData();
        updateChartStyle();
        setupPieChartListeners();

        // 初始化饼图样式
        pieChart.setLabelLineLength(20);
        pieChart.setLegendVisible(false);
        
        // 注册为交易事件监听器
        TransactionEventManager.getInstance().addTransactionEventListener(this);
    }
    
    /**
     * 清理资源，取消事件监听器注册
     * 应在控制器不再使用时调用此方法
     */
    public void cleanup() {
        // 取消注册事件监听器
        TransactionEventManager.getInstance().removeTransactionEventListener(this);
    }

    /**
     * 获取所有交易的日期（不重复）
     */
    private List<LocalDate> getAvailableTransactionDates() {
        return transactions.stream()
                .map(t -> t.getDate().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * 处理日期选择
     */
    @FXML
    private void handleDateSelection() {
        // 获取所有有交易数据的日期
        List<LocalDate> availableDates = getAvailableTransactionDates();
        
        if (availableDates.isEmpty()) {
            showAlert("No available transaction data dates");
            return;
        }

        if (singleDateRadio.isSelected()) {
            // 创建日期选择器
            DatePicker datePicker = new DatePicker();
            datePicker.setValue(selectedDate);
            datePicker.getStyleClass().add("date-picker");
            
            // 设置日期选择器只显示有交易数据的日期
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || !availableDates.contains(date));
                }
            });
            
            // 创建对话框
            Dialog<LocalDate> dialog = new Dialog<>();
            dialog.setTitle("Select a date");
            dialog.setHeaderText("Please choose a date with transaction data");
            
            // 设置按钮
            ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(selectButtonType,cancelButtonType);
            
            // 应用CSS样式
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");
            
            // 设置对话框内容
            dialogPane.setContent(datePicker);
            
            // 转换结果
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == selectButtonType) {
                    return datePicker.getValue();
                }
                return null;
            });
            
            // 显示对话框并处理结果
            Optional<LocalDate> result = dialog.showAndWait();
            result.ifPresent(date -> {
                selectedDate = date;
                updateDateLabel();
                updateChartData();
                updateChartStyle();
                setupPieChartListeners();
            });
        } else {
            // 显示日期范围选择器
            Optional<DateRangeSelector.DateRange> result = DateRangeSelector.show(
                availableDates, rangeStartDate, rangeEndDate);
            
            result.ifPresent(dateRange -> {
                rangeStartDate = dateRange.getStartDate();
                rangeEndDate = dateRange.getEndDate();
                updateDateLabel();
                updateChartData();
                updateChartStyle();
                setupPieChartListeners();
            });
        }
    }

    /**
     * 处理饼图点击事件
     */
    private void handlePieChartClick(MouseEvent event) {
        // 改进后的点击处理逻辑
    }

    private void setupPieChartListeners() {
        pieChart.getData().forEach(data -> {
            Node node = data.getNode();
            node.setOnMouseClicked(e -> {
                data.getNode().setEffect(new DropShadow());
                showAlert("Selection: " + data.getName() + " - Amount: " + String.format("%.2f", data.getPieValue()));
            });
            node.setStyle("-fx-border-width: 1; -fx-border-color: white;");
        });
    }

    private void updateChartStyle() {
        pieChart.setLabelsVisible(true);
        pieChart.setLabelLineLength(30);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);
        pieChart.setStyle("-fx-font-size: 12px; -fx-padding: 10 0 0 20;");
        pieChart.setClockwise(false);
        pieChart.setStartAngle(5);
    }

    private void updateChartData() {
        String selectedModel = modelComboBox.getValue();
        boolean isIncome = incomeRadio.isSelected();
        
        // Clear the chart container
        chartContainer.getChildren().clear();
        
        // Create and display the selected chart type
        if (selectedModel.equals("Bar Chart")) {
            BarChart<String, Number> barChart = createBarChart(isIncome);
            chartContainer.getChildren().add(barChart);
        } else {
            // Create and display pie chart
            ObservableList<PieChart.Data> pieChartData = createPieChartData(isIncome);
            pieChart.setData(pieChartData);
            chartContainer.getChildren().add(pieChart);
            setupPieChartListeners();  // Re-bind listeners
            updateChartStyle();
        }
    }

    private void updateDateLabel() {
        if (singleDateRadio.isSelected()) {
            if (selectedDate != null) {
                dateLabel.setText(selectedDate.format(DATE_FORMATTER));
            }
        } else {
            if (rangeStartDate != null && rangeEndDate != null) {
                dateLabel.setText(rangeStartDate.format(DATE_FORMATTER) + " to " + 
                                 rangeEndDate.format(DATE_FORMATTER));
            }
        }
    }

    /**
     * Create pie chart data
     */
    private ObservableList<PieChart.Data> createPieChartData(boolean isIncome) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        
        // Filter transactions by type (income or expense) and date
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> t.getCategory().equals(isIncome ? "Income" : "Expense"))
                .filter(t -> {
                    LocalDate transactionDate = t.getDate().toLocalDate();
                    if (singleDateRadio.isSelected()) {
                        return transactionDate.equals(selectedDate);
                    } else {
                        return !transactionDate.isBefore(rangeStartDate) && 
                               !transactionDate.isAfter(rangeEndDate);
                    }
                })
                .collect(Collectors.toList());
        
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
     * Show alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Implementation of TransactionEventListener interface method
     * Called when transaction data changes
     */
    private BarChart<String, Number> createBarChart(boolean isIncome) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        
        xAxis.setLabel("Type");
        yAxis.setLabel("Amount");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(isIncome ? "Income Distribution" : "Expense Distribution");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(isIncome ? "Income" : "Expense");
        
        // Filter and aggregate data
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> t.getCategory().equals(isIncome ? "Income" : "Expense"))
                .filter(t -> {
                    LocalDate transactionDate = t.getDate().toLocalDate();
                    if (singleDateRadio.isSelected()) {
                        return transactionDate.equals(selectedDate);
                    } else {
                        return !transactionDate.isBefore(rangeStartDate) && 
                               !transactionDate.isAfter(rangeEndDate);
                    }
                })
                .collect(Collectors.toList());
        
        Map<String, Double> groupedData = new HashMap<>();
        for (Transaction t : filteredTransactions) {
            String type = t.getType();
            groupedData.put(type, groupedData.getOrDefault(type, 0.0) + Math.abs(t.getAmount()));
        }
        
        // Add data to series
        for (Map.Entry<String, Double> entry : groupedData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        // If no data available
        if (groupedData.isEmpty()) {
            series.getData().add(new XYChart.Data<>("No Data", 0));
        }
        
        barChart.getData().add(series);
        return barChart;
    }
    
    @Override
    public void onTransactionChanged(TransactionEvent event) {
        // Reload transaction data
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());
        
        // Update chart
        updateChartData();
    }
}