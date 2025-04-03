package com.example.springai.tool;

/**
 * Result of a tool execution
 */
public class ToolResult {
    private final boolean success;
    private final String message;
    
    private ToolResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    /**
     * Create a successful result
     * @param message Success message
     * @return ToolResult with success=true
     */
    public static ToolResult success(String message) {
        return new ToolResult(true, message);
    }
    
    /**
     * Create a failure result
     * @param message Error message
     * @return ToolResult with success=false
     */
    public static ToolResult failure(String message) {
        return new ToolResult(false, message);
    }
    
    /**
     * Check if the result is successful
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Get the result message
     * @return Result message
     */
    public String getMessage() {
        return message;
    }
}
