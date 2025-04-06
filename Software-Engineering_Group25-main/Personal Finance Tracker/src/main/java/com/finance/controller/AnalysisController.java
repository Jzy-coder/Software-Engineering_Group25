package com.finance.controller;

import com.finance.event.TransactionEvent;
import com.finance.event.TransactionEventListener;
import com.finance.event.TransactionEventManager;
import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.effect.DropShadow;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;

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
    private ToggleGroup typeGroup;

    private TransactionService transactionService;
    private ObservableList<Transaction> transactions;
    private LocalDate selectedDate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 使用LoginManager中的TransactionService实例，确保用户数据隔离
        transactionService = com.finance.gui.LoginManager.getTransactionService();
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());

        // 初始化模型下拉框
        modelComboBox.setItems(FXCollections.observableArrayList("Transaction Type"));
        modelComboBox.getSelectionModel().selectFirst();

        // 设置初始选择日期为有交易数据的日期中的最新日期，如果没有则使用当前日期
        List<LocalDate> availableDates = getAvailableTransactionDates();
        if (!availableDates.isEmpty()) {
            // 选择最新的日期
            selectedDate = availableDates.get(availableDates.size() - 1);
        } else {
            // 如果没有交易数据，使用当前日期
            selectedDate = LocalDate.now();
        }
        
        // 设置日期标签
        dateLabel.setText(selectedDate.format(DATE_FORMATTER));

        // 添加监听器
        modelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateChartData());
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateChartData());

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
            showAlert("No available transaction dates");
            return;
        }
    
        // 创建开始日期和截止日期选择器
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setValue(selectedDate);
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setValue(selectedDate);
    
        // 设置日期选择器只显示有交易数据的日期
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !availableDates.contains(date));
            }
        });
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !availableDates.contains(date));
            }
        });
    
        // 创建对话框
        Dialog<Map<String, LocalDate>> dialog = new Dialog<>();
        dialog.setTitle("Select Date Range");
        dialog.setHeaderText("Please select a start and end date with transaction data");
    
        // 设置按钮
        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);
    
        // 设置对话框内容
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new Label("Start Date:"), startDatePicker, new Label("End Date:"), endDatePicker);
        dialog.getDialogPane().setContent(vbox);
    
        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                if (startDate != null && endDate != null) {
                    Map<String, LocalDate> result = new HashMap<>();
                    result.put("startDate", startDate);
                    result.put("endDate", endDate);
                    return result;
                }
            }
            return null;
        });
    
        // 显示对话框并处理结果
        Optional<Map<String, LocalDate>> result = dialog.showAndWait();
        result.ifPresent(dateRange -> {
            LocalDate startDate = dateRange.get("startDate");
            LocalDate endDate = dateRange.get("endDate");
            // 更新日期标签
            dateLabel.setText(startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER));
            // 更新图表数据
            updateChartData(startDate, endDate);
            updateChartStyle();
            setupPieChartListeners();
        });
    }

    // 修改 updateChartData 方法以接受日期范围
    private void updateChartData(LocalDate startDate, LocalDate endDate) {
        String selectedModel = modelComboBox.getValue();
        boolean isIncome = incomeRadio.isSelected();
    
        // Update pie chart data based on selected model and transaction type
        ObservableList<PieChart.Data> pieChartData = createPieChartData(selectedModel, isIncome, startDate, endDate);
        pieChart.setData(pieChartData);
        setupPieChartListeners();  // Re-bind listeners
        updateChartStyle();
    }

    // 修改 createPieChartData 方法以接受日期范围
    private ObservableList<PieChart.Data> createPieChartData(String model, boolean isIncome, LocalDate startDate, LocalDate endDate) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
    
        // Filter transactions by type (income or expense) and date range
        List<Transaction> filteredTransactions = transactions.stream()
               .filter(t -> t.getCategory().equals(isIncome ? "Income" : "Expense"))
               .filter(t -> {
                    LocalDate transactionDate = t.getDate().toLocalDate();
                    return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
                })
               .collect(Collectors.toList());
    
        // Generate data based on transaction type
        if (model.equals("Transaction Type")) {
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
        }
    
        // If no data available, add a "No Data" item
        if (data.isEmpty()) {
            data.add(new PieChart.Data("No Data", 1));
        }
    
        return data;
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
        
        // Update pie chart data based on selected model and transaction type
        ObservableList<PieChart.Data> pieChartData = createPieChartData(selectedModel, isIncome);
        pieChart.setData(pieChartData);
        setupPieChartListeners();  // Re-bind listeners
        updateChartStyle();
    }

    /**
     * Create pie chart data
     */
    private ObservableList<PieChart.Data> createPieChartData(String model, boolean isIncome) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        
        // Filter transactions by type (income or expense)
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> t.getCategory().equals(isIncome ? "Income" : "Expense"))
                .filter(t -> t.getDate().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());
        
        // Generate data based on transaction type
        if (model.equals("Transaction Type")) {
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
    @Override
    public void onTransactionChanged(TransactionEvent event) {
        // Reload transaction data
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());
        
        // Update chart
        updateChartData();
        updateChartStyle();
        setupPieChartListeners();
    }
}