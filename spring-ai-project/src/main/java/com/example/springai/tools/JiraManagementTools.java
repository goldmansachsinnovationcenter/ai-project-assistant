package com.example.springai.tools;

import com.example.springai.service.JiraService;
import com.example.springai.dto.CreateIssueRequest;
import com.example.springai.dto.JiraSearchRequest;
import com.example.springai.entity.JiraIssue;
import com.example.springai.entity.JiraProject;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JiraManagementTools {
    
    private final JiraService jiraService;
    private final OllamaChatModel chatModel;
    
    public JiraManagementTools(JiraService jiraService, OllamaChatModel chatModel) {
        this.jiraService = jiraService;
        this.chatModel = chatModel;
        System.out.println("JiraManagementTools bean created successfully!");
    }
    
    @Tool(description = "List all available Jira projects")
    public String listJiraProjects() {
        System.out.println("List Jira projects tool called");
        try {
            List<JiraProject> projects = jiraService.getProjects();
            
            if (projects.isEmpty()) {
                return "No Jira projects found.";
            }
            
            StringBuilder result = new StringBuilder("Available Jira Projects:\n\n");
            for (JiraProject project : projects) {
                result.append(String.format("• %s (%s)\n", project.getName(), project.getKey()));
                if (project.getDescription() != null && !project.getDescription().trim().isEmpty()) {
                    result.append(String.format("  Description: %s\n", project.getDescription()));
                }
                result.append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to list Jira projects: " + e.getMessage();
        }
    }
    
    @Tool(description = "Create a new Jira ticket with summary, description, project key, issue type, and priority")
    public String createJiraTicket(String summary, String description, String projectKey, String issueType, String priority) {
        System.out.println("Create Jira ticket tool called");
        if (summary == null || summary.trim().isEmpty()) {
            return "Ticket summary is required";
        }
        
        try {
            CreateIssueRequest request = new CreateIssueRequest();
            request.setSummary(summary);
            request.setDescription(description);
            request.setProjectKey(projectKey);
            request.setIssueType(issueType != null ? issueType : "Task");
            request.setPriority(priority != null ? priority : "Medium");
            
            JiraIssue ticket = jiraService.createTicket(request);
            return String.format("Jira ticket '%s' has been created successfully with summary: %s", ticket.getKey(), summary);
        } catch (Exception e) {
            return "Failed to create Jira ticket: " + e.getMessage();
        }
    }
    
    @Tool(description = "Search for Jira tickets using JQL, project key, status, assignee, or keyword")
    public String searchJiraTickets(String jql, String projectKey, String status, String assignee, String keyword) {
        System.out.println("Search Jira tickets tool called");
        try {
            JiraSearchRequest searchRequest = new JiraSearchRequest();
            searchRequest.setJql(jql);
            searchRequest.setProjectKey(projectKey);
            searchRequest.setStatus(status);
            searchRequest.setAssignee(assignee);
            searchRequest.setKeyword(keyword);
            searchRequest.setMaxResults(50);
            
            List<JiraIssue> tickets = jiraService.searchTickets(searchRequest);
            
            if (tickets.isEmpty()) {
                return "No Jira tickets found matching the search criteria.";
            }
            
            StringBuilder result = new StringBuilder("Found Jira Tickets:\n\n");
            for (JiraIssue ticket : tickets) {
                result.append(String.format("• %s: %s\n", ticket.getKey(), ticket.getSummary()));
                result.append(String.format("  Status: %s | Priority: %s\n", ticket.getStatus(), ticket.getPriority()));
                if (ticket.getAssignee() != null) {
                    result.append(String.format("  Assignee: %s\n", ticket.getAssignee()));
                }
                result.append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to search Jira tickets: " + e.getMessage();
        }
    }
    
    @Tool(description = "Display available Jira commands and usage examples")
    public String jiraHelp() {
        System.out.println("Jira help tool called");
        StringBuilder response = new StringBuilder("Available Jira commands:\n\n");
        
        response.append("1. List Jira projects:\n");
        response.append("   Example: list jira projects\n\n");
        
        response.append("2. Create a Jira ticket:\n");
        response.append("   Example: create jira ticket with summary 'Bug fix' and description 'Fix login issue'\n\n");
        
        response.append("3. Search Jira tickets:\n");
        response.append("   Example: search jira tickets for project PROJ\n");
        response.append("   Example: search jira tickets with status 'In Progress'\n");
        response.append("   Example: search jira tickets assigned to john.doe\n\n");
        
        response.append("4. Get help with Jira commands:\n");
        response.append("   Example: jira help\n\n");
        
        response.append("Note: Jira integration allows you to manage tickets, projects, and workflows through natural language commands.");
        
        return response.toString();
    }
}
