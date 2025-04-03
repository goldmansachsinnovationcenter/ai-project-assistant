package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AddRequirementToolTest {

    @Test
    public void testAddRequirementSuccess() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.of(project));
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("project", "TestProject");
        parameters.put("requirement", "The system should allow user login");
        
        AddRequirementTool tool = new AddRequirementTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("has been added to project"));
        
        Mockito.verify(projectService).addRequirement(project, "The system should allow user login");
    }
    
    @Test
    public void testAddRequirementProjectNotFound() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        Mockito.when(projectService.findProjectByName("NonExistentProject"))
                .thenReturn(Optional.empty());
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("project", "NonExistentProject");
        parameters.put("requirement", "The system should allow user login");
        
        AddRequirementTool tool = new AddRequirementTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }
    
    @Test
    public void testAddRequirementMissingParameters() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("project", "TestProject");
        
        AddRequirementTool tool = new AddRequirementTool(projectService);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
        
        parameters = new HashMap<>();
        parameters.put("requirement", "The system should allow user login");
        
        result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
    }
}
