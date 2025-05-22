package com.finance.extracted_logic;

import com.finance.service.AIChatService;
import java.util.function.Consumer;

/**
 * Processes AI chat messages and system prompts by interacting with the AIChatService.
 */
public class AIChatProcessor {
    private AIChatService chatService;
    
    public void setChatService(AIChatService chatService) {
        this.chatService = chatService;
    }
    
    public void processMessage(String message, Consumer<String> responseHandler) {
        if (message != null && !message.trim().isEmpty()) {
            chatService.getAIResponse(message.trim(), responseHandler);
        }
    }
    
    public void setSystemPrompt(String prompt, Consumer<String> responseHandler) {
        if (prompt != null && !prompt.trim().isEmpty()) {
            chatService.setSystemPrompt(prompt.trim(), responseHandler);
        }
    }
}