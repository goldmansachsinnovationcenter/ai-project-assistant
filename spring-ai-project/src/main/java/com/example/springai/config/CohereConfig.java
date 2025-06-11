package com.example.springai.config;

import com.cohere.api.CohereApiClient;
import com.cohere.api.core.ClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohereConfig {

    @Value("${spring.ai.cohere.api-key}")
    private String apiKey;

    @Bean
    public CohereApiClient cohereClient() {
        ClientOptions clientOptions = ClientOptions.builder()
                .apiKey(apiKey)
                .build();
        return new CohereApiClient(clientOptions);
    }
}
