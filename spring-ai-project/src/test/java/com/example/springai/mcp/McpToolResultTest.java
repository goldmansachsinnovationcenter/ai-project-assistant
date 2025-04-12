package com.example.springai.mcp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class McpToolResultTest {

    @Test
    void success_CreatesSuccessfulResult() {
        McpToolResult result = McpToolResult.success("Operation successful");
        
        assertTrue(result.isSuccess());
        assertEquals("Operation successful", result.getMessage());
    }
    
    @Test
    void failure_CreatesFailedResult() {
        McpToolResult result = McpToolResult.failure("Operation failed");
        
        assertFalse(result.isSuccess());
        assertEquals("Operation failed", result.getMessage());
    }
    
    @Test
    void isSuccess_ReturnsTrueForSuccessfulResult() {
        McpToolResult result = McpToolResult.success("Test success");
        
        assertTrue(result.isSuccess());
    }
    
    @Test
    void isSuccess_ReturnsFalseForFailedResult() {
        McpToolResult result = McpToolResult.failure("Test failure");
        
        assertFalse(result.isSuccess());
    }
    
    @Test
    void getMessage_ReturnsCorrectMessageForSuccessfulResult() {
        String message = "Success message";
        McpToolResult result = McpToolResult.success(message);
        
        assertEquals(message, result.getMessage());
    }
    
    @Test
    void getMessage_ReturnsCorrectMessageForFailedResult() {
        String message = "Failure message";
        McpToolResult result = McpToolResult.failure(message);
        
        assertEquals(message, result.getMessage());
    }
}
