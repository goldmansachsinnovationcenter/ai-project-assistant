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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddRequirementToolTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private AddRequirementTool addRequirementTool;

    private Project testProject;
    private Map<String, String> parameters;

    @BeforeEach
    public void setup() {
        testProject = new Project();
        testProject.setId("test-id");
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        
        parameters = new HashMap<>();
        parameters.put("project", "Test Project");
        parameters.put("requirement", "Test Requirement");
    }

    @Test
    public void testExecute_Success() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Test Requirement"));
        assertTrue(result.getMessage().contains("Test Project"));
        assertTrue(result.getMessage().contains("has been added"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(projectService, times(1)).addRequirement(eq(testProject), eq("Test Requirement"));
    }

    @Test
    public void testExecute_ProjectNotFound() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.empty());
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }

    @Test
    public void testExecute_MissingProjectName() {
        parameters.remove("project");
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }

    @Test
    public void testExecute_MissingRequirementText() {
        parameters.remove("requirement");
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }

    @Test
    public void testExecute_EmptyProjectName() {
        parameters.put("project", "  ");
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }

    @Test
    public void testExecute_EmptyRequirementText() {
        parameters.put("requirement", "  ");
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }

    @Test
    public void testExecute_Exception() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        doThrow(new RuntimeException("Database error")).when(projectService).addRequirement(any(Project.class), anyString());
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to add requirement"));
        assertTrue(result.getMessage().contains("Database error"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(projectService, times(1)).addRequirement(eq(testProject), eq("Test Requirement"));
    }

    @Test
    public void testGetParameterNames() {
        String[] paramNames = addRequirementTool.getParameterNames();
        
        assertEquals(2, paramNames.length);
        assertEquals("project", paramNames[0]);
        assertEquals("requirement", paramNames[1]);
    }
    
    @Test
    public void testGetName() {
        assertEquals("add-requirement", addRequirementTool.getName());
    }
    
    @Test
    public void testGetDescription() {
        assertTrue(addRequirementTool.getDescription().contains("Add a requirement"));
    }
    
    @Test
    public void testGetUri() {
        assertTrue(addRequirementTool.getUri().contains("add-requirement"));
    }
}
