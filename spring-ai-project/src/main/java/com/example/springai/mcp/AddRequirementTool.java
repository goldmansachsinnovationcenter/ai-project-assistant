package com.example.springai.mcp;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Tool for adding a requirement to a specific project
 */
@Component("mcpAddRequirementTool")
public class AddRequirementTool extends ProjectManagementTool {
    private final ProjectService projectService;

    public AddRequirementTool(ProjectService projectService) {
        super("add-requirement", "Add a requirement to a specific project");
        this.projectService = projectService;
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        String projectName = parameters.get("project");
        String requirementText = parameters.get("requirement");
        
        if (projectName == null || projectName.trim().isEmpty() || 
            requirementText == null || requirementText.trim().isEmpty()) {
            return ToolResult.failure("Project name and requirement text are required");
        }
        
        try {
            Optional<Project> projectOpt = projectService.findProjectByName(projectName);
            if (projectOpt.isEmpty()) {
                return ToolResult.failure(String.format("Project '%s' not found. Please check the name and try again.", projectName));
            }
            
            Project project = projectOpt.get();
            projectService.addRequirement(project, requirementText);
            
            return ToolResult.success(String.format("Requirement '%s' has been added to project '%s'.", 
                                     requirementText, project.getName()));
        } catch (Exception e) {
            return ToolResult.failure("Failed to add requirement: " + e.getMessage());
        }
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"project", "requirement"};
    }
}
