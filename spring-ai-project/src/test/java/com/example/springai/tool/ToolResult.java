package com.example.springai.tool;

/**
 * Represents the result of a tool execution
 */
public class ToolResult {
    private final boolean success;
    private final String message;

    public ToolResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static ToolResult success(String message) {
        return new ToolResult(true, message);
    }

    public static ToolResult failure(String message) {
        return new ToolResult(false, message);
    }
}
