package com.example.springai.tool;

import org.junit.jupiter.api.Test;
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
}
