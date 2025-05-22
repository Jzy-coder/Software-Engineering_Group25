package com.finance.extracted_logic;

import com.finance.service.AIChatService;
import java.util.function.Consumer;

/**
 * Processes AI chat messages and system prompts by interacting with the AIChatService.
 */
public class AIChatProcessor {
    private AIChatService chatService;
    
    /**
     * Sets the AIChatService instance to be used for processing messages.
     * @param chatService The AIChatService instance.
     */
    public void setChatService(AIChatService chatService) {
        this.chatService = chatService;
    }
    
    /**
     * Processes a user message by sending it to the AIChatService and handling the response.
     * @param message The user's input message.
     * @param responseHandler A Consumer to handle the AI's response.
     */
    public void processMessage(String message, Consumer<String> responseHandler) {
        if (message != null && !message.trim().isEmpty()) {
            chatService.getAIResponse(message.trim(), responseHandler);
        }
    }
    
    /**
     * Sets the system prompt for the AIChatService.
     * @param prompt The system prompt to set.
     * @param responseHandler A Consumer to handle the response from setting the prompt.
     */
    public void setSystemPrompt(String prompt, Consumer<String> responseHandler) {
        if (prompt != null && !prompt.trim().isEmpty()) {
            chatService.setSystemPrompt(prompt.trim(), responseHandler);
        }
    }
}