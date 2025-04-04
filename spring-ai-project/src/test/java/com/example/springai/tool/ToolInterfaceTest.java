package com.example.springai.tool;

import org.junit.jupiter.api.Test;
import com.example.springai.mcp.McpToolDefinition;
import com.example.springai.mcp.McpToolResult;
import com.example.springai.mcp.Tool;
import com.example.springai.mcp.ToolResult;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ToolInterfaceTest {

    @Test
    public void testToolImplementation() {
        Tool tool = new Tool() {
            @Override
            public String getName() {
                return "test-tool";
            }

            @Override
            public String getDescription() {
                return "Test tool description";
            }

            @Override
            public String getUri() {
                return "project-assistant/test-tool";
            }

            @Override
            public String[] getParameterNames() {
                return new String[]{"param1", "param2"};
            }

            @Override
            public ToolResult execute(Map<String, String> parameters) {
                if (parameters.containsKey("param1")) {
                    return ToolResult.success("Tool executed successfully with param1: " + parameters.get("param1"));
                } else {
                    return ToolResult.failure("Missing required parameter: param1");
                }
            }
        };
        
        assertEquals("test-tool", tool.getName());
        assertEquals("Test tool description", tool.getDescription());
        assertEquals("project-assistant/test-tool", tool.getUri());
        assertArrayEquals(new String[]{"param1", "param2"}, tool.getParameterNames());
        
        Map<String, String> validParams = new HashMap<>();
        validParams.put("param1", "value1");
        ToolResult successResult = tool.execute(validParams);
        assertTrue(successResult.isSuccess());
        assertTrue(successResult.getMessage().contains("value1"));
        
        ToolResult failureResult = tool.execute(Collections.emptyMap());
        assertFalse(failureResult.isSuccess());
        assertTrue(failureResult.getMessage().contains("Missing required parameter"));
    }
    
    @Test
    public void testMcpToolImplementation() {
        Tool tool = new Tool() {
            @Override
            public String getName() {
                return "test-mcp-tool";
            }

            @Override
            public String getDescription() {
                return "Test MCP tool description";
            }

            @Override
            public String getUri() {
                return "project-assistant/test-mcp-tool";
            }

            @Override
            public String[] getParameterNames() {
                return new String[]{"param1", "param2"};
            }

            @Override
            public ToolResult execute(Map<String, String> parameters) {
                if (parameters.containsKey("param1")) {
                    return ToolResult.success("Tool executed successfully with param1: " + parameters.get("param1"));
                } else {
                    return ToolResult.failure("Missing required parameter: param1");
                }
            }
        };
        
        McpToolDefinition toolDefinition = tool.getToolDefinition();
        assertNotNull(toolDefinition);
        assertEquals("test-mcp-tool", toolDefinition.getName());
        assertEquals("Test MCP tool description", toolDefinition.getDescription());
        assertEquals(2, toolDefinition.getParameters().size());
        assertTrue(toolDefinition.getParameters().stream().anyMatch(p -> p.getName().equals("param1")));
        assertTrue(toolDefinition.getParameters().stream().anyMatch(p -> p.getName().equals("param2")));
        
        Map<String, Object> mcpParams = new HashMap<>();
        mcpParams.put("param1", "value1");
        Map<String, String> stringParams = new HashMap<>();
        mcpParams.forEach((key, value) -> stringParams.put(key, value.toString()));
        
        ToolResult mcpSuccessResult = tool.execute(stringParams);
        assertTrue(mcpSuccessResult.isSuccess());
        assertEquals("Tool executed successfully with param1: value1", mcpSuccessResult.getMessage());
        
        ToolResult mcpFailureResult = tool.execute(Collections.emptyMap());
        assertFalse(mcpFailureResult.isSuccess());
        assertEquals("Missing required parameter: param1", mcpFailureResult.getMessage());
    }
}
