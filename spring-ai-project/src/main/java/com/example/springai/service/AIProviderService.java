package com.example.springai.service;

import org.springframework.ai.chat.model.ChatModel;
import com.cohere.api.Cohere;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIProviderService {
    
    private final OllamaChatModel ollamaChatModel;
    private final Cohere cohereClient;
    private final CohereChatModel cohereChatModel;
    
    public AIProviderService(OllamaChatModel ollamaChatModel, 
                           Cohere cohereClient,
                           @Value("${spring.ai.cohere.model:command-r}") String cohereModel) {
        this.ollamaChatModel = ollamaChatModel;
        this.cohereClient = cohereClient;
        this.cohereChatModel = new CohereChatModel(cohereClient, cohereModel);
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

    public Cohere getCohereClient() {
        return cohereClient;
    }
}
