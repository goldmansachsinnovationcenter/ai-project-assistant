package com.example.springai.service;

import com.cohere.api.Cohere;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.ChatMessage;
import com.cohere.api.types.Message;
import com.cohere.api.types.NonStreamedChatResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.List;
import java.util.stream.Collectors;

public class CohereChatModel implements ChatModel {
    
    private final Cohere cohereClient;
    private final String model;
    
    public CohereChatModel(Cohere cohereClient, String model) {
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
            
            List<Message> chatHistory = messages.subList(0, Math.max(0, messages.size() - 1))
                    .stream()
                    .map(this::convertMessage)
                    .collect(Collectors.toList());
            
            ChatRequest request = ChatRequest.builder()
                    .message(currentMessage)
                    .stream(false)
                    .chatHistory(chatHistory)
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
    
    private Message convertMessage(org.springframework.ai.chat.messages.Message message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .message(message.getText())
                .build();
        
        return switch (message.getMessageType()) {
            case USER -> Message.user(chatMessage);
            case ASSISTANT -> Message.chatbot(chatMessage);
            default -> Message.user(chatMessage);
        };
    }
}
