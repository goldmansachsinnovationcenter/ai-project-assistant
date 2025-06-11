package com.example.springai.config;

import org.springframework.ai.cohere.CohereChatModel;
import org.springframework.ai.cohere.api.CohereApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohereConfig {
    
    @Value("${spring.ai.cohere.api-key}")
    private String apiKey;
    
    @Value("${spring.ai.cohere.model:command-r}")
    private String model;
    
    @Bean
    public CohereChatModel cohereChatModel() {
        return new CohereChatModel(new CohereApi(apiKey));
    }
}
