package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import com.example.springai.mcp.McpClient;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;

/**
 * Controller for handling AI chat with MCP tool calling
 */
@RestController
@RequestMapping("/api/ai")
public class McpClientController {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
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
    @GetMapping("/mcp-client-chat")
    public String mcpClientChat(@RequestParam String message) {
        String response;
        
        try {
            String systemPrompt = "You are an AI assistant for project management. " +
                "If the user is asking to create, list, or show projects, add requirements, or prepare stories, " +
                "use the appropriate tool to help them. " +
                "Available tools: create-project, list-projects, show-project, add-requirement, prepare-stories, help.";
            
            java.util.List<org.springframework.ai.chat.messages.Message> messages = new java.util.ArrayList<>();
            messages.add(new org.springframework.ai.chat.messages.SystemMessage(systemPrompt));
            messages.add(new org.springframework.ai.chat.messages.UserMessage(message));
            
            Prompt prompt = new Prompt(messages);
            
            ChatResponse aiResponse = chatClient.call(prompt);
            
            response = aiResponse.getResult().getOutput().getContent();
            
            saveChatMessage(message, response);
        } catch (Exception e) {
            response = "I'm sorry, I encountered an error processing your request. Please try again or use one of the available commands. Type 'help' to see the available commands.";
            System.err.println("Error in MCP client chat: " + e.getMessage());
            e.printStackTrace();
        }
        
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
