package com.finance.controller;

import java.time.LocalDate;

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

public class InvestmentController {
    @FXML
    private ComboBox<String> viewTypeComboBox;
    @FXML
    private StackPane contentPane;
    @FXML
    private VBox bothView;
    @FXML
    private VBox tendencyView;
    @FXML
    private VBox comparisonView;
    @FXML
    private VBox singleTendencyView;
    @FXML
    private VBox singleComparisonView;

    @FXML
    private RadioButton incomeRadio;
    @FXML
    private RadioButton expenseRadio;
    @FXML
    private RadioButton balanceRadio;
    @FXML
    private ComboBox<String> categoryComboBox;

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
        incomeRadio.setSelected(true);
        singleIncomeRadio.setSelected(true);

        // 初始化类别选择框并设置默认项
        categoryComboBox.setItems(incomeCategories);
        categoryComboBox.getSelectionModel().selectFirst();
        singleCategoryComboBox.setItems(incomeCategories);
        singleCategoryComboBox.getSelectionModel().selectFirst();
    }
    

    private void updateView(String viewType) {
        // 首先隐藏所有视图
        bothView.setVisible(false);
        singleTendencyView.setVisible(false);
        singleComparisonView.setVisible(false);

        // 根据选择显示相应的视图
        switch (viewType) {
            case "Tendency":
                singleTendencyView.setVisible(true);
                break;
            case "Comparison":
                singleComparisonView.setVisible(true);
                break;
            case "Both":
                bothView.setVisible(true);
                break;
        }
    }

    private void setupCategoryComboBox(ComboBox<String> comboBox) {
        // 初始化时不添加监听器，避免重复触发
        comboBox.setItems(FXCollections.observableArrayList("All"));
        comboBox.getSelectionModel().selectFirst();
    }

    private void initializeComparisonControls() {
        // 初始化类别列表
        initializeCategoryList(leftCategoryList, leftIncomeRadio, leftExpenseRadio);
        initializeCategoryList(rightCategoryList, rightIncomeRadio, rightExpenseRadio);
        initializeCategoryList(singleLeftCategoryList, singleLeftIncomeRadio, singleLeftExpenseRadio);
        initializeCategoryList(singleRightCategoryList, singleRightIncomeRadio, singleRightExpenseRadio);

        // 设置日期选择器的默认值
        setupDatePickers();
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
        // 设置主视图的单选按钮监听器
        ChangeListener<Boolean> mainRadioListener = (observable, oldValue, newValue) -> {
            if (newValue) {
                ObservableList<String> items = FXCollections.observableArrayList();
                if (incomeRadio.isSelected()) {
                    categoryComboBox.setDisable(false);
                    items.setAll(incomeCategories);
                } else if (expenseRadio.isSelected()) {
                    categoryComboBox.setDisable(false);
                    items.setAll(expenseCategories);
                } else if (balanceRadio.isSelected()) {
                    categoryComboBox.setDisable(true);
                    items.clear();
                }
                categoryComboBox.setItems(items);
                if (!items.isEmpty()) {
                    categoryComboBox.getSelectionModel().selectFirst();
                }
            }
        };

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
        incomeRadio.selectedProperty().addListener(mainRadioListener);
        expenseRadio.selectedProperty().addListener(mainRadioListener);
        balanceRadio.selectedProperty().addListener(mainRadioListener);

        singleIncomeRadio.selectedProperty().addListener(singleRadioListener);
        singleExpenseRadio.selectedProperty().addListener(singleRadioListener);
        singleBalanceRadio.selectedProperty().addListener(singleRadioListener);
    }
}