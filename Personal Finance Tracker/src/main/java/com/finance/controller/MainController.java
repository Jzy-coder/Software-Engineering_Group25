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
 * Controller for the main application interface.
 * Manages the content area where different views (Welcome, Income/Expense, Budget, etc.) are loaded.
 */
public class MainController implements Initializable {
    
    // Store reference to the currently loaded controller
    private Object currentController;

    @FXML
    private StackPane contentArea;
    
    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is automatically called after the fxml file has been loaded.
     *
     * @param location The location used to resolve relative paths for the root object, or
     *                 null if the location is not known.
     * @param resources The resources used to localize the root object, or null if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showWelcomeView();
    }
    
    /**
     * Show Welcome view
     */
    @FXML
    private void showWelcomeView() {
        loadView("/fxml/WelcomeView.fxml");
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
        loadView("/fxml/BudgetView.fxml");
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
        loadView("/fxml/InvestmentView.fxml");
    }
    
    /**
     * Show Settings view
     */
    @FXML
    private void showSettingsView() {
        loadView("/fxml/SettingsView.fxml");
    }

    /**
     * Show AI Chat view
     */
    @FXML
    private void showAIChatView() {
        loadView("/fxml/AIChatView.fxml");
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
            e.printStackTrace(); // Keep printing stack trace to console
            // Show a more detailed alert
            String errorMessage = String.format("Failed to load view: %s\nFXML Path: %s\nException: %s", 
                                              e.getMessage(), fxmlPath, e.getClass().getName());
            showAlert(errorMessage);
        } catch (Exception e) { // Catch other potential exceptions during loading/initialization
            e.printStackTrace();
            String errorMessage = String.format("An unexpected error occurred while loading view: %s\nFXML Path: %s\nException: %s", 
                                              e.getMessage(), fxmlPath, e.getClass().getName());
            showAlert(errorMessage);
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
