package com.example.springai.mcp;

import org.springframework.stereotype.Component;

import com.example.springai.tool.Tool; // Updated import

import java.util.List;
import java.util.ArrayList;

/**
 * Simple MCP client implementation
 */
@Component
public class McpClient {
    private final List<Tool> tools;
    
    public McpClient(List<Tool> tools) {
        this.tools = tools;
    }
    
    public List<Tool> getTools() {
        return tools;
    }
    
    public Prompt createPrompt(String userMessage) {
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("You are an AI assistant for project management. ");
        systemPrompt.append("You can use the following tools:\n\n");
        
        for (Tool tool : tools) {
            systemPrompt.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
            systemPrompt.append("  Parameters: ").append(String.join(", ", tool.getParameterNames())).append("\n\n");
        }
        
        systemPrompt.append("When the user asks to perform an action, respond with a JSON object containing the tool name and parameters.\n");
        systemPrompt.append("Example: {\"tool\": \"create-project\", \"parameters\": {\"name\": \"MyProject\", \"description\": \"A sample project\"}}\n\n");
        
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt.toString()));
        messages.add(new UserMessage(userMessage));
        
        return new Prompt(messages);
    }
}
