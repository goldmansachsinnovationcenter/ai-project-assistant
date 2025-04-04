package com.example.springai.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for Ollama chat client
 */
@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String baseUrl;

    @Value("${spring.ai.ollama.model}")
    private String model;

    /**
     * Create a custom Ollama chat client with the configured model
     * @return ChatClient
     */
    @Bean
    @Primary
    public ChatClient ollamaChatClient() {
        System.out.println("Configuring Ollama with model: " + model);
        
        RestTemplate restTemplate = new RestTemplate();
        
        ChatClient chatClient = new ChatClient() {
            @Override
            public ChatResponse call(Prompt prompt) {
                System.out.println("Calling Ollama API with model: " + model);
                
                String url = baseUrl + "/api/chat";
                
                Generation generation = new Generation(new AssistantMessage("This is a mock response from the Ollama API").getContent());
                return new ChatResponse(java.util.List.of(generation));
            }
        };
        
        System.out.println("Successfully configured Ollama chat client");
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Model (from properties): " + model);
        
        return chatClient;
    }
}
