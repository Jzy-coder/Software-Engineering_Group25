package com.finance.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.finance.controller.AnalysisController;

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
    
    // Store reference to the currently loaded controller
    private Object currentController;

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
        loadView("/fxml/AnalysisView.fxml");
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
        loadView("/fxml/SettingsView.fxml");
    }
    
    /**
     * Load view into content area
     */
    private void loadView(String fxmlPath) {
        try {
            // Clean up current view controller resources (if needed)
            cleanupCurrentController();
            
            // Load new view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Save reference to the current controller
            Object controller = loader.getController();
            if (controller != null) {
                currentController = controller;
            }
            
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
            // Clean up current view controller resources
            cleanupCurrentController();
            
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
     * Clean up resources of the current controller
     */
    private void cleanupCurrentController() {
        if (currentController instanceof AnalysisController) {
            // If current controller is AnalysisController, call its cleanup method
            ((AnalysisController) currentController).cleanup();
        }
        // Clear current controller reference
        currentController = null;
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