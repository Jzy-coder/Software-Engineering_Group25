package com.finance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

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
    private void initialize() {
        viewTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateView(newValue);
            }
        });
        
        // 设置默认选项
        viewTypeComboBox.getSelectionModel().selectFirst();
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
}