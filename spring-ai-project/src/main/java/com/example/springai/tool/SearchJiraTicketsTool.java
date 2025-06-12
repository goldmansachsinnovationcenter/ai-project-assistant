package com.example.springai.tool;

import com.example.springai.service.JiraService;
import com.example.springai.entity.JiraIssue;
import com.example.springai.dto.JiraSearchRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Tool for searching Jira tickets
 */
@Component
public class SearchJiraTicketsTool extends JiraManagementTool {
    private final JiraService jiraService;

    public SearchJiraTicketsTool(JiraService jiraService) {
        super("search-jira-tickets", "Search for Jira tickets using JQL or simple text search");
        this.jiraService = jiraService;
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        String query = parameters.get("query");
        String projectKey = parameters.get("projectKey");
        String status = parameters.get("status");
        String assignee = parameters.get("assignee");
        
        if (query == null || query.trim().isEmpty()) {
            return ToolResult.failure("Search query is required");
        }
        
        try {
            JiraSearchRequest searchRequest = new JiraSearchRequest();
            searchRequest.setKeyword(query);
            searchRequest.setProjectKey(projectKey);
            searchRequest.setStatus(status);
            searchRequest.setAssignee(assignee);
            
            List<JiraIssue> tickets = jiraService.searchTickets(searchRequest);
            
            if (tickets.isEmpty()) {
                return ToolResult.success("No Jira tickets found matching the search criteria.");
            }
            
            StringBuilder result = new StringBuilder(String.format("Found %d Jira ticket(s):\n\n", tickets.size()));
            for (JiraIssue ticket : tickets) {
                result.append(String.format("• %s: %s\n", ticket.getKey(), ticket.getSummary()));
                result.append(String.format("  Status: %s", ticket.getStatus()));
                if (ticket.getAssignee() != null) {
                    result.append(String.format(" | Assignee: %s", ticket.getAssignee()));
                }
                if (ticket.getPriority() != null) {
                    result.append(String.format(" | Priority: %s", ticket.getPriority()));
                }
                result.append("\n");
                if (ticket.getDescription() != null && !ticket.getDescription().trim().isEmpty()) {
                    String shortDesc = ticket.getDescription().length() > 100 
                        ? ticket.getDescription().substring(0, 100) + "..." 
                        : ticket.getDescription();
                    result.append(String.format("  Description: %s\n", shortDesc));
                }
                result.append("\n");
            }
            
            return ToolResult.success(result.toString());
        } catch (Exception e) {
            return ToolResult.failure("Failed to search Jira tickets: " + e.getMessage());
        }
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"query", "projectKey", "status", "assignee"};
    }
}
