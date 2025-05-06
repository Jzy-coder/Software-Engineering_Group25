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
    private static final String API_KEY = "sk-ad2454088d4d4813aea9219324e0d860";
    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    public void getAIResponse(String message, Consumer<String> callback) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "deepseek-chat");
            requestBody.addProperty("temperature", 0.7);
            requestBody.addProperty("max_tokens", 1000);
            
            JsonArray messagesArray = new JsonArray();

            // Add system prompt for personal finance assistant role
            JsonObject systemMessageObj = new JsonObject();
            systemMessageObj.addProperty("role", "system");
            systemMessageObj.addProperty("content", "You are a helpful personal finance assistant. Your goal is to provide accurate and relevant financial advice and information based on the user's queries.");
            messagesArray.add(systemMessageObj);

            // Add user message
            JsonObject userMessageObj = new JsonObject();
            userMessageObj.addProperty("role", "user");
            userMessageObj.addProperty("content", message);
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
                        throw new RuntimeException("API request failed with status: " + response.statusCode());
                    }
                    return response.body();
                })
                .thenAccept(response -> {
                    try {
                        System.out.println("Processing response...");
                        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                        String aiMessage = jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                        System.out.println("AI response extracted: " + aiMessage);
                        callback.accept(aiMessage);
                    } catch (Exception e) {
                        System.err.println("Error parsing response: " + e.getMessage());
                        callback.accept("Failed to parse API response: " + e.getMessage());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("API Error: " + e.getMessage());
                    e.printStackTrace();
                    callback.accept("API Error: " + e.getMessage());
                    return null;
                });
        } catch (Exception e) {
            callback.accept("Error: " + e.getMessage());
        }
    }
}
