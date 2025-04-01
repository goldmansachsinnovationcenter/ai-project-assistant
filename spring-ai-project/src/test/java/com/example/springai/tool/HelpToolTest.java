package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HelpToolTest {

    @Test
    public void testHelpToolWithNoProjects() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Mockito.when(projectService.hasProjects()).thenReturn(false);
        
        HelpTool tool = new HelpTool(projectService);
        ToolResult result = tool.execute(Collections.emptyMap());
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Available commands"));
        assertTrue(result.getMessage().contains("Create a new project"));
        assertTrue(result.getMessage().contains("List all projects"));
        assertTrue(result.getMessage().contains("Show project details"));
        assertTrue(result.getMessage().contains("Add requirement"));
        assertTrue(result.getMessage().contains("Prepare stories"));
        
        assertFalse(result.getMessage().contains("Example with existing project"));
    }
    
    @Test
    public void testHelpToolWithExistingProjects() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Project exampleProject = new Project();
        exampleProject.setName("ExampleProject");
        exampleProject.setDescription("Example Description");
        
        Mockito.when(projectService.hasProjects()).thenReturn(true);
        Mockito.when(projectService.getFirstProject()).thenReturn(exampleProject);
        
        HelpTool tool = new HelpTool(projectService);
        ToolResult result = tool.execute(Collections.emptyMap());
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Available commands"));
        assertTrue(result.getMessage().contains("Example with existing project"));
        assertTrue(result.getMessage().contains("ExampleProject"));
    }
}
