package com.example.springai.tool;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Test class for the custom tool implementation
 */
public class ToolTest {

    @Test
    public void testCreateProjectTool() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Project project = new Project();
        project.setName("TestProject");
        
        Mockito.when(projectService.createProject("TestProject", null))
                .thenReturn(project);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "TestProject");
        
        CreateProjectTool tool = new CreateProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("TestProject"));
        
        Mockito.verify(projectService).createProject("TestProject", null);
    }
    
    @Test
    public void testListProjectsTool() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Project project1 = new Project();
        project1.setName("Project1");
        
        Project project2 = new Project();
        project2.setName("Project2");
        
        Mockito.when(projectService.hasProjects()).thenReturn(true);
        Mockito.when(projectService.getAllProjects())
                .thenReturn(Arrays.asList(project1, project2));
        
        ListProjectsTool tool = new ListProjectsTool(projectService);
        ToolResult result = tool.execute(Collections.emptyMap());
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Project1"));
        assertTrue(result.getMessage().contains("Project2"));
    }
    
    @Test
    public void testListProjectsToolEmpty() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Mockito.when(projectService.hasProjects()).thenReturn(false);
        
        ListProjectsTool tool = new ListProjectsTool(projectService);
        ToolResult result = tool.execute(Collections.emptyMap());
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("don't have any projects"));
    }
    
    @Test
    public void testShowProjectTool() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.of(project));
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "TestProject");
        
        ShowProjectTool tool = new ShowProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("TestProject"));
        assertTrue(result.getMessage().contains("Test Description"));
    }
    
    @Test
    public void testShowProjectToolNotFound() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Mockito.when(projectService.findProjectByName("NonExistentProject"))
                .thenReturn(Optional.empty());
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "NonExistentProject");
        
        ShowProjectTool tool = new ShowProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }
}
