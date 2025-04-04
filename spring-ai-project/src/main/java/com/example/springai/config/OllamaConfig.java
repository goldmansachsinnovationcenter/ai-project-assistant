package com.example.springai.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for Ollama chat client
 */
@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String baseUrl;

    @Value("${spring.ai.ollama.model}")
    private String model;

    @Value("${spring.ai.ollama.chat.options.temperature:0.7}")
    private double temperature;

    @Value("${spring.ai.ollama.chat.options.num_predict:1024}")
    private int numPredict;

    /**
     * Create a custom Ollama chat client with the configured model
     * @return ChatClient
     */
    @Bean
    @Primary
    public ChatClient ollamaChatClient() {
        System.out.println("Configuring Ollama with model: " + model);
        
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        
        ChatClient chatClient = new ChatClient() {
            @Override
            public ChatResponse call(Prompt prompt) {
                try {
                    System.out.println("Calling Ollama API with model: " + model);
                    
                    String url = baseUrl + "/api/chat";
                    
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    
                    ObjectNode requestBody = objectMapper.createObjectNode();
                    requestBody.put("model", model);
                    requestBody.put("stream", false);
                    
                    ObjectNode options = requestBody.putObject("options");
                    options.put("temperature", temperature);
                    options.put("num_predict", numPredict);
                    
                    List<Message> messages = prompt.getMessages();
                    requestBody.putArray("messages").addAll(
                        messages.stream()
                            .map(message -> {
                                ObjectNode msgNode = objectMapper.createObjectNode();
                                if (message instanceof UserMessage) {
                                    msgNode.put("role", "user");
                                } else {
                                    msgNode.put("role", "assistant");
                                }
                                msgNode.put("content", message.getContent());
                                return msgNode;
                            })
                            .toList()
                    );
                    
                    HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
                    
                    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                    
                    JsonNode responseJson = objectMapper.readTree(response.getBody());
                    String content = responseJson.path("message").path("content").asText();
                    
                    Generation generation = new Generation(new AssistantMessage(content).getContent());
                    return new ChatResponse(java.util.List.of(generation));
                } catch (Exception e) {
                    System.err.println("Error calling Ollama API: " + e.getMessage());
                    e.printStackTrace();
                    
                    Generation generation = new Generation(new AssistantMessage(
                        "I'm sorry, I encountered an error connecting to the Ollama API. " +
                        "Please make sure Ollama is running and the model '" + model + "' is available. " +
                        "Error: " + e.getMessage()
                    ).getContent());
                    return new ChatResponse(java.util.List.of(generation));
                }
            }
        };
        
        System.out.println("Successfully configured Ollama chat client");
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Model (from properties): " + model);
        
        return chatClient;
    }
}
