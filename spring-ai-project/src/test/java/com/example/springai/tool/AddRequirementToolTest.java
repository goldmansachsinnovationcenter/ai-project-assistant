package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
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

    private Map<String, String> parameters;
    private Project testProject;
    private Requirement testRequirement;

    @BeforeEach
    public void setup() {
        parameters = new HashMap<>();
        parameters.put("project", "Test Project");
        parameters.put("requirement", "Test Requirement");
        
        testProject = new Project();
        testProject.setId("test-id");
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        
        testRequirement = new Requirement();
        testRequirement.setId(1L);
        testRequirement.setText("Test Requirement");
        testRequirement.setProject(testProject);
    }

    @Test
    public void testExecute_Success() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        when(projectService.addRequirement(any(Project.class), anyString())).thenReturn(testRequirement);
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Test Requirement"));
        assertTrue(result.getMessage().contains("added to project"));
        assertTrue(result.getMessage().contains("Test Project"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(projectService, times(1)).addRequirement(testProject, "Test Requirement");
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
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().length() > 0);
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }
    
    @Test
    public void testExecute_EmptyProjectName() {
        parameters.put("project", "  ");
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().length() > 0);
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }
    
    @Test
    public void testExecute_MissingRequirement() {
        parameters.remove("requirement");
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().length() > 0);
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }
    
    @Test
    public void testExecute_EmptyRequirement() {
        parameters.put("requirement", "  ");
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().length() > 0);
        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(Project.class), anyString());
    }
    
    @Test
    public void testExecute_Exception() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        when(projectService.addRequirement(any(Project.class), anyString()))
            .thenThrow(new RuntimeException("Database error"));
        
        ToolResult result = addRequirementTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to add requirement"));
        assertTrue(result.getMessage().contains("Database error"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(projectService, times(1)).addRequirement(testProject, "Test Requirement");
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
