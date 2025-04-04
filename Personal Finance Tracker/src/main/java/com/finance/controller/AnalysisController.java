package com.finance.controller;

import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
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

public class AnalysisController implements Initializable {

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
    private LocalDate selectedDate = LocalDate.now();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transactionService = new TransactionService();
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());

        // 初始化模型下拉框
        modelComboBox.setItems(FXCollections.observableArrayList("时间", "金额", "交易类型"));
        modelComboBox.getSelectionModel().selectFirst();

        // 设置日期标签
        dateLabel.setText(selectedDate.format(DATE_FORMATTER));

        // 添加监听器
        modelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateChartData());
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateChartData());

        // 初始化图表
        updateChartData();

        // 添加图表点击事件
        pieChart.setOnMouseClicked(this::handlePieChartClick);
    }

    /**
     * 处理日期选择
     */
    @FXML
    private void handleDateSelection() {
        // 创建日期选择器
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(selectedDate);
        
        // 创建对话框
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("选择日期");
        dialog.setHeaderText("请选择一个日期");
        
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
        });
    }

    /**
     * 处理饼图点击事件
     */
    private void handlePieChartClick(MouseEvent event) {
        for (PieChart.Data data : pieChart.getData()) {
            if (data.getNode().getBoundsInParent().contains(event.getX(), event.getY())) {
                showAlert("已选择: " + data.getName() + " - 数值: " + data.getPieValue());
                break;
            }
        }
    }

    /**
     * 更新图表数据
     */
    private void updateChartData() {
        String selectedModel = modelComboBox.getValue();
        boolean isIncome = incomeRadio.isSelected();
        
        // 根据选择的模型和交易类型更新饼图数据
        ObservableList<PieChart.Data> pieChartData = createPieChartData(selectedModel, isIncome);
        pieChart.setData(pieChartData);
        
        // 设置标题
        pieChart.setTitle((isIncome ? "收入" : "支出") + "分布");
    }

    /**
     * 创建饼图数据
     */
    private ObservableList<PieChart.Data> createPieChartData(String model, boolean isIncome) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        
        // 过滤出相应的交易类型（收入或支出）
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> (isIncome && t.getAmount() > 0) || (!isIncome && t.getAmount() < 0))
                .collect(Collectors.toList());
        
        // 根据模型生成数据
        if (model.equals("时间")) {
            // 按日期分组
            Map<String, Double> groupedData = new HashMap<>();
            for (Transaction t : filteredTransactions) {
                String date = t.getDate().toLocalDate().format(DATE_FORMATTER);
                groupedData.put(date, groupedData.getOrDefault(date, 0.0) + Math.abs(t.getAmount()));
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
} 