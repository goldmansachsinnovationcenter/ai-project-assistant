package com.example.springai.service;

import com.example.springai.tool.*; // Corrected import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class McpToolServiceTest {

    @Mock private CreateProjectTool createProjectTool;
    @Mock private ListProjectsTool listProjectsTool;
    @Mock private ShowProjectTool showProjectTool;
    @Mock private AddRequirementTool addRequirementTool;
    @Mock private HelpTool helpTool;

    private McpToolService mcpToolService;

    @BeforeEach
    void setUp() {
        mcpToolService = new McpToolService(
                createProjectTool,
                listProjectsTool,
                showProjectTool,
                addRequirementTool,
                helpTool
        );
        lenient().when(createProjectTool.getName()).thenReturn("createProject");
        lenient().when(listProjectsTool.getName()).thenReturn("listProjects");
        lenient().when(showProjectTool.getName()).thenReturn("showProject");
        lenient().when(addRequirementTool.getName()).thenReturn("addRequirement");
        lenient().when(helpTool.getName()).thenReturn("help");
    }

    @Test
    void getAllTools_ReturnsCorrectListOfTools() {
        List<Tool> tools = mcpToolService.getAllTools();
        assertNotNull(tools);
        assertEquals(5, tools.size(), "Should return 5 tools");
        assertTrue(tools.contains(createProjectTool));
        assertTrue(tools.contains(listProjectsTool));
        assertTrue(tools.contains(showProjectTool));
        assertTrue(tools.contains(addRequirementTool));
        assertTrue(tools.contains(helpTool));
        assertFalse(tools.stream().anyMatch(t -> "prepareStories".equals(t.getName())), "PrepareStoriesTool should not be present");
    }

    @Test
    void getToolByName_Found() {
        Tool foundTool = mcpToolService.getToolByName("showProject");
        assertNotNull(foundTool);
        assertEquals(showProjectTool, foundTool);
        assertEquals("showProject", foundTool.getName());
    }

    @Test
    void getToolByName_NotFound() {
        Tool foundTool = mcpToolService.getToolByName("nonExistentTool");
        assertNull(foundTool, "Should return null for a non-existent tool name");
    }

    @Test
    void executeTool_Success() {
        String toolName = "addRequirement";
        Map<String, String> parameters = Map.of("project", "proj1", "text", "req1");
        when(addRequirementTool.execute(anyMap())).thenReturn(ToolResult.success("Requirement added"));

        ToolResult expectedResult = ToolResult.success("Requirement added");
        ToolResult actualResult = mcpToolService.executeTool(toolName, parameters);

        assertNotNull(actualResult);
        assertTrue(actualResult.isSuccess());
        assertEquals(expectedResult.getMessage(), actualResult.getMessage());

        verify(addRequirementTool, times(1)).execute(parameters);
        verify(createProjectTool, never()).execute(any());
        verify(listProjectsTool, never()).execute(any());
        verify(showProjectTool, never()).execute(any());
        verify(helpTool, never()).execute(any());
    }

    @Test
    void executeTool_ToolNotFound() {
        String toolName = "unknownTool";
        Map<String, String> parameters = Collections.emptyMap();

        ToolResult actualResult = mcpToolService.executeTool(toolName, parameters);

        assertNotNull(actualResult);
        assertFalse(actualResult.isSuccess());
        assertEquals("Tool 'unknownTool' not found", actualResult.getMessage());

        verify(createProjectTool, never()).execute(any());
        verify(listProjectsTool, never()).execute(any());
        verify(showProjectTool, never()).execute(any());
        verify(addRequirementTool, never()).execute(any());
        verify(helpTool, never()).execute(any());
    }

    @Test
    void getMcpTools_ReturnsCorrectTools() {
        List<Tool> tools = mcpToolService.getMcpTools(); // Assuming this method exists and is relevant
        assertEquals(5, tools.size(), "getMcpTools should return 5 tools");
        assertTrue(tools.contains(helpTool)); // Example check
    }

    @Test
    void getTools_ReturnsCorrectTools() {
        List<Tool> tools = mcpToolService.getTools();
        assertEquals(5, tools.size(), "getTools should return 5 tools");
        assertTrue(tools.contains(createProjectTool)); // Example check
    }
}
