package com.example.springai.service;

import org.springframework.ai.chat.model.ChatModel;
import com.cohere.api.CohereApiClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class AIProviderService {
    
    private final OllamaChatModel ollamaChatModel;
    private final CohereApiClient cohereClient;
    
    public AIProviderService(OllamaChatModel ollamaChatModel, CohereApiClient cohereClient) {
        this.ollamaChatModel = ollamaChatModel;
        this.cohereClient = cohereClient;
    }
    
    public enum Provider {
        OLLAMA, COHERE
    }
    
    public ChatModel getChatModel(Provider provider) {
        return switch (provider) {
            case OLLAMA -> ollamaChatModel;
            case COHERE -> throw new UnsupportedOperationException("Cohere integration requires custom implementation");
        };
    }

    public CohereApiClient getCohereClient() {
        return cohereClient;
    }
}
