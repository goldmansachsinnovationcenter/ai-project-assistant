package com.example.springai.config;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
     * @return OllamaChatClient
     */
    @Bean
    @Primary
    public OllamaChatClient ollamaChatClient() {
        System.out.println("Configuring Ollama with model: " + model);
        
        OllamaApi ollamaApi = new OllamaApi(baseUrl);
        
        OllamaChatClient chatClient = new OllamaChatClient(ollamaApi);
        
        System.out.println("Successfully configured Ollama chat client");
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Model (from properties): " + model);
        
        return chatClient;
    }
}
