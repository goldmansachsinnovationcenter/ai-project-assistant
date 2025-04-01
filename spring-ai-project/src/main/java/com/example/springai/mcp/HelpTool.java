package com.example.springai.mcp;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Tool for displaying help information
 */
@Component("mcpHelpTool")
public class HelpTool extends ProjectManagementTool {
    private final ProjectService projectService;

    public HelpTool(ProjectService projectService) {
        super("help", "Display available commands and usage examples");
        this.projectService = projectService;
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        StringBuilder response = new StringBuilder("Available commands:\n\n");
        
        response.append("1. Create a new project:\n");
        response.append("   Example: create project ProjectName\n\n");
        
        response.append("2. List all projects:\n");
        response.append("   Example: list projects\n\n");
        
        response.append("3. Show project details:\n");
        response.append("   Example: show project ProjectName\n\n");
        
        response.append("4. Add requirement to a project:\n");
        response.append("   Example: add requirement The system should allow user login to project ProjectName\n\n");
        
        response.append("5. Prepare stories for a project:\n");
        response.append("   Example: prepare stories for project ProjectName\n\n");
        
        if (projectService.hasProjects()) {
            Project exampleProject = projectService.getFirstProject();
            response.append("Example with existing project:\n");
            response.append(String.format("- show project %s\n", exampleProject.getName()));
            response.append(String.format("- add requirement New login feature to project %s\n", exampleProject.getName()));
            response.append(String.format("- prepare stories for project %s\n", exampleProject.getName()));
        }
        
        return ToolResult.success(response.toString());
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{};
    }
}
