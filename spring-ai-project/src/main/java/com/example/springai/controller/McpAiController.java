package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    private McpServer mcpServer;
    
    @Value("${spring.ai.mcp.server.transport.webmvc.path:/api/mcp}")
    private String mcpServerPath;
    
    /**
     * Process a chat message using MCP tool calling
     * @param message User's message
     * @return Response from the AI or tool execution
     */
    @GetMapping("/mcp-chat")
    public String mcpChat(@RequestParam String message) {
        String response;
        
        try {
            StringBuilder systemPrompt = new StringBuilder();
            systemPrompt.append("You are an AI assistant for project management. ");
            systemPrompt.append("You can use the following tools:\n\n");
            
            List<McpTool> tools = mcpServer.getTools();
            for (McpTool tool : tools) {
                systemPrompt.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
                systemPrompt.append("  Parameters: ");
                if (tool.getParameters() != null && !tool.getParameters().isEmpty()) {
                    systemPrompt.append(tool.getParameters().toString());
                } else {
                    systemPrompt.append("None");
                }
                systemPrompt.append("\n\n");
            }
            
            systemPrompt.append("When the user asks to perform an action, use the appropriate tool to help them.\n");
            systemPrompt.append("If no tool is appropriate, just respond conversationally.\n");
            
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemPrompt.toString()));
            messages.add(new UserMessage(message));
            
            Prompt prompt = new Prompt(messages);
            ChatResponse aiResponse = chatClient.call(prompt);
            
            response = aiResponse.getResult().getContent();
            
            System.out.println("DEBUG - LLM Response: " + response);
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
