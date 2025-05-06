package com.finance.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import com.finance.service.AIChatService;

public class AIChatController {
    @FXML private VBox chatContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextArea messageInput;
    @FXML private Label statusLabel;
    
    private final AIChatService chatService = new AIChatService();
    
    @FXML
    private void initialize() {
        // Auto-scroll to bottom when new messages are added
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            chatScrollPane.setVvalue(1.0);
        });
        // 动态调整气泡宽度以适应窗口变化
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
