package com.finance.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import com.finance.service.AIChatService;

import java.util.Optional;

public class AIChatController {
    @FXML private VBox chatContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextArea messageInput;
    @FXML private Label statusLabel;
    @FXML private HBox styleButtonsContainer; // Added reference for the style buttons HBox

    private final AIChatService chatService = new AIChatService();

    @FXML
    private void initialize() {
        // Add tooltips to predefined buttons (if not set in FXML)
        for (javafx.scene.Node node : styleButtonsContainer.getChildren()) {
            if (node instanceof Button && ((Button) node).getUserData() != null) {
                Tooltip.install(node, new Tooltip((String) ((Button) node).getUserData()));
            }
        }

        // Auto-scroll to bottom when new messages are added
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            chatScrollPane.setVvalue(1.0);
        });
        // Dynamically adjust the bubble width to adapt to the window changes
        chatScrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            for (javafx.scene.Node node : chatContainer.getChildren()) {
                if (node instanceof HBox) {
                    HBox hbox = (HBox) node;
                    for (javafx.scene.Node bubble : hbox.getChildren()) {
                        if (bubble instanceof TextFlow) {
                            double maxBubbleWidth = chatScrollPane.getWidth() > 0 ? chatScrollPane.getWidth() - 80 : 400;
                            ((TextFlow) bubble).setMaxWidth(maxBubbleWidth);
                            for (javafx.scene.Node label : ((TextFlow) bubble).getChildren()) {
                                if (label instanceof Label) {
                                    if (((Label) label).getText().endsWith(": ")) {
                                        ((Label) label).setMaxWidth(60);
                                    } else {
                                        ((Label) label).setMaxWidth(maxBubbleWidth - 60);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    
    @FXML
    private void handleSendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            addMessageToChat("You", message, true);
            messageInput.clear();
            
            statusLabel.setText("Thinking...");
            System.out.println("Sending message to AI: " + message); // Debug log
            chatService.getAIResponse(message, (String response) -> {
                javafx.application.Platform.runLater(() -> {
                    System.out.println("Received AI response: " + response); // Debug log
                    if (response.startsWith("API Error:") || response.startsWith("Error:")) {
                        statusLabel.setText("Error - see console");
                    } else {
                        statusLabel.setText("Ready");
                    }
                    addMessageToChat("AI Assistant", response, false);
                });
            });
        }
    }
    
    @FXML
    private void handleClearChat() {
        chatContainer.getChildren().clear();
    }
    
    // --- New methods for handling style buttons --- 

    @FXML
    private void handleStyleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String systemPrompt = (String) clickedButton.getUserData();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            statusLabel.setText("Setting style...");
            System.out.println("Setting system prompt: " + systemPrompt); // Debug log
            // Call service method to set the system prompt
            chatService.setSystemPrompt(systemPrompt, (String response) -> {
                javafx.application.Platform.runLater(() -> {
                    System.out.println("Received style confirmation: " + response); // Debug log
                    if (response.startsWith("API Error:") || response.startsWith("Error:")) {
                        statusLabel.setText("Error setting style");
                        addMessageToChat("System", "Error setting style: " + response, false);
                    } else {
                        statusLabel.setText("Style set: " + clickedButton.getText());
                        // Display confirmation message from AI in the chat
                        addMessageToChat("AI Assistant", response, false);
                    }
                });
            });
        }
    }

    @FXML
    private void handleAddCustomStyle() {
        // Dialog for custom style name
        TextInputDialog nameDialog = new TextInputDialog("Custom");
        nameDialog.setTitle("Add Custom Style");
        nameDialog.setHeaderText("Enter a short name for the style button (e.g., 'Analytical').");
        nameDialog.setContentText("Button Name:");

        // Apply CSS styles
        DialogPane nameDialogPane = nameDialog.getDialogPane();
        nameDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        nameDialogPane.getStyleClass().add("dialog-pane");

        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isPresent() && !nameResult.get().trim().isEmpty()) {
            String buttonName = nameResult.get().trim();

            // Dialog for custom style prompt
            TextArea promptArea = new TextArea();
            promptArea.setPromptText("Enter the system prompt for the AI (e.g., 'Act as a detailed financial analyst.')");
            promptArea.setWrapText(true);

            Dialog<String> promptDialog = new Dialog<>();
            promptDialog.setTitle("Add Custom Style Prompt");
            promptDialog.setHeaderText("Enter the system prompt for the '" + buttonName + "' style.");
            promptDialog.getDialogPane().setContent(promptArea);
        promptDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Apply CSS styles
        DialogPane promptDialogPane = promptDialog.getDialogPane();
        promptDialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        promptDialogPane.getStyleClass().add("dialog-pane");

        // Set result converter
            promptDialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return promptArea.getText();
                }
                return null;
            });

            Optional<String> promptResult = promptDialog.showAndWait();
            if (promptResult.isPresent() && !promptResult.get().trim().isEmpty()) {
                String prompt = promptResult.get().trim();

                // Create and add the new button
                Button newStyleButton = new Button(buttonName);
                newStyleButton.setUserData(prompt);
                newStyleButton.setOnAction(this::handleStyleButtonClick);
                Tooltip.install(newStyleButton, new Tooltip(prompt)); // Add tooltip
                
                // Insert before the '+' button
                int plusButtonIndex = -1;
                for(int i=0; i<styleButtonsContainer.getChildren().size(); i++){
                    if(styleButtonsContainer.getChildren().get(i) instanceof Button && ((Button)styleButtonsContainer.getChildren().get(i)).getText().equals("+")){
                        plusButtonIndex = i;
                        break;
                    }
                }
                if(plusButtonIndex != -1){
                    styleButtonsContainer.getChildren().add(plusButtonIndex, newStyleButton);
                } else {
                    styleButtonsContainer.getChildren().add(newStyleButton); // Fallback add to end
                }
                
                statusLabel.setText("Custom style '" + buttonName + "' added.");
            }
        }
    }

    // --- End of new methods ---

    private void addMessageToChat(String sender, String message, boolean isUser) {
        Label senderLabel = new Label(sender + ":");
        Label messageLabel = new Label(message);
        senderLabel.setStyle("-fx-font-weight: bold;");
        senderLabel.setWrapText(true);
        messageLabel.setWrapText(true);

        double maxBubbleWidth = chatScrollPane.getWidth() > 0 ? chatScrollPane.getWidth() - 80 : 400;

        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(5, 10, 5, 10));

        if (isUser) {
            TextFlow messageBubble = new TextFlow();
            messageBubble.setMaxWidth(maxBubbleWidth);
            messageLabel.setMaxWidth(maxBubbleWidth - 60); // Adjust width if needed
            senderLabel.setMaxWidth(60); // Adjust width if needed
            messageBubble.getChildren().addAll(senderLabel, new Label(" "), messageLabel); // Add space for inline display
            messageBubble.getStyleClass().add("user-message");
            messageBox.getChildren().add(messageBubble);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            VBox messageBubbleContent = new VBox(); // Use VBox for AI messages
            messageBubbleContent.setMaxWidth(maxBubbleWidth);
            senderLabel.setMaxWidth(maxBubbleWidth); // Allow sender label full width
            messageLabel.setMaxWidth(maxBubbleWidth); // Allow message label full width
            messageBubbleContent.getChildren().addAll(senderLabel, messageLabel);
            messageBubbleContent.getStyleClass().add("ai-message"); // Apply style to VBox or individual labels
            // Add a background/border to the VBox to mimic the bubble appearance if needed
            messageBubbleContent.setStyle("-fx-background-color: #e1f5fe; -fx-background-radius: 10; -fx-padding: 10;"); // Example styling

            messageBox.getChildren().add(messageBubbleContent);
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        chatContainer.getChildren().add(messageBox);
    }
}
