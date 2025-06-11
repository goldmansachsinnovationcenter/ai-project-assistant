package com.example.springai.service;

import com.cohere.api.CohereApiClient;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.NonStreamedChatResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.List;

public class CohereChatModel implements ChatModel {
    
    private final CohereApiClient cohereClient;
    private final String model;
    
    public CohereChatModel(CohereApiClient cohereClient, String model) {
        this.cohereClient = cohereClient;
        this.model = model != null ? model : "command-r";
    }
    
    @Override
    public ChatResponse call(Prompt prompt) {
        try {
            List<org.springframework.ai.chat.messages.Message> messages = prompt.getInstructions();
            if (messages.isEmpty()) {
                throw new IllegalArgumentException("No messages provided");
            }
            
            String currentMessage = messages.get(messages.size() - 1).getText();
            
            ChatRequest request = ChatRequest.builder()
                    .message(currentMessage)
                    .stream(false)
                    .model(model)
                    .build();
            
            NonStreamedChatResponse response = cohereClient.chat(request);
            
            String content = response.getText();
            AssistantMessage assistantMessage = new AssistantMessage(content);
            Generation generation = new Generation(assistantMessage);
            
            return new ChatResponse(List.of(generation));
        } catch (Exception e) {
            throw new RuntimeException("Error calling Cohere API: " + e.getMessage(), e);
        }
    }
}
