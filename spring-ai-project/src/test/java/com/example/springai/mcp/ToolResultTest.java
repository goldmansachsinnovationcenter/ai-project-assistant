package com.example.springai.mcp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToolResultTest {

    @Test
    public void testConstructor() {
        ToolResult result = new ToolResult(true, "Success message");
        
        assertTrue(result.isSuccess());
        assertEquals("Success message", result.getMessage());
    }
    
    @Test
    public void testSuccess() {
        ToolResult result = ToolResult.success("Operation completed successfully");
        
        assertTrue(result.isSuccess());
        assertEquals("Operation completed successfully", result.getMessage());
    }
    
    @Test
    public void testFailure() {
        ToolResult result = ToolResult.failure("Operation failed");
        
        assertFalse(result.isSuccess());
        assertEquals("Operation failed", result.getMessage());
    }
}
