package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for MCP tool calling functionality using Spring AI MCP server
 * This replaces the previous regex-based implementation with proper LLM-based tool calling
 */
@RestController
@RequestMapping("/api/ai/mcp-chat")
public class SimulatedMcpController {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private McpServer mcpServer;
    
    /**
     * Process a chat message using MCP tool calling
     * @param message User's message
     * @return Response from the AI or tool execution
     */
    @GetMapping("/chat")
    public String mcpChat(@RequestParam String message) {
        String response;
        
        try {
            StringBuilder systemPrompt = new StringBuilder();
            systemPrompt.append("You are an AI assistant for project management. ");
            systemPrompt.append("You can help users create and manage projects, add requirements, and generate user stories. ");
            systemPrompt.append("You can use the following tools:\n\n");
            
            mcpServer.getTools().forEach(tool -> {
                systemPrompt.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
                systemPrompt.append("  Parameters: ");
                if (tool.getParameters() != null && !tool.getParameters().isEmpty()) {
                    systemPrompt.append(tool.getParameters().toString());
                } else {
                    systemPrompt.append("None");
                }
                systemPrompt.append("\n\n");
            });
            
            systemPrompt.append("When the user asks to perform an action, use the appropriate tool to help them.\n");
            systemPrompt.append("If no tool is appropriate, just respond conversationally.\n");
            systemPrompt.append("Examples of commands users might ask:\n");
            systemPrompt.append("- Create a new project called ProjectName\n");
            systemPrompt.append("- List all projects\n");
            systemPrompt.append("- Show details of project ProjectName\n");
            systemPrompt.append("- Add requirement RequirementText to project ProjectName\n");
            
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemPrompt.toString()));
            messages.add(new UserMessage(message));
            
            Prompt prompt = new Prompt(messages);
            ChatResponse aiResponse = chatClient.call(prompt);
            
            response = aiResponse.getResult().getContent();
            
            System.out.println("DEBUG - LLM Response: " + response);
        } catch (Exception e) {
            response = "I'm sorry, I encountered an error processing your request. Please try again or use one of the available commands.";
            System.err.println("Error in MCP chat: " + e.getMessage());
            e.printStackTrace();
        }
        
        saveChatMessage(message, response);
        
        return response;
    }
    
    private void saveChatMessage(String prompt, String response) {
        ChatMessage chatMessage = new ChatMessage(prompt, response);
        chatMessageRepository.save(chatMessage);
    }
}
