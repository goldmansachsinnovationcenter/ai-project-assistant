package com.example.springai.tool;

import com.example.springai.service.JiraService;
import com.example.springai.dto.CreateIssueRequest;
import com.example.springai.entity.JiraIssue;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Tool for creating a new Jira ticket
 */
@Component
public class CreateJiraTicketTool extends JiraManagementTool {
    private final JiraService jiraService;

    public CreateJiraTicketTool(JiraService jiraService) {
        super("create-jira-ticket", "Create a new Jira ticket with the given summary and description");
        this.jiraService = jiraService;
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        String summary = parameters.get("summary");
        String description = parameters.get("description");
        String projectKey = parameters.get("projectKey");
        String issueType = parameters.getOrDefault("issueType", "Task");
        String priority = parameters.getOrDefault("priority", "Medium");
        
        if (summary == null || summary.trim().isEmpty()) {
            return ToolResult.failure("Ticket summary is required");
        }
        
        try {
            CreateIssueRequest request = new CreateIssueRequest();
            request.setSummary(summary);
            request.setDescription(description);
            request.setProjectKey(projectKey);
            request.setIssueType(issueType);
            request.setPriority(priority);
            
            JiraIssue ticket = jiraService.createTicket(request);
            return ToolResult.success(String.format("Jira ticket '%s' has been created successfully with summary: %s", ticket.getKey(), summary));
        } catch (Exception e) {
            return ToolResult.failure("Failed to create Jira ticket: " + e.getMessage());
        }
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"summary", "description", "projectKey", "issueType", "priority"};
    }
}
