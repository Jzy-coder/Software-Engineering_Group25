package com.finance.extracted_logic;

import com.finance.service.AIChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AIChatProcessorTest {
    @Mock
    private AIChatService mockChatService;
    private AIChatProcessor chatProcessor;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatProcessor = new AIChatProcessor();
        chatProcessor.setChatService(mockChatService);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void testProcessMessage_ValidMessage() {
        AtomicReference<String> responseHolder = new AtomicReference<>();
        String testMessage = "Test message";
        
        chatProcessor.processMessage(testMessage, responseHolder::set);
        
        verify(mockChatService, times(1))
            .getAIResponse(eq(testMessage), any(Consumer.class));
    }
    
    @Test
    void testProcessMessage_EmptyMessage() {
        AtomicReference<String> responseHolder = new AtomicReference<>();
        chatProcessor.processMessage("", responseHolder::set);
        assertNull(responseHolder.get());
    }
    
    @Test
    void testProcessMessage_NullMessage() {
        AtomicReference<String> responseHolder = new AtomicReference<>();
        chatProcessor.processMessage(null, responseHolder::set);
        assertNull(responseHolder.get());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void testSetSystemPrompt_ValidPrompt() {
        AtomicReference<String> responseHolder = new AtomicReference<>();
        String testPrompt = "New system prompt";
        
        chatProcessor.setSystemPrompt(testPrompt, responseHolder::set);
        
        verify(mockChatService, times(1))
            .setSystemPrompt(eq(testPrompt), any(Consumer.class));
    }
    
    @Test
    void testProcessMessage_ErrorHandling() {
        AtomicReference<String> responseHolder = new AtomicReference<>();
        String errorMessage = "API Error: Service unavailable";
        
        doAnswer(invocation -> {
            Consumer<String> callback = invocation.getArgument(1);
            callback.accept(errorMessage);
            return null;
        }).when(mockChatService).getAIResponse(any(), any());
        
        chatProcessor.processMessage("Test", responseHolder::set);
        
        assertEquals(errorMessage, responseHolder.get());
    }
    
    @Test
    void testSetSystemPrompt_EmptyPrompt() {
        AtomicReference<String> responseHolder = new AtomicReference<>();
        chatProcessor.setSystemPrompt("", responseHolder::set);
        assertNull(responseHolder.get());
    }
    
    @Test
    void testSetSystemPrompt_NullPrompt() {
        AtomicReference<String> responseHolder = new AtomicReference<>();
        chatProcessor.setSystemPrompt(null, responseHolder::set);
        assertNull(responseHolder.get());
    }
}