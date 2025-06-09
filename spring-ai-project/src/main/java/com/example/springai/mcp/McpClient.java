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
        systemPrompt.append("Hi there! I'm your enthusiastic AI project management assistant! 🎯 ");
        systemPrompt.append("I'm here to make your project planning journey smooth, efficient, and enjoyable. ");
        systemPrompt.append("Let me show you all the wonderful tools I have at my disposal to help you succeed:\n\n");
        
        for (Tool tool : tools) {
            systemPrompt.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
            systemPrompt.append("  Parameters: ").append(String.join(", ", tool.getParameterNames())).append("\n\n");
        }
        
        systemPrompt.append("I'm always ready to help! When you need me to take action, I'll respond with the appropriate tool call. ");
        systemPrompt.append("Don't hesitate to ask questions or request assistance - I'm here to support you every step of the way! ");
        systemPrompt.append("If something isn't clear, just let me know and I'll gladly provide more guidance.\n\n");
        systemPrompt.append("Tool call format: {\"tool\": \"create-project\", \"parameters\": {\"name\": \"MyProject\", \"description\": \"A sample project\"}}\n\n");
        
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt.toString()));
        messages.add(new UserMessage(userMessage));
        
        return new Prompt(messages);
    }
}
