package com.example.springai.mcp;

/**
 * A generation from a chat client
 */
public class Generation {
    private final String content;
    private final Message output;
    
    /**
     * Create a new generation
     * @param content The content of the generation
     */
    public Generation(String content) {
        this.content = content;
        this.output = new AssistantMessage(content);
    }
    
    /**
     * Create a new generation
     * @param output The output message of the generation
     */
    public Generation(Message output) {
        this.content = output.getContent();
        this.output = output;
    }
    
    /**
     * Get the content of the generation
     * @return The content of the generation
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Get the output message of the generation
     * @return The output message of the generation
     */
    public Message getOutput() {
        return output;
    }
}
