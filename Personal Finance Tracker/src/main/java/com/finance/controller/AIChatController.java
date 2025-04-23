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
        TextFlow messageBubble = new TextFlow();
        Label senderLabel = new Label(sender + ": ");
        Label messageLabel = new Label(message);
        senderLabel.setStyle("-fx-font-weight: bold;");
        senderLabel.setWrapText(true);
        messageLabel.setWrapText(true);
        double maxBubbleWidth = chatScrollPane.getWidth() > 0 ? chatScrollPane.getWidth() - 80 : 400;
        messageBubble.setMaxWidth(maxBubbleWidth);
        messageLabel.setMaxWidth(maxBubbleWidth - 60);
        senderLabel.setMaxWidth(60);
        messageBubble.getChildren().addAll(senderLabel, messageLabel);
        messageBubble.getStyleClass().add(isUser ? "user-message" : "ai-message");

        HBox messageBox = new HBox(messageBubble);
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        if (isUser) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }
        chatContainer.getChildren().add(messageBox);
    }
}
