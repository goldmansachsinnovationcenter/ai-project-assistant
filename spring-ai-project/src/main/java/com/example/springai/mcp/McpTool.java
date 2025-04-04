package com.example.springai.mcp;

import java.util.Map;

/**
 * Interface for MCP tools that can be called by the LLM
 */
public interface McpTool {
    /**
     * Get the tool definition
     * @return the tool definition
     */
    McpToolDefinition getToolDefinition();
    
    /**
     * Execute the tool with the given parameters
     * @param parameters Map of parameter names to values
     * @return the result of the tool execution
     */
    McpToolResult execute(Map<String, Object> parameters);
}
