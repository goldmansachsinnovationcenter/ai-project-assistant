package com.example.springai.model;

import com.example.springai.mcp.Tool;
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
        prompt.append("respond with a JSON object containing the tool name and parameters.\n\n");
        
        prompt.append("Available tools:\n");
        for (Tool tool : tools) {
            prompt.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
            String[] paramNames = tool.getParameterNames();
            if (paramNames.length > 0) {
                prompt.append("  Parameters: ").append(String.join(", ", paramNames)).append("\n");
            }
        }
        
        prompt.append("\nResponse format example:\n");
        prompt.append("{\"tool\": \"tool-name\", \"parameters\": {\"param1\": \"value1\", \"param2\": \"value2\"}}\n\n");
        
        prompt.append("User message: ").append(userMessage);
        
        return prompt.toString();
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
    
    /**
     * Legacy method for backward compatibility
     * @param tools List of tools
     * @return Prompt template string
     */
    public static String createPrompt(List<Tool> tools) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an AI assistant for project management. You can use the following tools:\n\n");
        
        for (Tool tool : tools) {
            prompt.append("Tool: ").append(tool.getName()).append("\n");
            prompt.append("Description: ").append(tool.getDescription()).append("\n");
            prompt.append("Parameters: ").append(String.join(", ", tool.getParameterNames())).append("\n\n");
        }
        
        prompt.append("When a user asks you to perform an action related to project management, ");
        prompt.append("respond with a JSON object containing the tool name and parameters in the format: ");
        prompt.append("{\"tool\": \"tool-name\", \"parameters\": {\"param1\": \"value1\", \"param2\": \"value2\"}}\n\n");
        
        prompt.append("For example, to create a project named 'TestProject', use: ");
        prompt.append("{\"tool\": \"create-project\", \"parameters\": {\"name\": \"TestProject\"}}\n\n");
        
        prompt.append("If the user's request doesn't match any tool, respond conversationally as a helpful AI assistant. ");
        prompt.append("Be concise but informative in your responses.\n\n");
        
        return prompt.toString();
    }
}
