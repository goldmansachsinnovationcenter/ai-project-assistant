package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.client.McpClientBuilder;
import org.springframework.ai.mcp.client.transport.webmvc.WebMvcMcpClientTransport;
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
    private OllamaChatClient chatClient;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Value("${spring.ai.mcp.server.transport.webmvc.path:/api/mcp}")
    private String mcpServerPath;
    
    private McpClient mcpClient;
    
    /**
     * Initialize the MCP client
     */
    @PostConstruct
    public void init() {
        WebMvcMcpClientTransport transport = new WebMvcMcpClientTransport(mcpServerPath);
        mcpClient = McpClientBuilder.builder()
                .withTransport(transport)
                .build();
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
            
            Prompt prompt = Prompt.builder()
                    .withSystemMessage(systemPrompt)
                    .withUserMessage(message)
                    .withTools(mcpClient.getTools())
                    .build();
            
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
