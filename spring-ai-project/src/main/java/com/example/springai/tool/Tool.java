package com.example.springai.tool;

import java.util.Map;

/**
 * Interface for all tools that can be called by the LLM
 */
public interface Tool {
    /**
     * Get the name of the tool
     * @return the tool name
     */
    String getName();
    
    /**
     * Get the description of the tool
     * @return the tool description
     */
    String getDescription();
    
    /**
     * Get the URI of the tool
     * @return the tool URI
     */
    String getUri();
    
    /**
     * Get the parameter names that this tool accepts
     * @return array of parameter names
     */
    String[] getParameterNames();
    
    /**
     * Execute the tool with the given parameters
     * @param parameters Map of parameter names to values
     * @return the result of the tool execution
     */
    ToolResult execute(Map<String, String> parameters);
}
