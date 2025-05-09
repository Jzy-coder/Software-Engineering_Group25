package com.finance.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class AIChatService {
    private static final String API_KEY = "sk-adf4d171a77846b5b1f75307d52bf619"; // Replace with your actual API key
    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String DEFAULT_SYSTEM_PROMPT = "You are a helpful personal finance assistant. Your goal is to provide accurate and relevant financial advice and information based on the user's queries.";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String currentSystemPrompt = DEFAULT_SYSTEM_PROMPT; // Store the current system prompt

    /**
     * Sends a message to the AI and gets a response, including the current system prompt.
     * @param message The user's message.
     * @param callback The callback to handle the AI's response.
     */
    public void getAIResponse(String message, Consumer<String> callback) {
        sendRequest(message, this.currentSystemPrompt, callback);
    }

    /**
     * Sets a new system prompt and gets a confirmation message from the AI.
     * @param prompt The new system prompt to set.
     * @param callback The callback to handle the AI's confirmation response.
     */
    public void setSystemPrompt(String prompt, Consumer<String> callback) {
        this.currentSystemPrompt = prompt; // Update the current system prompt
        // Send only the system prompt to get a confirmation/acknowledgement
        // We send a dummy user message to trigger the response based on the new system prompt.
        sendRequest("Confirm style update.", prompt, (response) -> {
            // You might want to customize the expected confirmation message check here
            if (!response.startsWith("API Error:") && !response.startsWith("Error:")) {
                 // Assuming the AI confirms style setting, return a standard confirmation message
                 // Or you could return the actual AI response if it's meaningful
                 callback.accept("AI style set successfully."); // Simplified confirmation
            } else {
                callback.accept(response); // Pass on error messages
            }
        });
    }

    /**
     * Helper method to send requests to the AI API.
     * @param userMessage The user's message.
     * @param systemPrompt The system prompt to use.
     * @param callback The callback to handle the response.
     */
    private void sendRequest(String userMessage, String systemPrompt, Consumer<String> callback) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "deepseek-chat");
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 1000);

            JsonArray messagesArray = new JsonArray();

            // Add the potentially updated system prompt
            JsonObject systemMessageObj = new JsonObject();
            systemMessageObj.addProperty("role", "system");
            systemMessageObj.addProperty("content", systemPrompt); // Use the provided system prompt
            messagesArray.add(systemMessageObj);

            // Add user message
            JsonObject userMessageObj = new JsonObject();
            userMessageObj.addProperty("role", "user");
            userMessageObj.addProperty("content", userMessage);
            messagesArray.add(userMessageObj);

            requestBody.add("messages", messagesArray);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

            System.out.println("Sending request to: " + API_URL);
            System.out.println("Request headers: " + request.headers().map());
            System.out.println("Request body: " + requestBody);

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("Received response status: " + response.statusCode());
                    System.out.println("Response headers: " + response.headers().map());
                    System.out.println("Response body: " + response.body());

                    if (response.statusCode() != 200) {
                        // Provide more detailed error info if possible
                        String errorBody = response.body() != null ? response.body() : "No response body";
                        throw new RuntimeException("API request failed with status: " + response.statusCode() + " - " + errorBody);
                    }
                    return response.body();
                })
                .thenAccept(responseBody -> {
                    try {
                        System.out.println("Processing response...");
                        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                        // Check for errors in the response body itself
                        if (jsonResponse.has("error")) {
                             String errorMessage = jsonResponse.getAsJsonObject("error").get("message").getAsString();
                             System.err.println("API returned an error: " + errorMessage);
                             callback.accept("API Error: " + errorMessage);
                             return;
                        }
                        
                        String aiMessage = jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                        System.out.println("AI response extracted: " + aiMessage);
                        callback.accept(aiMessage);
                    } catch (Exception e) {
                        System.err.println("Error parsing response: " + e.getMessage() + " | Response body: " + responseBody);
                        callback.accept("Failed to parse API response: " + e.getMessage());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("API Communication Error: " + e.getMessage());
                    e.printStackTrace();
                    // Provide a clearer error message to the callback
                    String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    callback.accept("API Error: " + errorMessage);
                    return null;
                });
        } catch (Exception e) {
            System.err.println("Error creating API request: " + e.getMessage());
            callback.accept("Error: Failed to create API request - " + e.getMessage());
        }
    }
}
