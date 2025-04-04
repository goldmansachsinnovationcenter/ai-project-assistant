package com.example.springai.mcp;

/**
 * A message in a chat conversation
 */
public interface Message {
    
    /**
     * Get the content of the message
     * @return The content of the message
     */
    String getContent();
    
    /**
     * Get the role of the message
     * @return The role of the message
     */
    String getRole();
}
