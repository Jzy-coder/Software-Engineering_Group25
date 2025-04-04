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
        transactionService = new TransactionService();
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());

        // 初始化模型下拉框
        modelComboBox.setItems(FXCollections.observableArrayList("时间", "金额", "交易类型"));
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
            showAlert("没有可用的交易数据日期");
            return;
        }
        
        // 创建日期选择器
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(selectedDate);
        
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
        dialog.setTitle("选择日期");
        dialog.setHeaderText("请选择一个有交易数据的日期");
        
        // 设置按钮
        ButtonType selectButtonType = new ButtonType("选择", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);
        
        // 设置对话框内容
        dialog.getDialogPane().setContent(datePicker);
        
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
            dateLabel.setText(selectedDate.format(DATE_FORMATTER));
            updateChartData();
        updateChartStyle();
        setupPieChartListeners();
        });
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
                showAlert("精确选择: " + data.getName() + " - 金额: " + String.format("%.2f", data.getPieValue()));
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
        
        // 根据选择的模型和交易类型更新饼图数据
        ObservableList<PieChart.Data> pieChartData = createPieChartData(selectedModel, isIncome);
        pieChart.setData(pieChartData);
        setupPieChartListeners();  // 新增重新绑定监听器
        updateChartStyle();
    }

    /**
     * 创建饼图数据
     */
    private ObservableList<PieChart.Data> createPieChartData(String model, boolean isIncome) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        
        // 过滤出相应的交易类型（收入或支出）
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> t.getCategory().equals(isIncome ? "Income" : "Expense"))
                .filter(t -> t.getDate().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());
        
        // 根据模型生成数据
        if (model.equals("时间")) {
            // 按小时分组
            Map<String, Double> groupedData = new HashMap<>();
            for (Transaction t : filteredTransactions) {
                String hour = String.format("%02d:00", t.getDate().getHour());
                groupedData.put(hour, groupedData.getOrDefault(hour, 0.0) + Math.abs(t.getAmount()));
            }
            
            // 转换为饼图数据
            for (Map.Entry<String, Double> entry : groupedData.entrySet()) {
                data.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        } else if (model.equals("金额")) {
            // 按金额范围分组
            double[] ranges = {0, 100, 500, 1000, Double.MAX_VALUE};
            String[] rangeNames = {"0-100", "100-500", "500-1000", "1000+"};
            
            Map<String, Double> groupedData = new HashMap<>();
            for (int i = 0; i < rangeNames.length; i++) {
                groupedData.put(rangeNames[i], 0.0);
            }
            
            for (Transaction t : filteredTransactions) {
                double amount = Math.abs(t.getAmount());
                for (int i = 0; i < ranges.length - 1; i++) {
                    if (amount >= ranges[i] && amount < ranges[i + 1]) {
                        groupedData.put(rangeNames[i], groupedData.get(rangeNames[i]) + amount);
                        break;
                    }
                }
            }
            
            // 转换为饼图数据
            for (Map.Entry<String, Double> entry : groupedData.entrySet()) {
                if (entry.getValue() > 0) {
                    data.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            }
        } else if (model.equals("交易类型")) {
            // 按交易类型分组
            Map<String, Double> groupedData = new HashMap<>();
            for (Transaction t : filteredTransactions) {
                String type = t.getType();
                groupedData.put(type, groupedData.getOrDefault(type, 0.0) + Math.abs(t.getAmount()));
            }
            
            // 转换为饼图数据
            for (Map.Entry<String, Double> entry : groupedData.entrySet()) {
                data.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
        
        // 如果没有数据，添加一个"无数据"项
        if (data.isEmpty()) {
            data.add(new PieChart.Data("无数据", 1));
        }
        
        return data;
    }

    /**
     * 显示警告对话框
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("信息");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 实现TransactionEventListener接口的方法
     * 当交易数据发生变化时被调用
     */
    @Override
    public void onTransactionChanged(TransactionEvent event) {
        // 重新加载交易数据
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());
        
        // 更新图表
        updateChartData();
        updateChartStyle();
        setupPieChartListeners();
    }
}