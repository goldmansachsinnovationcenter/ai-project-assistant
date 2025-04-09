package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelpToolTest {

    @Mock
    private ProjectService projectService;

    private HelpTool helpTool;
    private Map<String, String> parameters;

    @BeforeEach
    public void setup() {
        helpTool = new HelpTool(projectService);
        parameters = new HashMap<>();
    }

    @Test
    public void testExecute_Success() {
        when(projectService.hasProjects()).thenReturn(false);
        
        ToolResult result = helpTool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Available commands"));
        assertTrue(result.getMessage().contains("Create a new project"));
        assertTrue(result.getMessage().contains("List all projects"));
        verify(projectService, times(1)).hasProjects();
        verify(projectService, never()).getFirstProject();
    }
    
    @Test
    public void testExecute_WithExistingProjects() {
        Project mockProject = new Project();
        mockProject.setName("TestProject");
        
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getFirstProject()).thenReturn(mockProject);
        
        ToolResult result = helpTool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Available commands"));
        assertTrue(result.getMessage().contains("Example with existing project"));
        assertTrue(result.getMessage().contains("TestProject"));
        verify(projectService, times(1)).hasProjects();
        verify(projectService, times(1)).getFirstProject();
    }
    
    @Test
    public void testExecute_Exception() {
        when(projectService.hasProjects()).thenThrow(new RuntimeException("Database error"));
        
        assertThrows(RuntimeException.class, () -> {
            helpTool.execute(parameters);
        });
        
        verify(projectService, times(1)).hasProjects();
        verify(projectService, never()).getFirstProject();
    }
    
    @Test
    public void testGetParameterNames() {
        String[] paramNames = helpTool.getParameterNames();
        
        assertEquals(0, paramNames.length);
    }
    
    @Test
    public void testGetName() {
        assertEquals("help", helpTool.getName());
    }
    
    @Test
    public void testGetDescription() {
        String description = helpTool.getDescription();
        assertNotNull(description);
        assertEquals("Display available commands and usage examples", description);
    }
    
    @Test
    public void testGetUri() {
        String uri = helpTool.getUri();
        assertNotNull(uri);
        assertEquals("project-assistant/help", uri);
    }
}
