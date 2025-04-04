package com.example.springai.mcp;

/**
 * A message from the assistant
 */
public class AssistantMessage implements Message {
    private final String content;
    
    /**
     * Create a new assistant message
     * @param content The content of the message
     */
    public AssistantMessage(String content) {
        this.content = content;
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public String getRole() {
        return "assistant";
    }
}
