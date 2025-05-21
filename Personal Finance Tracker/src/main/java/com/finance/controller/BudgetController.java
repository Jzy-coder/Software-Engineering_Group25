package com.finance.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.finance.model.Budget;
import com.finance.util.BudgetDataManager;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.DialogPane;

public class BudgetController implements Initializable {

    // UI Components
    @FXML private VBox singleBudgetContainer;
    @FXML private ListView<String> planListView;
    @FXML private Label budgetBalanceLabel;
    @FXML private GridPane inputGrid;
    @FXML private TextField budgetNameField;
    @FXML private TextField plannedAmountField;
    @FXML private TextField actualAmountField;

    // Data
    private Budget currentBudget;
    private ObservableList<String> plans = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentBudget = BudgetDataManager.loadBudget();
        if (currentBudget != null) {
            plans.setAll(currentBudget.getPlans()); // 转换为 ObservableList
            planListView.setItems(plans);
            refreshSingleBudgetDisplay();
        }
    }

    //================ Budget Management ================//
    
    @FXML
    private void handleAddBudget() {

        // if there is an existing budget, show a confirmation dialog
        if (currentBudget != null) {
            BudgetDataManager.addBudgetToHistory(currentBudget); // 新增此行
        }

        // if there is an existing budget, show a confirmation dialog
        if (currentBudget != null) {
            double progress = currentBudget.getActualAmount() / currentBudget.getPlannedAmount();
            if (progress < 1.0) {
                // if there is an existing budget, show a confirmation dialog
                plans.clear();
                currentBudget.setPlans(plans);
                BudgetDataManager.saveBudget(currentBudget);
                planListView.setItems(plans);
            }
        }

        // create a new budget
        Dialog<ButtonType> dialog = new Dialog<>(); 
        dialog.setTitle("Add Budget");
        dialog.setHeaderText("Enter budget details");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("confirmation-dialog"); 
    
        // set up input fields
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);
    
        // create input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
    
        TextField nameField = new TextField();
        TextField plannedField = new TextField();
        TextField actualField = new TextField();
    
        grid.addRow(0, new Label("Budget Name:"), nameField);
        grid.addRow(1, new Label("Planned Amount:"), plannedField);
        grid.addRow(2, new Label("Actual Amount:"), actualField);
    
        dialog.getDialogPane().setContent(grid);
    
        // handle OK button click
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            try {
                String name = nameField.getText().trim();
                String plannedText = plannedField.getText().trim();
                String actualText = actualField.getText().trim();

                
                if (!plannedText.matches("^\\d*\\.?\\d+$")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Planned amount must be a valid number!", "error-alert");
                    return;
                }
                if (!actualText.matches("^\\d*\\.?\\d+$")) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Actual amount must be a valid number!", "error-alert");
                    return;
                }

                double planned = Double.parseDouble(plannedText);
                double actual = Double.parseDouble(actualText);
    
                
                if (name.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Budget name cannot be empty!", "error-alert");
                    return;
                }
                if (planned <= 0 || actual < 0 || planned < actual) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid amounts. Ensure:\n- Planned > 0\n- Actual ≥ 0\n- Planned >= Actual", "error-alert");
                    return;
                }
    
                
                currentBudget = new Budget(name, planned, actual);
                BudgetDataManager.saveBudget(currentBudget);
    
                
                refreshSingleBudgetDisplay();
                updateBudgetBalance();
    
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numbers!", "error-alert");
            }
        }
    }

    
    @FXML
    private void handleEditBudget() {
        if (currentBudget != null) {
            showBudgetDialog("Edit Budget", 
                currentBudget.getName(), 
                currentBudget.getPlannedAmount(), 
                currentBudget.getActualAmount(),
                "confirmation-dialog" 
            );
        }
    }
    
    @FXML
    private void handleReviewBudget() {
        List<Budget> budgetHistory = BudgetDataManager.loadBudgetHistory();
        
         // new instance of the Budget class
        if (currentBudget != null) {
            budgetHistory.removeIf(b -> 
                b.getName().equals(currentBudget.getName()) &&
                Math.abs(b.getPlannedAmount() - currentBudget.getPlannedAmount()) < 0.01 &&
                Math.abs(b.getActualAmount() - currentBudget.getActualAmount()) < 0.01
            );
        }
    
        if (budgetHistory.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Information", "No budget history found.", "info-alert");
            return;
        }
        
        // remove duplicates
        for (int i = budgetHistory.size() - 1; i >= 0; i--) {
            Budget current = budgetHistory.get(i);
            for (int j = i - 1; j >= 0; j--) {
                Budget compare = budgetHistory.get(j);
                if (current.getName().equals(compare.getName()) &&
                    Math.abs(current.getPlannedAmount() - compare.getPlannedAmount()) < 0.01 &&
                    Math.abs(current.getActualAmount() - compare.getActualAmount()) < 0.01) {
                    budgetHistory.remove(j);
                    i--;
                }
            }
        }
        
        // Create dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Budget History Review");
        dialog.setHeaderText("Your Budget History");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("review-dialog"); 
        
        // use a scroll pane for the content
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        // Create content container
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        
        // Add each budget to the dialog - using read-only version for history items
        for (Budget budget : budgetHistory) {
            HBox budgetItem = createReadOnlyBudgetItem(
                budget.getName(),
                budget.getPlannedAmount(),
                budget.getActualAmount()
            );
            contentBox.getChildren().add(budgetItem);
        }
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.showAndWait();
    }

    @FXML
    private void handleRemoveBudget() {

            if (currentBudget != null) {
            
            BudgetDataManager.addBudgetToHistory(currentBudget); 

            
            BudgetDataManager.saveBudget(null);
            currentBudget = null;
            
            plans.clear();
            planListView.setItems(plans); 
            refreshSingleBudgetDisplay();
            updateBudgetBalance(); 
            showAlert(Alert.AlertType.INFORMATION, "Success", "Budget removed successfully", "success-alert");
        }
    }

    //================ Plan Management ================//
        @FXML
    private void handleAddPlan() {
        // check if a budget exists
        if (currentBudget == null) {
            showAlert(Alert.AlertType.WARNING, "No Budget", "Please create a budget first before adding plans.", "warning-alert");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Plan");
        dialog.setHeaderText("Enter plan description:");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("input-dialog");
        
        // set up input fields
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        // add input fields
        Optional<String> result = dialog.showAndWait();

        // check if user clicked OK
        if (result.isPresent()) {
            String plan = result.get().trim(); 
            if (!plan.isEmpty()) {
                plans.add(plan);
                currentBudget.setPlans(plans);
                BudgetDataManager.saveBudget(currentBudget);
                
                // update the progress bar
                Budget oldBudget = new Budget(currentBudget.getName(), 
                                            currentBudget.getPlannedAmount(), 
                                            currentBudget.getActualAmount());
                // save the old budget to history
                List<String> oldPlans = new java.util.ArrayList<>(plans);
                oldPlans.remove(oldPlans.size() - 1); // remove the new plan
                oldBudget.setPlans(oldPlans);
                
                // update the history
                BudgetDataManager.updateBudgetInHistory(oldBudget, currentBudget);
                
                planListView.setItems(plans); 
            }
        }
    }

    @FXML
    private void handleRemovePlan() {
        int selectedIndex = planListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            // store the current budget
            Budget oldBudget = new Budget(currentBudget.getName(), 
                                        currentBudget.getPlannedAmount(), 
                                        currentBudget.getActualAmount());
            oldBudget.setPlans(currentBudget.getPlans());
            
            plans.remove(selectedIndex);
            currentBudget.setPlans(plans); // update the budget
            BudgetDataManager.saveBudget(currentBudget);
            
            // update the history
            BudgetDataManager.updateBudgetInHistory(oldBudget, currentBudget);
        }
    }

    //================ Core Logic ================//
    private void showBudgetDialog(String title, String name, double planned, double actual, String styleClass) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Edit budget details");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        if (styleClass != null && !styleClass.isEmpty()) {
            dialogPane.getStyleClass().add(styleClass);
        }
        
        
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(name);
        TextField plannedField = new TextField(String.valueOf(planned));
        TextField actualField = new TextField(String.valueOf(actual));

        grid.addRow(0, new Label("Budget Name:"), nameField);
        grid.addRow(1, new Label("Planned Amount:"), plannedField);
        grid.addRow(2, new Label("Actual Amount:"), actualField);

        dialog.getDialogPane().setContent(grid);

        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) { 
            try {
                String newName = nameField.getText().trim();
                double newPlanned = Double.parseDouble(plannedField.getText());
                double newActual = Double.parseDouble(actualField.getText());

                //validateInput(newName, newPlanned, newActual); 

                if (newName.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Budget name cannot be empty!", "error-alert");
                    return;
                }
                if (newPlanned <= 0 || newActual < 0 || newPlanned < newActual) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid amounts. Ensure:\n- Planned > 0\n- Actual ≥ 0\n- Planned >= Actual", "error-alert");
                    return;
                }
                
                // check if the budget is being deleted
                if (Math.abs(newActual - newPlanned) < 0.01) { // use a small epsilon value
                    // store the current budget
                    Budget oldBudget = new Budget(currentBudget.getName(), 
                                                currentBudget.getPlannedAmount(), 
                                                currentBudget.getActualAmount());
                    oldBudget.setPlans(currentBudget.getPlans());
                    
                    // update the budget
                    currentBudget.setName(newName);
                    currentBudget.setPlannedAmount(newPlanned);
                    currentBudget.setActualAmount(newActual);
                    
                    // update the history
                    BudgetDataManager.updateBudgetInHistory(oldBudget, currentBudget);
                    
                    // show the congratulatory alert
                    Alert congratsAlert = new Alert(Alert.AlertType.INFORMATION);
                    congratsAlert.setTitle("Goal Achieved");
                    congratsAlert.setHeaderText("Congratulations!");
                    congratsAlert.setContentText("Congratulations on achieving this goal!");
                    DialogPane congratsDialogPane = congratsAlert.getDialogPane();
                    congratsDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                    congratsDialogPane.getStyleClass().add("success-alert"); 
                    
                    // make button to english
                    ButtonType okButtonAlert = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    congratsAlert.getButtonTypes().setAll(okButtonAlert);
                    
                    congratsAlert.showAndWait();
                    
                    // delete the budget
                    handleRemoveBudget();
                } else {
                    // store the current budget
                    Budget oldBudget = new Budget(currentBudget.getName(), 
                                                currentBudget.getPlannedAmount(), 
                                                currentBudget.getActualAmount());
                    oldBudget.setPlans(currentBudget.getPlans());
                    
                    // update the budget
                    currentBudget.setName(newName);
                    currentBudget.setPlannedAmount(newPlanned);
                    currentBudget.setActualAmount(newActual);
                    BudgetDataManager.saveBudget(currentBudget);
                    
                    // update the history
                    BudgetDataManager.updateBudgetInHistory(oldBudget, currentBudget);
                    
                    refreshSingleBudgetDisplay();
                    updateBudgetBalance(); 
                }

                } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid number format", "error-alert");
                } catch (IllegalArgumentException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", e.getMessage(), "error-alert");
                }
        }
    }
    
    // private void validateInput(String name, double planned, double actual) {
    //     if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
    //     if (planned <= 0) throw new IllegalArgumentException("Planned amount must > 0");
    //     if (actual < 0) throw new IllegalArgumentException("Actual amount cannot be negative");
    //     if (planned < actual) throw new IllegalArgumentException("Planned must >= Actual");
    //     // Note: We now allow actual == planned for goal achievement
    // }
    // Validation logic is now integrated into methods calling showAlert with appropriate styleClass.

    
    private void refreshSingleBudgetDisplay() {
        singleBudgetContainer.getChildren().clear(); // ensure container is empty

        if (currentBudget != null) {
            HBox budgetItem = createBudgetItem(
                currentBudget.getName(),
                currentBudget.getPlannedAmount(),
                currentBudget.getActualAmount()
            );
            singleBudgetContainer.getChildren().add(budgetItem); // only add once
        }
    }
    

    //================ UI Components ================//


    private HBox createBudgetItem(String name, double planned, double actual) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");

        // ==================== progress bar ====================
        VBox progressBox = new VBox(8);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ProgressBar progressBar = new ProgressBar(actual / planned);
        progressBar.setPrefWidth(300);
      

        // apply style to progress bar
            progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
            double progress = newVal.doubleValue();
            progressBar.getStyleClass().removeAll("warning", "caution", "safe"); 

            // output the progress
            System.out.println("Current Progress: " + progress);

            // add the class
            if (progress < 0.4) {
                progressBar.getStyleClass().add("warning"); 
            } else if (progress < 0.6) {
                progressBar.getStyleClass().add("caution"); 
            } else {
                progressBar.getStyleClass().add("safe"); 
            }
        });



        // % process
        Label progressLabel = new Label();
        progressLabel.textProperty().bind(
            Bindings.format("%.0f%%", progressBar.progressProperty().multiply(100))
        );
        progressLabel.getStyleClass().add("progress-label");

        StackPane progressStack = new StackPane();
        progressStack.getChildren().addAll(progressBar, progressLabel); // add progressBar first

        
        Label detailLabel = new Label(String.format("Planned: ￥%.2f | Actual: ￥%.2f", planned, actual));
        detailLabel.setStyle("-fx-text-fill: #666;");

        progressBox.getChildren().addAll(nameLabel, progressStack, detailLabel); 

        // ==================== operation button ====================
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        editBtn.setOnAction(e -> showBudgetDialog("Edit Budget", name, planned, actual, "confirmation-dialog"));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            // remove the budget from history
            List<Budget> tempBudgetHistory = BudgetDataManager.loadBudgetHistory();
            tempBudgetHistory.removeIf(b -> b.getName().equals(name) && 
                                  Math.abs(b.getPlannedAmount() - planned) < 0.01 && 
                                  Math.abs(b.getActualAmount() - actual) < 0.01);
            BudgetDataManager.saveBudgetHistory(tempBudgetHistory);
            
            
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm deletion");
            confirmDialog.setHeaderText("Delete confirmation");
            confirmDialog.setContentText("Once deleted, it cannot be modified anymore. Are you sure you want to delete it?");
            DialogPane confirmDialogPane = confirmDialog.getDialogPane();
            confirmDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            confirmDialogPane.getStyleClass().add("confirmation-dialog"); 
            
            
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmDialog.getButtonTypes().setAll(okButton, cancelButton);
            
            // wait for user input
            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                singleBudgetContainer.getChildren().remove(container); // remove the container
                handleRemoveBudget(); // call the method to remove the budget
            }
        });

        container.getChildren().addAll(progressBox, editBtn, deleteBtn);
        return container;
    }
    
    /**
     * create a read-only budget item
     */
    private HBox createReadOnlyBudgetItem(String name, double planned, double actual) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-radius: 5;");

        // ==================== progress bar ====================
        VBox progressBox = new VBox(8);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ProgressBar progressBar = new ProgressBar(actual / planned);
        progressBar.setPrefWidth(300);
      
        // apply color style based on progress
        progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
            double progress = newVal.doubleValue();
            progressBar.getStyleClass().removeAll("warning", "caution", "safe"); // remove all old states

            // add the color style
            if (progress < 0.4) {
                progressBar.getStyleClass().add("warning"); 
            } else if (progress < 0.6) {
                progressBar.getStyleClass().add("caution"); 
            } else {
                progressBar.getStyleClass().add("safe"); 
            }
        });

        
        Label progressLabel = new Label();
        progressLabel.textProperty().bind(
            Bindings.format("%.0f%%", progressBar.progressProperty().multiply(100))
        );
        progressLabel.getStyleClass().add("progress-label");

        StackPane progressStack = new StackPane();
        progressStack.getChildren().addAll(progressBar, progressLabel);

        
        Label detailLabel = new Label(String.format("Planned: ￥%.2f | Actual: ￥%.2f", planned, actual));
        detailLabel.setStyle("-fx-text-fill: #666;");

        // find the matching budget in history
        Budget matchingBudget = null;
        List<Budget> budgetHistoryList = BudgetDataManager.loadBudgetHistory();
        for (Budget b : budgetHistoryList) {
            if (b.getName().equals(name) && 
                Math.abs(b.getPlannedAmount() - planned) < 0.01 && 
                Math.abs(b.getActualAmount() - actual) < 0.01) {
                matchingBudget = b;
                break;
            }
        }
        
        // add plans list
        if (matchingBudget != null && !matchingBudget.getPlans().isEmpty()) {
            Label plansLabel = new Label("Plans:");
            plansLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 0 0;");
            
            ListView<String> plansListView = new ListView<>();
            plansListView.setPrefHeight(Math.min(matchingBudget.getPlans().size() * 24 + 2, 100)); 
            plansListView.setItems(FXCollections.observableArrayList(matchingBudget.getPlans()));
            plansListView.setStyle("-fx-background-color: transparent;"); 
            plansListView.setMouseTransparent(true); 
            plansListView.setFocusTraversable(false); 
            plansListView.setCellFactory(param -> new javafx.scene.control.ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setText(item);
                        setStyle("-fx-background-color: transparent; -fx-text-fill: #666;"); // set text color
                    }
                }
            });
            plansListView.getSelectionModel().clearSelection(); // clear selection
            
            progressBox.getChildren().addAll(nameLabel, progressStack, detailLabel, plansLabel, plansListView);
        } else {
            progressBox.getChildren().addAll(nameLabel, progressStack, detailLabel);
        }

        // ==================== operation button ====================
        // add delete button
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            // remove from history
            List<Budget> tempBudgetHistory = BudgetDataManager.loadBudgetHistory();
            tempBudgetHistory.removeIf(b -> b.getName().equals(name) && 
                                  Math.abs(b.getPlannedAmount() - planned) < 0.01 && 
                                  Math.abs(b.getActualAmount() - actual) < 0.01);
            BudgetDataManager.saveBudgetHistory(tempBudgetHistory);
            
            // translate button text to english
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm deletion");
            confirmDialog.setHeaderText("Delete confirmation");
            confirmDialog.setContentText("Once deleted, it cannot be modified anymore. Are you sure you want to delete it?");
            DialogPane confirmDialogPane = confirmDialog.getDialogPane();
            confirmDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            confirmDialogPane.getStyleClass().add("confirmation-dialog"); // 添加CSS类
            
            // translate button text to english
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmDialog.getButtonTypes().setAll(okButton, cancelButton);
            
            // wait for user confirmation
            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                // remove the item from the list
                ((VBox) container.getParent()).getChildren().remove(container);
            }
        });

        container.getChildren().addAll(progressBox, deleteBtn);
        return container;
    }
    private void updateBudgetBalance() {
        double balance = (currentBudget != null) ? 
            currentBudget.getPlannedAmount() - currentBudget.getActualAmount() : 0;
        budgetBalanceLabel.setText(String.format("Budget Balance: ￥%.2f", balance));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message, String styleClass) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // apply css
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        if (styleClass != null && !styleClass.isEmpty()) {
            dialogPane.getStyleClass().add(styleClass);
        }

        // modify text into english
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(
            new ButtonType("OK", ButtonBar.ButtonData.OK_DONE)
        );

        alert.showAndWait();
    }
}