package com.example.springai.mcp;

/**
 * A message from the user
 */
public class UserMessage implements Message {
    private final String content;
    
    /**
     * Create a new user message
     * @param content The content of the message
     */
    public UserMessage(String content) {
        this.content = content;
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public String getRole() {
        return "user";
    }
}
