package com.example.springai.mcp;

/**
 * A message from the system
 */
public class SystemMessage implements Message {
    private final String content;
    
    /**
     * Create a new system message
     * @param content The content of the message
     */
    public SystemMessage(String content) {
        this.content = content;
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public String getRole() {
        return "system";
    }
}
