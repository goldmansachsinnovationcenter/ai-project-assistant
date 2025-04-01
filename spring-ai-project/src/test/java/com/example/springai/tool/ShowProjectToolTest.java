package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShowProjectToolTest {

    @Test
    public void testShowProjectWithRequirements() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        List<Requirement> requirements = new ArrayList<>();
        Requirement req1 = new Requirement();
        req1.setText("The system should allow user login");
        
        Requirement req2 = new Requirement();
        req2.setText("The system should allow user registration");
        
        requirements.add(req1);
        requirements.add(req2);
        project.setRequirements(requirements);
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.of(project));
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "TestProject");
        
        ShowProjectTool tool = new ShowProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("TestProject"));
        assertTrue(result.getMessage().contains("Test Description"));
        assertTrue(result.getMessage().contains("The system should allow user login"));
        assertTrue(result.getMessage().contains("The system should allow user registration"));
    }
    
    @Test
    public void testShowProjectWithoutRequirements() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        project.setRequirements(Collections.emptyList());
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.of(project));
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "TestProject");
        
        ShowProjectTool tool = new ShowProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("TestProject"));
        assertTrue(result.getMessage().contains("Test Description"));
        assertTrue(result.getMessage().contains("doesn't have any requirements"));
    }
    
    @Test
    public void testShowProjectNotFound() {
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
    
    @Test
    public void testShowProjectMissingName() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Map<String, String> parameters = new HashMap<>();
        
        ShowProjectTool tool = new ShowProjectTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
    }
}
