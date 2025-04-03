package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ListProjectsToolTest {

    @Test
    public void testListProjectsWithProjects() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Project project1 = new Project();
        project1.setName("Project1");
        project1.setDescription("Description 1");
        
        Project project2 = new Project();
        project2.setName("Project2");
        project2.setDescription("Description 2");
        
        List<Project> projects = Arrays.asList(project1, project2);
        
        Mockito.when(projectService.hasProjects()).thenReturn(true);
        Mockito.when(projectService.getAllProjects()).thenReturn(projects);
        
        ListProjectsTool tool = new ListProjectsTool(projectService);
        ToolResult result = tool.execute(Collections.emptyMap());
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Here are your projects"));
        assertTrue(result.getMessage().contains("Project1"));
        assertTrue(result.getMessage().contains("Project2"));
        
        Mockito.verify(projectService).hasProjects();
        Mockito.verify(projectService).getAllProjects();
    }
    
    @Test
    public void testListProjectsWithNoProjects() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Mockito.when(projectService.hasProjects()).thenReturn(false);
        
        ListProjectsTool tool = new ListProjectsTool(projectService);
        ToolResult result = tool.execute(Collections.emptyMap());
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("don't have any projects"));
        assertTrue(result.getMessage().contains("create a new project"));
        
        Mockito.verify(projectService).hasProjects();
        Mockito.verify(projectService, Mockito.never()).getAllProjects();
    }
    
    @Test
    public void testListProjectsServiceException() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Mockito.when(projectService.hasProjects()).thenReturn(true);
        Mockito.when(projectService.getAllProjects())
                .thenThrow(new RuntimeException("Database error"));
        
        ListProjectsTool tool = new ListProjectsTool(projectService);
        ToolResult result = tool.execute(Collections.emptyMap());
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to list projects"));
        assertTrue(result.getMessage().contains("Database error"));
        
        Mockito.verify(projectService).hasProjects();
        Mockito.verify(projectService).getAllProjects();
    }
}
