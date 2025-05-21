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
    private StackPane chartContainer;
    
    private PieChart pieChart;
    private BarChart<String, Number> barChart;

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
        // Initialize the chart
        initializeCharts();
        
        transactionService = com.finance.gui.LoginManager.getTransactionService();
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());

        
        modelComboBox.setItems(FXCollections.observableArrayList("pie chart", "column chart"));
        modelComboBox.getSelectionModel().selectFirst(); 

       
        List<LocalDate> availableDates = getAvailableTransactionDates();
        if (!availableDates.isEmpty()) {
            
            selectedDate = availableDates.get(availableDates.size() - 1);
            rangeStartDate = availableDates.get(0);
            rangeEndDate = availableDates.get(availableDates.size() - 1);
        } else {
            // If no transaction data, set default date
            selectedDate = LocalDate.now();
            rangeStartDate = LocalDate.now();
            rangeEndDate = LocalDate.now();
        }
        
        // setup chart data
        updateDateLabel();

        // add event listeners
        modelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateChartData());
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateChartData());
        dateSelectionGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            dateButton.setText(singleDateRadio.isSelected() ? "Select Date" : "Select Date Range");
            updateDateLabel();
            updateChartData();
        });

        // setup chart data
        showDefaultView();

        // initialize event listeners
        pieChart.setLabelLineLength(20);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);
        
        // register event listeners
        TransactionEventManager.getInstance().addTransactionEventListener(this);
    }
    
    /**
     * create bar chart data
     */
    public void cleanup() {
        
        TransactionEventManager.getInstance().removeTransactionEventListener(this);
    }

    /**
     * add transaction
     */
    private List<LocalDate> getAvailableTransactionDates() {
        return transactions.stream()
                .map(t -> t.getDate().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * handle transaction event
     */
    @FXML
    private void handleDateSelection() {
        // add transaction
        List<LocalDate> availableDates = getAvailableTransactionDates();
        
        if (availableDates.isEmpty()) {
            showAlert("No available transaction data dates");
            return;
        }

        if (singleDateRadio.isSelected()) {
            // create date picker
            DatePicker datePicker = new DatePicker();
            datePicker.setValue(selectedDate);
            datePicker.getStyleClass().add("date-picker");
            
            // setup date picker
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || !availableDates.contains(date));
                }
            });
            
            // create dialog
            Dialog<LocalDate> dialog = new Dialog<>();
            dialog.setTitle("Select a date");
            dialog.setHeaderText("Please choose a date with transaction data");
            
            // setup dialog
            ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(selectButtonType,cancelButtonType);
            
            // add css
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");
            
            // setup date picker
            dialogPane.setContent(datePicker);
            
            // transaction data
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == selectButtonType) {
                    return datePicker.getValue();
                }
                return null;
            });
            
            // show dialog
            Optional<LocalDate> result = dialog.showAndWait();
            result.ifPresent(date -> {
                selectedDate = date;
                updateDateLabel();
                updateChartData();
                updateChartStyle();
                setupPieChartListeners();
            });
        } else {
            // show date range selector
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
     * handle transaction event
     */
    private void setupPieChartListeners() {
        int colorIndex = 0;
        for (PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();
            // setup css
            node.getStyleClass().add("data" + colorIndex);
            
            // add click effect
            node.setOnMouseClicked(e -> {
                // remove shadow effect from other segments
                pieChart.getData().forEach(d -> d.getNode().setEffect(null));
                // add shadow effect to current segment
                data.getNode().setEffect(new DropShadow(10, javafx.scene.paint.Color.GRAY));
                // show selection details
                double amount = data.getName().equals("No Data") ? 0.00 : data.getPieValue();
                
                // create alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Selection Details");
                alert.setHeaderText(data.getName());
                alert.setContentText("Amount: " + String.format("%.2f", amount));
                
                // add css
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                dialogPane.getStyleClass().add("dialog-pane");
                
                // Set button text to English
                ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(okButton);
                
                alert.showAndWait();
            });
            
            colorIndex = (colorIndex + 1) % 10; // use a different color for each segment
        }
    }

    private void updateChartStyle() {
        pieChart.setLabelsVisible(true);
        pieChart.setLabelLineLength(30);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);
        pieChart.setClockwise(false);
        pieChart.setStartAngle(90);
    }

    private void initializeCharts() {
        // initialize pie chart
        pieChart = new PieChart();
        pieChart.getStylesheets().add(getClass().getResource("/css/chart-styles.css").toExternalForm());
        pieChart.getStyleClass().add("pie-chart");
        pieChart.setLabelLineLength(30);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);
        pieChart.setStartAngle(90);
        pieChart.setClockwise(false);
        // for pie chart
        pieChart.setAnimated(false);
        // setup chart data
        pieChart.setPrefSize(600, 400);
        // setup chart container
        pieChart.setLabelsVisible(true);
        pieChart.setLabelLineLength(20);
        // add chart to chart container
        pieChart.setMinSize(300, 200);
        pieChart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        // initialize bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Category");
        yAxis.setLabel("Amount");
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Income/Expense Analysis");
        barChart.setAnimated(false); 
    }
    
    private void showDefaultView() {
        // add default chart
        chartContainer.getChildren().clear();
        ObservableList<PieChart.Data> pieChartData = createPieChartData("pie chart", incomeRadio.isSelected());
        pieChart.setData(pieChartData);
        setupPieChartListeners();
        updateChartStyle();
        chartContainer.getChildren().add(pieChart);
    }
    
    private void updateChartData() {
        String selectedModel = modelComboBox.getValue();
        boolean isIncome = incomeRadio.isSelected();
        
        chartContainer.getChildren().clear();
        
        switch (selectedModel) {
            case "pie chart":
                ObservableList<PieChart.Data> pieChartData = createPieChartData(selectedModel, isIncome);
                pieChart.setData(pieChartData);
                // remove css
                pieChart.setStartAngle(90);
                pieChart.setClockwise(false);
                pieChart.setLabelsVisible(true);
                pieChart.setLabelLineLength(20);
                // enable css
                pieChart.layout();
                setupPieChartListeners();
                updateChartStyle();
                chartContainer.getChildren().add(pieChart);
                
                javafx.application.Platform.runLater(() -> {
                    pieChart.layout();
                    pieChart.setLabelsVisible(false);
                    pieChart.setLabelsVisible(true);
                });
                break;
                
            case "column chart":
                updateBarChartData(isIncome);
                chartContainer.getChildren().add(barChart);
                break;
                
            default:
                showDefaultView();
                break;
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
    private ObservableList<PieChart.Data> createPieChartData(String model, boolean isIncome) {
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
        
        // Generate data based on selected chart type
        if (model.equals("pie chart")) {
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
        
        // apply css
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        // Set button text to English
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);
        alert.showAndWait();
    }
    
    /**
     * Implementation of TransactionEventListener interface method
     * Called when transaction data changes
     */
    private void updateBarChartData(boolean isIncome) {
        barChart.getData().clear();
        
        // filter transactions by type and date
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
        
        // create grouped data
        Map<String, Double> groupedData = filteredTransactions.stream()
                .collect(Collectors.groupingBy(
                    Transaction::getType,
                    Collectors.summingDouble(t -> Math.abs(t.getAmount()))
                ));
        
        // create series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // add data
        groupedData.forEach((type, amount) -> {
            series.getData().add(new XYChart.Data<>(type, amount));
        });
        
        barChart.getData().add(series);
        
        
        barChart.setLegendVisible(false);
        
        // set color
        String[] colors = {
            "#f3622d", "#fba71b", "#57b757", "#41a9c9", "#4258c9",
            "#9a42c8", "#c84164", "#888888", "#e45e9d", "#5e9de4"
        };
        
        int colorIndex = 0;
        for (XYChart.Data<String, Number> item : series.getData()) {
            String color = colors[colorIndex % colors.length];
            item.getNode().setStyle("-fx-bar-fill: " + color + ";");
            colorIndex++;
        }
    }
    
    @Override
    public void onTransactionChanged(TransactionEvent event) {
        // Reload transaction data
        transactions = FXCollections.observableArrayList(transactionService.getAllTransactions());
        
        // Update chart
        updateChartData();
    }
}