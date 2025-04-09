package com.example.springai.mcp;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateProjectToolTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private CreateProjectTool createProjectTool;

    private Project testProject;
    private Map<String, String> parameters;

    @BeforeEach
    public void setup() {
        testProject = new Project();
        testProject.setId("test-id");
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        
        parameters = new HashMap<>();
        parameters.put("name", "Test Project");
        parameters.put("description", "Test Description");
    }

    @Test
    public void testExecute_Success() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.empty());
        when(projectService.createProject("Test Project", "Test Description")).thenReturn(testProject);

        ToolResult result = createProjectTool.execute(parameters);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Test Project"));
        assertTrue(result.getMessage().contains("created successfully"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(projectService, times(1)).createProject("Test Project", "Test Description");
    }

    @Test
    public void testExecute_ProjectAlreadyExists() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));

        ToolResult result = createProjectTool.execute(parameters);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(projectService, never()).createProject(anyString(), anyString());
    }

    @Test
    public void testExecute_MissingName() {
        parameters.remove("name");

        ToolResult result = createProjectTool.execute(parameters);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("name is required"));
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).createProject(anyString(), anyString());
    }

    @Test
    public void testExecute_EmptyName() {
        parameters.put("name", "  ");

        ToolResult result = createProjectTool.execute(parameters);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("name is required"));
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).createProject(anyString(), anyString());
    }

    @Test
    public void testExecute_Exception() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.empty());
        when(projectService.createProject("Test Project", "Test Description")).thenThrow(new RuntimeException("Database error"));

        ToolResult result = createProjectTool.execute(parameters);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to create project"));
        assertTrue(result.getMessage().contains("Database error"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(projectService, times(1)).createProject("Test Project", "Test Description");
    }

    @Test
    public void testGetParameterNames() {
        String[] paramNames = createProjectTool.getParameterNames();
        
        assertEquals(2, paramNames.length);
        assertEquals("name", paramNames[0]);
        assertEquals("description", paramNames[1]);
    }
}
