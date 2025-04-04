package com.example.springai.model;

import com.example.springai.mcp.Tool;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.stereotype.Component;

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
    public String createToolCallingPrompt(String userMessage, List<Tool> tools) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an AI assistant for project management. ");
        prompt.append("If the user is asking to perform any of the following actions, ");
        prompt.append("use the appropriate tool to help them.\n\n");
        
        prompt.append("Available tools:\n");
        for (Tool tool : tools) {
            prompt.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
            String[] paramNames = tool.getParameterNames();
            if (paramNames.length > 0) {
                prompt.append("  Parameters: ").append(String.join(", ", paramNames)).append("\n");
            }
        }
        
        prompt.append("\nUser message: ").append(userMessage);
        
        return prompt.toString();
    }
    
    /**
     * Create a Prompt object for MCP tool calling
     * @param userMessage User's message
     * @param mcpClient MCP client with available tools
     * @return Prompt object for the LLM
     */
    public Prompt createMcpPrompt(String userMessage, McpClient mcpClient) {
        String systemPrompt = "You are an AI assistant for project management. " +
            "If the user is asking to create, list, or show projects, add requirements, or prepare stories, " +
            "use the appropriate tool to help them.";
        
        return Prompt.builder()
                .withSystemMessage(systemPrompt)
                .withUserMessage(userMessage)
                .withTools(mcpClient.getTools())
                .build();
    }
    
    /**
     * Get a list of tool names from a list of tools
     * @param tools List of tools
     * @return Comma-separated list of tool names
     */
    public String getToolNamesList(List<Tool> tools) {
        return tools.stream()
                .map(Tool::getName)
                .collect(Collectors.joining(", "));
    }
}
