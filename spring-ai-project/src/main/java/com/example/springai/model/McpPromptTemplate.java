package com.example.springai.model;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpTool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for creating MCP-compatible prompts
 */
@Component
public class McpPromptTemplate {
    
    /**
     * Create a prompt template for tool calling
     * @param userMessage User's message
     * @param tools List of available tools
     * @return Prompt template string
     */
    public String createToolCallingPrompt(String userMessage, List<McpTool> tools) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an AI assistant for project management. ");
        prompt.append("If the user is asking to perform any of the following actions, ");
        prompt.append("use the appropriate tool to help them.\n\n");
        
        prompt.append("Available tools:\n");
        for (McpTool tool : tools) {
            prompt.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
            if (tool.getParameters() != null && !tool.getParameters().isEmpty()) {
                prompt.append("  Parameters: ").append(tool.getParameters().toString()).append("\n");
            }
        }
        
        prompt.append("\nUser message: ").append(userMessage);
        
        return prompt.toString();
    }
    
    /**
     * Create a Prompt object for MCP tool calling
     * @param userMessage User's message
     * @param mcpServer MCP server with available tools
     * @return Prompt object for the LLM
     */
    public Prompt createMcpPrompt(String userMessage, McpServer mcpServer) {
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
        
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt.toString()));
        messages.add(new UserMessage(userMessage));
        
        return new Prompt(messages);
    }
    
    /**
     * Get a list of tool names from a list of tools
     * @param tools List of tools
     * @return Comma-separated list of tool names
     */
    public String getToolNamesList(List<McpTool> tools) {
        return tools.stream()
                .map(McpTool::getName)
                .collect(Collectors.joining(", "));
    }
}
