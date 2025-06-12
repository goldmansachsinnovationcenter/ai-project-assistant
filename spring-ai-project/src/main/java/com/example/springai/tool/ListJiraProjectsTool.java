package com.example.springai.tool;

import com.example.springai.service.JiraService;
import com.example.springai.entity.JiraProject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Tool for listing all Jira projects
 */
@Component
public class ListJiraProjectsTool extends JiraManagementTool {
    private final JiraService jiraService;

    public ListJiraProjectsTool(JiraService jiraService) {
        super("list-jira-projects", "List all available Jira projects");
        this.jiraService = jiraService;
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        try {
            List<JiraProject> projects = jiraService.getProjects();
            
            if (projects.isEmpty()) {
                return ToolResult.success("No Jira projects found.");
            }
            
            StringBuilder result = new StringBuilder("Available Jira Projects:\n\n");
            for (JiraProject project : projects) {
                result.append(String.format("• %s (%s)\n", project.getName(), project.getKey()));
                if (project.getDescription() != null && !project.getDescription().trim().isEmpty()) {
                    result.append(String.format("  Description: %s\n", project.getDescription()));
                }
                result.append("\n");
            }
            
            return ToolResult.success(result.toString());
        } catch (Exception e) {
            return ToolResult.failure("Failed to list Jira projects: " + e.getMessage());
        }
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{};
    }
}
