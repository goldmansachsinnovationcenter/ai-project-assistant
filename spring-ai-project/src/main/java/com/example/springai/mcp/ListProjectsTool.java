package com.example.springai.mcp;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Tool for listing all projects
 */
@Component("mcpListProjectsTool")
public class ListProjectsTool extends ProjectManagementTool {
    private final ProjectService projectService;

    public ListProjectsTool(ProjectService projectService) {
        super("list-projects", "List all available projects");
        this.projectService = projectService;
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        try {
            if (!projectService.hasProjects()) {
                return ToolResult.success("You don't have any projects yet. Would you like to create a new project?");
            }
            
            List<Project> projects = projectService.getAllProjects();
            StringBuilder response = new StringBuilder("Here are your projects:\n");
            for (Project project : projects) {
                response.append("- ").append(project.getName()).append("\n");
            }
            
            return ToolResult.success(response.toString());
        } catch (Exception e) {
            return ToolResult.failure("Failed to list projects: " + e.getMessage());
        }
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{};
    }
}
