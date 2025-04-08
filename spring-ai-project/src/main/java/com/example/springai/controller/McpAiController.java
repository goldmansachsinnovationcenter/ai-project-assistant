package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.model.McpPromptTemplate;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.mcp.*;
import com.example.springai.service.McpToolService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling AI chat with MCP tool calling
 */
@RestController
@RequestMapping("/api/ai")
public class McpAiController {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private McpPromptTemplate promptTemplate;
    
    @Autowired
    private McpToolService mcpToolService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${spring.ai.mcp.server.transport.webmvc.path:/api/mcp}")
    private String mcpServerPath;
    
    private McpClient mcpClient;
    
    /**
     * MCP client is autowired from McpConfig
     */
    @Autowired
    public void setMcpClient(McpClient mcpClient) {
        this.mcpClient = mcpClient;
    }
    
    /**
     * Process a chat message using MCP tool calling
     * @param message User's message
     * @return Response from the AI or tool execution
     */
    @GetMapping("/mcp-chat")
    public String mcpChat(@RequestParam String message) {
        String response;
        
        try {
            Prompt prompt = promptTemplate.createMcpPrompt(message, mcpClient);
            
            ChatResponse aiResponse = chatClient.call(prompt);
            
            response = aiResponse.getResult().getContent();
            
            System.out.println("DEBUG - LLM Response: " + response);
            
            try {
                System.out.println("DEBUG - Attempting to parse response as JSON: " + response);
                JsonNode jsonNode = objectMapper.readTree(response);
                System.out.println("DEBUG - Successfully parsed JSON: " + jsonNode);
                
                if (jsonNode.has("tool")) {
                    System.out.println("DEBUG - Found tool field: " + jsonNode.get("tool").asText());
                    
                    if ("list-projects".equals(jsonNode.get("tool").asText())) {
                        System.out.println("DEBUG - Tool is list-projects, executing tool");
                        String toolName = jsonNode.get("tool").asText();
                        Map<String, String> parameters = new HashMap<>();
                        if (jsonNode.has("parameters") && jsonNode.get("parameters").isObject()) {
                            JsonNode paramsNode = jsonNode.get("parameters");
                            paramsNode.fields().forEachRemaining(entry -> 
                                parameters.put(entry.getKey(), entry.getValue().asText()));
                        }
                        
                        System.out.println("DEBUG - Executing tool: " + toolName + " with parameters: " + parameters);
                        ToolResult toolResult = mcpToolService.executeTool(toolName, parameters);
                        System.out.println("DEBUG - Tool execution result: " + toolResult.getMessage());
                        
                        response = toolResult.getMessage();
                        System.out.println("DEBUG - Updated response: " + response);
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to parse LLM response as JSON: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            response = "I'm sorry, I encountered an error processing your request. Please try again or use one of the available commands. Type 'help' to see the available commands.";
            System.err.println("Error in MCP AI chat: " + e.getMessage());
            e.printStackTrace();
        }
        
        saveChatMessage(message, response);
        
        return response;
    }
    
    /**
     * Save a chat message to the repository
     * @param prompt User's message
     * @param response AI's response
     */
    private void saveChatMessage(String prompt, String response) {
        ChatMessage chatMessage = new ChatMessage(prompt, response);
        chatMessageRepository.save(chatMessage);
    }
}
