package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Tool for creating a new project
 */
@Component
public class CreateProjectTool extends ProjectManagementTool {
    private final ProjectService projectService;

    public CreateProjectTool(ProjectService projectService) {
        super("create-project", "Create a new project with the given name");
        this.projectService = projectService;
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        String name = parameters.get("name");
        String description = parameters.get("description");
        
        if (name == null || name.trim().isEmpty()) {
            return ToolResult.failure("Project name is required");
        }
        
        try {
            if (projectService.findProjectByName(name).isPresent()) {
                return ToolResult.failure(String.format("Project '%s' already exists", name));
            }
            
            Project project = projectService.createProject(name, description);
            return ToolResult.success(String.format("Project '%s' has been created successfully", project.getName()));
        } catch (Exception e) {
            return ToolResult.failure("Failed to create project: " + e.getMessage());
        }
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"name", "description"};
    }
}
