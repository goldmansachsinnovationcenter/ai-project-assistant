package com.example.springai.model;

import com.example.springai.mcp.Tool;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.server.tool.McpTool;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class McpPromptTemplateTest {

    @Test
    public void testCreatePrompt() {
        McpPromptTemplate template = new McpPromptTemplate();
        
        Tool tool1 = mock(Tool.class);
        when(tool1.getName()).thenReturn("tool1");
        when(tool1.getDescription()).thenReturn("Tool 1 description");
        when(tool1.getParameterNames()).thenReturn(new String[]{"param1", "param2"});
        
        Tool tool2 = mock(Tool.class);
        when(tool2.getName()).thenReturn("tool2");
        when(tool2.getDescription()).thenReturn("Tool 2 description");
        when(tool2.getParameterNames()).thenReturn(new String[]{"param3"});
        
        List<Tool> tools = Arrays.asList(tool1, tool2);
        String userMessage = "Test message";
        
        String prompt = template.createPrompt(tools, userMessage);
        
        assertNotNull(prompt);
        assertTrue(prompt.contains("You are an AI assistant for project management"));
        assertTrue(prompt.contains("tool1"));
        assertTrue(prompt.contains("Tool 1 description"));
        assertTrue(prompt.contains("param1, param2"));
        assertTrue(prompt.contains("tool2"));
        assertTrue(prompt.contains("Tool 2 description"));
        assertTrue(prompt.contains("param3"));
        assertTrue(prompt.contains("Test message"));
    }
    
    @Test
    public void testCreateMcpPrompt() {
        McpPromptTemplate template = new McpPromptTemplate();
        
        McpClient mcpClient = mock(McpClient.class);
        List<McpTool> mcpTools = Arrays.asList(
                mock(McpTool.class),
                mock(McpTool.class)
        );
        when(mcpClient.getTools()).thenReturn(mcpTools);
        
        String userMessage = "Create project TestProject";
        
        Prompt mcpPrompt = template.createMcpPrompt(userMessage, mcpClient);
        
        assertNotNull(mcpPrompt);
        assertEquals(userMessage, mcpPrompt.getUserMessages().get(0).getContent());
        assertTrue(mcpPrompt.getSystemMessages().get(0).getContent().contains("You are an AI assistant for project management"));
        assertNotNull(mcpPrompt.getOptions());
        assertTrue(mcpPrompt.getOptions().containsKey("tools"));
        assertEquals(mcpTools, mcpPrompt.getOptions().get("tools"));
    }
    
    @Test
    public void testGetToolNames() {
        McpPromptTemplate template = new McpPromptTemplate();
        
        Tool tool1 = mock(Tool.class);
        when(tool1.getName()).thenReturn("tool1");
        
        Tool tool2 = mock(Tool.class);
        when(tool2.getName()).thenReturn("tool2");
        
        List<Tool> tools = Arrays.asList(tool1, tool2);
        
        String toolNames = template.getToolNames(tools);
        
        assertEquals("tool1, tool2", toolNames);
    }
}
