package com.example.springai.model;

import com.example.springai.tool.Tool; // Updated import
import com.example.springai.mcp.McpClient; // Keep specific MCP imports
import com.example.springai.mcp.Message;
import com.example.springai.mcp.Prompt;
import com.example.springai.mcp.SystemMessage;
import com.example.springai.mcp.UserMessage;
import java.util.ArrayList;
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
        String systemPrompt = "Hello! I'm your friendly AI assistant, here to help you with project management! 😊 " +
            "I'm excited to work with you and make your project planning experience smooth and enjoyable. " +
            "Whether you need to create projects, organize requirements, or generate user stories, I'm here to assist you every step of the way.\n\n" +
            "I can help you with these tools:\n";
            
        for (Tool tool : mcpClient.getTools()) {
            systemPrompt += "- " + tool.getName() + ": " + tool.getDescription() + "\n";
            String[] paramNames = tool.getParameterNames();
            if (paramNames.length > 0) {
                systemPrompt += "  Parameters: " + String.join(", ", paramNames) + "\n";
            }
        }
        
        systemPrompt += "\nI'm always happy to help! When you ask me to perform an action, I'll use the appropriate tool to assist you. " +
            "Feel free to ask me anything about your projects - I'm here to make your work easier and more organized! " +
            "If you're ever unsure about what I can do, just ask for help and I'll gladly explain all my capabilities.\n\n" +
            "Remember: I respond with JSON for tool calls like this example: " +
            "{\"tool\": \"create-project\", \"parameters\": {\"name\": \"MyProject\", \"description\": \"A sample project\"}}\n\n";
        
        java.util.List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        messages.add(new UserMessage(userMessage));
        
        return new Prompt(messages);
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
