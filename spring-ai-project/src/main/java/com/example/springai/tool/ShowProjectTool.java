package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.service.ProjectService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Tool for showing project details
 */
@Component
public class ShowProjectTool extends ProjectManagementTool {
    private final ProjectService projectService;

    public ShowProjectTool(ProjectService projectService) {
        super("show-project", "Show details of a specific project");
        this.projectService = projectService;
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        String name = parameters.get("name");
        
        if (name == null || name.trim().isEmpty()) {
            return ToolResult.failure("Project name is required");
        }
        
        try {
            Optional<Project> projectOpt = projectService.findProjectByName(name);
            if (projectOpt.isEmpty()) {
                return ToolResult.failure(String.format("Project '%s' not found. Please check the name and try again.", name));
            }
            
            Project project = projectOpt.get();
            StringBuilder response = new StringBuilder();
            response.append(String.format("Project: %s\n", project.getName()));
            
            if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                response.append(String.format("Description: %s\n\n", project.getDescription()));
            }
            
            if (project.getRequirements().isEmpty()) {
                response.append("This project doesn't have any requirements yet.");
            } else {
                response.append("Requirements:\n");
                for (Requirement req : project.getRequirements()) {
                    response.append(String.format("- %s\n", req.getText()));
                }
            }
            
            return ToolResult.success(response.toString());
        } catch (Exception e) {
            return ToolResult.failure("Failed to show project: " + e.getMessage());
        }
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"name"};
    }
}
