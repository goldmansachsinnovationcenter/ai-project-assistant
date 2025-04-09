package com.example.springai.mcp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectManagementToolTest {

    private static class TestTool extends ProjectManagementTool {
        public TestTool(String name, String description) {
            super(name, description);
        }
        
        @Override
        public ToolResult execute(java.util.Map<String, String> parameters) {
            return ToolResult.success("Test execution");
        }
        
        @Override
        public String[] getParameterNames() {
            return new String[]{"param1", "param2"};
        }
    }
    
    @Test
    public void testConstructor() {
        ProjectManagementTool tool = new TestTool("test-tool", "Test tool description");
        
        assertEquals("test-tool", tool.getName());
        assertEquals("Test tool description", tool.getDescription());
        assertEquals("project-assistant/test-tool", tool.getUri());
    }
    
    @Test
    public void testGetName() {
        ProjectManagementTool tool = new TestTool("test-tool", "Test tool description");
        
        assertEquals("test-tool", tool.getName());
    }
    
    @Test
    public void testGetDescription() {
        ProjectManagementTool tool = new TestTool("test-tool", "Test tool description");
        
        assertEquals("Test tool description", tool.getDescription());
    }
    
    @Test
    public void testGetUri() {
        ProjectManagementTool tool = new TestTool("test-tool", "Test tool description");
        
        assertEquals("project-assistant/test-tool", tool.getUri());
    }
}
