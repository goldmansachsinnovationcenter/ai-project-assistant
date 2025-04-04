package com.example.springai.mcp;

/**
 * Result of an MCP tool execution
 */
public class McpToolResult {
    private boolean success;
    private String message;
    
    private McpToolResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public static McpToolResult success(String message) {
        return new McpToolResult(true, message);
    }
    
    public static McpToolResult failure(String message) {
        return new McpToolResult(false, message);
    }
}
