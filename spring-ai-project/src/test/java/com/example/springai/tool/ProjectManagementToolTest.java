package com.example.springai.tool;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class ProjectManagementToolTest {

    @Test
    public void testProjectManagementToolBase() {
        ProjectManagementTool tool = new ProjectManagementTool("test-tool", "Test tool description") {
            @Override
            public String[] getParameterNames() {
                return new String[]{"param1", "param2"};
            }

            @Override
            public ToolResult execute(Map<String, String> parameters) {
                return ToolResult.success("Tool executed successfully");
            }
        };
        
        assertEquals("test-tool", tool.getName());
        assertEquals("Test tool description", tool.getDescription());
        assertEquals("project-assistant/test-tool", tool.getUri());
        
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        ToolResult result = tool.execute(params);
        
        assertTrue(result.isSuccess());
        assertEquals("Tool executed successfully", result.getMessage());
    }
    
    @Test
    public void testUriGeneration() {
        ProjectManagementTool tool1 = new ProjectManagementTool("tool1", "Tool 1") {
            @Override
            public String[] getParameterNames() {
                return new String[]{};
            }

            @Override
            public ToolResult execute(Map<String, String> parameters) {
                return null;
            }
        };
        
        ProjectManagementTool tool2 = new ProjectManagementTool("tool2", "Tool 2") {
            @Override
            public String[] getParameterNames() {
                return new String[]{};
            }

            @Override
            public ToolResult execute(Map<String, String> parameters) {
                return null;
            }
        };
        
        assertEquals("project-assistant/tool1", tool1.getUri());
        assertEquals("project-assistant/tool2", tool2.getUri());
    }
}
