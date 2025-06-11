package com.example.springai.config;

import com.cohere.api.Cohere;
import com.cohere.api.core.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohereConfig {

    @Value("${spring.ai.cohere.api-key}")
    private String apiKey;

    @Bean
    public Cohere cohereClient() {
        return Cohere.builder()
                .token(apiKey)
                .environment(Environment.PRODUCTION)
                .clientName("ai-project-assistant")
                .build();
    }
}
