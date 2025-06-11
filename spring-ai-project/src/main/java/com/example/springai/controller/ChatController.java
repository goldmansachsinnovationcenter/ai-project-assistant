package com.example.springai.controller;

import com.example.springai.mcp.McpClient;
import com.example.springai.mcp.Prompt;
import com.example.springai.service.AIProviderService;
import com.example.springai.service.McpToolService;
import com.example.springai.tools.ProjectManagementTools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siva.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final OllamaChatModel chatModel;
    private final ProjectManagementTools projectManagementTools;
    private final AIProviderService aiProviderService;
    private final McpClient mcpClient;
    private final McpToolService mcpToolService;
    private final ObjectMapper objectMapper;

    public ChatController(OllamaChatModel chatModel, 
                         ProjectManagementTools projectManagementTools,
                         AIProviderService aiProviderService,
                         McpClient mcpClient,
                         McpToolService mcpToolService) {
        this.chatModel = chatModel;
        this.projectManagementTools = projectManagementTools;
        this.aiProviderService = aiProviderService;
        this.mcpClient = mcpClient;
        this.mcpToolService = mcpToolService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/ai/chat")
    public Map<String, String> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
            @RequestParam(value = "provider", defaultValue = "OLLAMA") String providerName) {

        try {
            AIProviderService.Provider provider = AIProviderService.Provider.valueOf(providerName.toUpperCase());
            ChatModel selectedChatModel = aiProviderService.getChatModel(provider);
            
            String response = ChatClient.create(selectedChatModel)
                    .prompt(message)
                    .tools(new DateTimeTools(), projectManagementTools)
                    .call()
                    .content();

            System.out.println(response);

            return Map.of("generation", response != null ? response : "No Response", "provider", providerName);
        } catch (IllegalArgumentException e) {
            return Map.of("generation", "Invalid provider: " + providerName, "provider", providerName);
        }
    }

    @GetMapping("/ai/mcp-chat")
    public Map<String, String> mcpChat(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "provider", defaultValue = "COHERE") String providerName) {
        
        try {
            AIProviderService.Provider provider = AIProviderService.Provider.valueOf(providerName.toUpperCase());
            ChatModel selectedChatModel = aiProviderService.getChatModel(provider);
            
            Prompt prompt = mcpClient.createPrompt(message);
            String response = ChatClient.create(selectedChatModel)
                    .prompt(prompt.toString())
                    .call()
                    .content();
            
            return processToolResponse(response, message);
        } catch (IllegalArgumentException e) {
            return Map.of("generation", "Invalid provider: " + providerName, "provider", providerName);
        } catch (Exception e) {
            return Map.of("generation", "Error processing request: " + e.getMessage(), "provider", providerName);
        }
    }
    
    private Map<String, String> processToolResponse(String response, String originalMessage) {
        try {
            if (response.contains("{") && response.contains("tool")) {
                JsonNode jsonNode = objectMapper.readTree(response);
                if (jsonNode.has("tool") && jsonNode.has("parameters")) {
                    String toolName = jsonNode.get("tool").asText();
                    JsonNode parametersNode = jsonNode.get("parameters");
                    
                    Map<String, String> parameters = objectMapper.convertValue(parametersNode, Map.class);
                    var toolResult = mcpToolService.executeTool(toolName, parameters);
                    
                    return Map.of("generation", toolResult.getMessage(), "tool_executed", toolName);
                }
            }
            
            return Map.of("generation", response);
        } catch (Exception e) {
            return Map.of("generation", response);
        }
    }
}
