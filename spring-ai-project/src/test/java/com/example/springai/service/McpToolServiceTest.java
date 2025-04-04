package com.example.springai.service;

import com.example.springai.mcp.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import org.springframework.ai.mcp.server.tool.McpTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class McpToolServiceTest {

    private McpToolService mcpToolService;
    private CreateProjectTool createProjectTool;
    private ListProjectsTool listProjectsTool;
    private ShowProjectTool showProjectTool;
    private AddRequirementTool addRequirementTool;
    private PrepareStoriesTool prepareStoriesTool;
    private HelpTool helpTool;

    @BeforeEach
    public void setup() {
        createProjectTool = mock(CreateProjectTool.class);
        listProjectsTool = mock(ListProjectsTool.class);
        showProjectTool = mock(ShowProjectTool.class);
        addRequirementTool = mock(AddRequirementTool.class);
        prepareStoriesTool = mock(PrepareStoriesTool.class);
        helpTool = mock(HelpTool.class);
        
        when(createProjectTool.getName()).thenReturn("create-project");
        when(listProjectsTool.getName()).thenReturn("list-projects");
        when(showProjectTool.getName()).thenReturn("show-project");
        when(addRequirementTool.getName()).thenReturn("add-requirement");
        when(prepareStoriesTool.getName()).thenReturn("prepare-stories");
        when(helpTool.getName()).thenReturn("help");
        
        mcpToolService = new McpToolService(
                createProjectTool,
                listProjectsTool,
                showProjectTool,
                addRequirementTool,
                prepareStoriesTool,
                helpTool
        );
    }

    @Test
    public void testGetToolByName() {
        Tool tool = mcpToolService.getToolByName("create-project");
        assertNotNull(tool);
        assertEquals("create-project", tool.getName());
        assertEquals(createProjectTool, tool);
        
        tool = mcpToolService.getToolByName("list-projects");
        assertNotNull(tool);
        assertEquals("list-projects", tool.getName());
        assertEquals(listProjectsTool, tool);
        
        tool = mcpToolService.getToolByName("show-project");
        assertNotNull(tool);
        assertEquals("show-project", tool.getName());
        assertEquals(showProjectTool, tool);
        
        tool = mcpToolService.getToolByName("add-requirement");
        assertNotNull(tool);
        assertEquals("add-requirement", tool.getName());
        assertEquals(addRequirementTool, tool);
        
        tool = mcpToolService.getToolByName("prepare-stories");
        assertNotNull(tool);
        assertEquals("prepare-stories", tool.getName());
        assertEquals(prepareStoriesTool, tool);
        
        tool = mcpToolService.getToolByName("help");
        assertNotNull(tool);
        assertEquals("help", tool.getName());
        assertEquals(helpTool, tool);
        
        tool = mcpToolService.getToolByName("non-existent-tool");
        assertNull(tool);
    }

    @Test
    public void testExecuteTool() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "TestProject");
        
        ToolResult expectedResult = ToolResult.success("Project created successfully");
        when(createProjectTool.execute(parameters)).thenReturn(expectedResult);
        
        ToolResult result = mcpToolService.executeTool("create-project", parameters);
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(createProjectTool).execute(parameters);
    }

    @Test
    public void testExecuteNonExistentTool() {
        Map<String, String> parameters = new HashMap<>();
        
        ToolResult result = mcpToolService.executeTool("non-existent-tool", parameters);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Tool not found"));
    }
    
    @Test
    public void testGetMcpTools() {
        List<Tool> mcpTools = mcpToolService.getTools();
        
        assertNotNull(mcpTools);
        assertEquals(6, mcpTools.size());
        
        assertTrue(mcpTools.contains(createProjectTool));
        assertTrue(mcpTools.contains(listProjectsTool));
        assertTrue(mcpTools.contains(showProjectTool));
        assertTrue(mcpTools.contains(addRequirementTool));
        assertTrue(mcpTools.contains(prepareStoriesTool));
        assertTrue(mcpTools.contains(helpTool));
    }
}
