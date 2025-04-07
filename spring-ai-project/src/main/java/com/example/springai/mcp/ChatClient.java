package com.example.springai.mcp;

/**
 * Interface for chat clients that can process prompts and return responses
 */
public interface ChatClient {
    
    /**
     * Call the chat client with a prompt
     * @param prompt The prompt to send to the chat client
     * @return The response from the chat client
     */
    ChatResponse call(Prompt prompt);
    
    /**
     * Get the tools available to this chat client
     * @return The list of tools
     */
    default java.util.List<Tool> getTools() {
        return java.util.Collections.emptyList();
    }
}
