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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowProjectToolTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ShowProjectTool showProjectTool;

    private Map<String, String> parameters;
    private Project testProject;

    @BeforeEach
    public void setup() {
        parameters = new HashMap<>();
        parameters.put("name", "Test Project");
        
        testProject = new Project();
        testProject.setId("test-id");
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        
        List<Requirement> requirements = new ArrayList<>();
        Requirement req1 = new Requirement();
        req1.setText("Requirement 1");
        req1.setProject(testProject);
        requirements.add(req1);
        
        Requirement req2 = new Requirement();
        req2.setText("Requirement 2");
        req2.setProject(testProject);
        requirements.add(req2);
        
        testProject.setRequirements(requirements);
    }

    @Test
    public void testExecute_Success() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        ToolResult result = showProjectTool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Test Project"));
        assertTrue(result.getMessage().contains("Test Description"));
        assertTrue(result.getMessage().contains("Requirement 1"));
        assertTrue(result.getMessage().contains("Requirement 2"));
        verify(projectService, times(1)).findProjectByName("Test Project");
    }
    
    @Test
    public void testExecute_ProjectNotFound() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.empty());
        
        ToolResult result = showProjectTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("not found"));
        verify(projectService, times(1)).findProjectByName("Test Project");
    }
    
    @Test
    public void testExecute_MissingProjectName() {
        parameters.remove("name");
        
        ToolResult result = showProjectTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("required"));
        verify(projectService, never()).findProjectByName(anyString());
    }
    
    @Test
    public void testExecute_EmptyProjectName() {
        parameters.put("name", "  ");
        
        ToolResult result = showProjectTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("required"));
        verify(projectService, never()).findProjectByName(anyString());
    }
    
    @Test
    public void testExecute_Exception() {
        when(projectService.findProjectByName("Test Project")).thenThrow(new RuntimeException("Database error"));
        
        ToolResult result = showProjectTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Failed to show project"));
        assertTrue(result.getMessage().contains("Database error"));
        verify(projectService, times(1)).findProjectByName("Test Project");
    }
    
    @Test
    public void testGetParameterNames() {
        String[] paramNames = showProjectTool.getParameterNames();
        
        assertEquals(1, paramNames.length);
        assertEquals("name", paramNames[0]);
    }
    
    @Test
    public void testGetName() {
        assertEquals("show-project", showProjectTool.getName());
    }
    
    @Test
    public void testGetDescription() {
        String description = showProjectTool.getDescription();
        assertNotNull(description);
        assertEquals("Show details of a specific project", description);
    }
    
    @Test
    public void testGetUri() {
        String uri = showProjectTool.getUri();
        assertNotNull(uri);
        assertTrue(uri.contains("show-project"));
    }
}
