package com.example.springai.mcp;

import java.util.ArrayList;
import java.util.List;

/**
 * A prompt to send to a chat client
 */
public class Prompt {
    private final List<Message> messages;
    
    /**
     * Create a new prompt with a single message
     * @param message The message to include in the prompt
     */
    public Prompt(String message) {
        this.messages = new ArrayList<>();
        this.messages.add(new UserMessage(message));
    }
    
    /**
     * Create a new prompt with a list of messages
     * @param messages The messages to include in the prompt
     */
    public Prompt(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }
    
    /**
     * Get the messages in the prompt
     * @return The messages in the prompt
     */
    public List<Message> getMessages() {
        return messages;
    }
}
