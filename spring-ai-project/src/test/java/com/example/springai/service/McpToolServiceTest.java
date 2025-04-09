package com.example.springai.service;

import com.example.springai.mcp.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class McpToolServiceTest {

    @Mock
    private CreateProjectTool createProjectTool;

    @Mock
    private ListProjectsTool listProjectsTool;

    @Mock
    private ShowProjectTool showProjectTool;

    @Mock
    private AddRequirementTool addRequirementTool;

    @Mock
    private PrepareStoriesTool prepareStoriesTool;

    @Mock
    private HelpTool helpTool;

    private McpToolService mcpToolService;
    
    @BeforeEach
    public void setup() {
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
    public void testGetAllTools() {
        List<Tool> tools = mcpToolService.getAllTools();
        
        assertNotNull(tools);
        assertEquals(6, tools.size());
        assertTrue(tools.contains(createProjectTool));
        assertTrue(tools.contains(listProjectsTool));
        assertTrue(tools.contains(showProjectTool));
        assertTrue(tools.contains(addRequirementTool));
        assertTrue(tools.contains(prepareStoriesTool));
        assertTrue(tools.contains(helpTool));
    }

    @Test
    public void testGetToolByName() {
        when(createProjectTool.getName()).thenReturn("create-project");
        
        Tool tool = mcpToolService.getToolByName("create-project");
        
        assertNotNull(tool);
        assertEquals(createProjectTool, tool);
    }

    @Test
    public void testGetToolByNameNotFound() {
        when(createProjectTool.getName()).thenReturn("create-project");
        when(listProjectsTool.getName()).thenReturn("list-projects");
        when(showProjectTool.getName()).thenReturn("show-project");
        when(addRequirementTool.getName()).thenReturn("add-requirement");
        when(prepareStoriesTool.getName()).thenReturn("prepare-stories");
        when(helpTool.getName()).thenReturn("help");
        
        Tool tool = mcpToolService.getToolByName("non-existent-tool");
        
        assertNull(tool);
    }

    @Test
    public void testExecuteTool() {
        String toolName = "create-project";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "Test Project");
        parameters.put("description", "Test Description");
        
        ToolResult expectedResult = ToolResult.success("Project created successfully");
        
        when(createProjectTool.getName()).thenReturn(toolName);
        when(createProjectTool.execute(parameters)).thenReturn(expectedResult);
        
        ToolResult result = mcpToolService.executeTool(toolName, parameters);
        
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(createProjectTool).execute(parameters);
    }

    @Test
    public void testExecuteToolNotFound() {
        String toolName = "non-existent-tool";
        Map<String, String> parameters = new HashMap<>();
        
        when(createProjectTool.getName()).thenReturn("create-project");
        when(listProjectsTool.getName()).thenReturn("list-projects");
        when(showProjectTool.getName()).thenReturn("show-project");
        when(addRequirementTool.getName()).thenReturn("add-requirement");
        when(prepareStoriesTool.getName()).thenReturn("prepare-stories");
        when(helpTool.getName()).thenReturn("help");
        
        ToolResult result = mcpToolService.executeTool(toolName, parameters);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    public void testGetMcpTools() {
        List<Tool> tools = mcpToolService.getMcpTools();
        
        assertNotNull(tools);
        assertEquals(6, tools.size());
        assertTrue(tools.contains(createProjectTool));
        assertTrue(tools.contains(listProjectsTool));
        assertTrue(tools.contains(showProjectTool));
        assertTrue(tools.contains(addRequirementTool));
        assertTrue(tools.contains(prepareStoriesTool));
        assertTrue(tools.contains(helpTool));
    }

    @Test
    public void testGetTools() {
        List<Tool> tools = mcpToolService.getTools();
        
        assertNotNull(tools);
        assertEquals(6, tools.size());
        assertTrue(tools.contains(createProjectTool));
        assertTrue(tools.contains(listProjectsTool));
        assertTrue(tools.contains(showProjectTool));
        assertTrue(tools.contains(addRequirementTool));
        assertTrue(tools.contains(prepareStoriesTool));
        assertTrue(tools.contains(helpTool));
    }
}
