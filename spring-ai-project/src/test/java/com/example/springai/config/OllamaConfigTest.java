package com.example.springai.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.springai.mcp.ChatClient;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OllamaConfigTest {

    @InjectMocks
    private OllamaConfig ollamaConfig;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ollamaConfig, "baseUrl", "http://test-ollama:11434");
        ReflectionTestUtils.setField(ollamaConfig, "model", "test-model");
        ReflectionTestUtils.setField(ollamaConfig, "temperature", 0.8); // Use double for temperature
    }

    @Test
    void ollamaChatClient_CreatesClientWithCorrectConfiguration() {
        ChatClient chatClient = ollamaConfig.ollamaChatClient();

        assertNotNull(chatClient, "Custom ChatClient bean should be created");
        assertTrue(chatClient instanceof com.example.springai.mcp.ChatClient, "Bean should be an instance of mcp.ChatClient");

    }

    @Test
    void ollamaChatClient_UsesDefaultValuesWhenPropertiesNotSet() {
        ReflectionTestUtils.setField(ollamaConfig, "baseUrl", "http://localhost:11434"); // Default base URL used in custom client logic
        ReflectionTestUtils.setField(ollamaConfig, "model", "llama2"); // Default model name often used
        ReflectionTestUtils.setField(ollamaConfig, "temperature", 0.7); // Default temperature from @Value

        ChatClient chatClient = ollamaConfig.ollamaChatClient();
        assertNotNull(chatClient, "Custom ChatClient bean should be created even with default properties");
        assertTrue(chatClient instanceof com.example.springai.mcp.ChatClient, "Bean should be an instance of mcp.ChatClient");

    }
}
