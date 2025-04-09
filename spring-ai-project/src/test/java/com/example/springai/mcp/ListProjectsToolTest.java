package com.example.springai.mcp;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListProjectsToolTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ListProjectsTool listProjectsTool;

    private List<Project> projectList;
    private Map<String, String> parameters;

    @BeforeEach
    public void setup() {
        Project project1 = new Project();
        project1.setId("test-id-1");
        project1.setName("Test Project 1");
        project1.setDescription("Test Description 1");
        
        Project project2 = new Project();
        project2.setId("test-id-2");
        project2.setName("Test Project 2");
        project2.setDescription("Test Description 2");
        
        projectList = Arrays.asList(project1, project2);
        parameters = new HashMap<>();
    }

    @Test
    public void testExecute_WithProjects() {
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getAllProjects()).thenReturn(projectList);

        ToolResult result = listProjectsTool.execute(parameters);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Here are your projects"));
        assertTrue(result.getMessage().contains("Test Project 1"));
        assertTrue(result.getMessage().contains("Test Project 2"));
        verify(projectService, times(1)).hasProjects();
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    public void testExecute_NoProjects() {
        when(projectService.hasProjects()).thenReturn(false);

        ToolResult result = listProjectsTool.execute(parameters);

        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("You don't have any projects yet"));
        verify(projectService, times(1)).hasProjects();
        verify(projectService, never()).getAllProjects();
    }

    @Test
    public void testExecute_Exception() {
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getAllProjects()).thenThrow(new RuntimeException("Database error"));

        ToolResult result = listProjectsTool.execute(parameters);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to list projects"));
        assertTrue(result.getMessage().contains("Database error"));
        verify(projectService, times(1)).hasProjects();
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    public void testGetParameterNames() {
        String[] paramNames = listProjectsTool.getParameterNames();
        
        assertEquals(0, paramNames.length);
    }
}
