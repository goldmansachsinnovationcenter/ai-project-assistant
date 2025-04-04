package com.example.springai.mcp;

import java.util.List;

/**
 * Response from a chat client
 */
public class ChatResponse {
    private final List<Generation> generations;
    
    /**
     * Create a new chat response
     * @param generations The generations in the response
     */
    public ChatResponse(List<Generation> generations) {
        this.generations = generations;
    }
    
    /**
     * Create a new chat response with a single generation
     * @param generation The generation in the response
     */
    public ChatResponse(Generation generation) {
        this(List.of(generation));
    }
    
    /**
     * Get the result of the chat response
     * @return The first generation in the response
     */
    public Generation getResult() {
        return generations.get(0);
    }
    
    /**
     * Get all generations in the response
     * @return The generations in the response
     */
    public List<Generation> getGenerations() {
        return generations;
    }
}
