package com.example.springai.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.cohere.CohereChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class AIProviderService {
    
    private final OllamaChatModel ollamaChatModel;
    private final CohereChatModel cohereChatModel;
    
    public AIProviderService(OllamaChatModel ollamaChatModel, CohereChatModel cohereChatModel) {
        this.ollamaChatModel = ollamaChatModel;
        this.cohereChatModel = cohereChatModel;
    }
    
    public enum Provider {
        OLLAMA, COHERE
    }
    
    public ChatModel getChatModel(Provider provider) {
        return switch (provider) {
            case OLLAMA -> ollamaChatModel;
            case COHERE -> cohereChatModel;
        };
    }
}
