package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CreateProjectToolTest {

    @Test
    public void testCreateProjectSuccess() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.empty());
        Mockito.when(projectService.createProject("TestProject", "Test Description"))
                .thenReturn(project);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "TestProject");
        parameters.put("description", "Test Description");
        
        CreateProjectTool tool = new CreateProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("TestProject"));
        assertTrue(result.getMessage().contains("created successfully"));
        
        Mockito.verify(projectService).createProject("TestProject", "Test Description");
    }
    
    @Test
    public void testCreateProjectAlreadyExists() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Project existingProject = new Project();
        existingProject.setName("TestProject");
        existingProject.setDescription("Existing Description");
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.of(existingProject));
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "TestProject");
        parameters.put("description", "Test Description");
        
        CreateProjectTool tool = new CreateProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        
        Mockito.verify(projectService, Mockito.never()).createProject(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    public void testCreateProjectMissingName() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("description", "Test Description");
        
        CreateProjectTool tool = new CreateProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
        
        Mockito.verify(projectService, Mockito.never()).createProject(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    public void testCreateProjectServiceException() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.empty());
        Mockito.when(projectService.createProject("TestProject", "Test Description"))
                .thenThrow(new RuntimeException("Database error"));
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "TestProject");
        parameters.put("description", "Test Description");
        
        CreateProjectTool tool = new CreateProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to create project"));
        assertTrue(result.getMessage().contains("Database error"));
    }
}
