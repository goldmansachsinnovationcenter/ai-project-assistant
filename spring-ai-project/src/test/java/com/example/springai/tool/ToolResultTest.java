package com.example.springai.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToolResultTest {

    @Test
    public void testSuccessResult() {
        ToolResult result = ToolResult.success("Test success message");
        
        assertTrue(result.isSuccess());
        assertEquals("Test success message", result.getMessage());
    }
    
    @Test
    public void testFailureResult() {
        ToolResult result = ToolResult.failure("Test failure message");
        
        assertFalse(result.isSuccess());
        assertEquals("Test failure message", result.getMessage());
    }
    
    @Test
    public void testConstructor() {
        ToolResult result = ToolResult.success("Test message");
        
        assertTrue(result.isSuccess());
        assertEquals("Test message", result.getMessage());
    }
    
    @Test
    public void testToString() {
        ToolResult successResult = ToolResult.success("Success message");
        ToolResult failureResult = ToolResult.failure("Failure message");
        
        String successString = successResult.toString();
        String failureString = failureResult.toString();
        
        assertNotNull(successString);
        assertNotNull(failureString);
        assertTrue(successString.length() > 0);
        assertTrue(failureString.length() > 0);
    }
}
