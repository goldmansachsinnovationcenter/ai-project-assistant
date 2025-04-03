package com.example.springai.tool;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ToolResultTest {

    @Test
    public void testSuccessResult() {
        ToolResult result = ToolResult.success("Operation successful");
        
        assertTrue(result.isSuccess());
        assertEquals("Operation successful", result.getMessage());
    }
    
    @Test
    public void testFailureResult() {
        ToolResult result = ToolResult.failure("Operation failed");
        
        assertFalse(result.isSuccess());
        assertEquals("Operation failed", result.getMessage());
    }
    
    @Test
    public void testConstructor() {
        ToolResult successResult = new ToolResult(true, "Success message");
        ToolResult failureResult = new ToolResult(false, "Failure message");
        
        assertTrue(successResult.isSuccess());
        assertEquals("Success message", successResult.getMessage());
        
        assertFalse(failureResult.isSuccess());
        assertEquals("Failure message", failureResult.getMessage());
    }
}
