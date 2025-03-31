package com.finance.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;

/**
 * Main Interface Controller
 */
public class MainController implements Initializable {

    @FXML
    private StackPane contentArea;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Default view is empty, user needs to select from sidebar
    }
    
    /**
     * Show Income/Expense view
     */
    @FXML
    private void showIncomeExpenseView() {
        loadView("/fxml/IncomeExpenseView.fxml");
    }
    
    /**
     * Show Budget view
     */
    @FXML
    private void showBudgetView() {
        showUnderDevelopmentMessage("Budget");
    }
    
    /**
     * Show Financial Analysis view
     */
    @FXML
    private void showAnalysisView() {
        showUnderDevelopmentMessage("Financial Analysis");
    }
    
    /**
     * Show Investment Portfolio view
     */
    @FXML
    private void showInvestmentView() {
        showUnderDevelopmentMessage("Investment Portfolio");
    }
    
    /**
     * Show Settings view
     */
    @FXML
    private void showSettingsView() {
        showUnderDevelopmentMessage("Settings");
    }
    
    /**
     * Load view into content area
     */
    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load view: " + e.getMessage());
        }
    }
    
    /**
     * Show under development message for features not yet implemented
     */
    private void showUnderDevelopmentMessage(String feature) {
        try {
            // Create a simple view with a message
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/UnderDevelopmentView.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            // If the view doesn't exist, just show an alert
            showAlert(feature + " feature is under development.");
        }
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}