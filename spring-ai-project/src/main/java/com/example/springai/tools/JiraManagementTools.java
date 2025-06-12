package com.example.springai.tools;

import com.example.springai.dto.CreateIssueRequest;
import com.example.springai.dto.JiraSearchRequest;
import com.example.springai.dto.UpdateIssueRequest;
import com.example.springai.entity.JiraIssue;
import com.example.springai.entity.JiraProject;
import com.example.springai.service.JiraService;
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
    
    @Tool(description = "Create a new Jira ticket with title, description, and project key")
    public String createJiraTicket(String title, String description, String projectKey) {
        System.out.println("Create Jira ticket tool called");
        if (title == null || title.trim().isEmpty()) {
            return "Ticket title is required";
        }
        
        try {
            CreateIssueRequest request = new CreateIssueRequest();
            request.setSummary(title);
            request.setDescription(description);
            request.setProjectKey(projectKey != null ? projectKey : "PROJ");
            request.setIssueType("Task");
            
            JiraIssue issue = jiraService.createTicket(request);
            return String.format("Jira ticket '%s' has been created successfully with key: %s", 
                                issue.getSummary(), issue.getKey());
        } catch (Exception e) {
            return "Failed to create Jira ticket: " + e.getMessage();
        }
    }
    
    @Tool(description = "Search for Jira tickets by keywords, project, or status")
    public String searchJiraTickets(String query, String projectKey, String status) {
        System.out.println("Search Jira tickets tool called");
        
        try {
            JiraSearchRequest request = new JiraSearchRequest();
            if (query != null && !query.trim().isEmpty()) {
                request.setKeyword(query);
            }
            if (projectKey != null && !projectKey.trim().isEmpty()) {
                request.setProjectKey(projectKey);
            }
            if (status != null && !status.trim().isEmpty()) {
                request.setStatus(status);
            }
            
            List<JiraIssue> issues = jiraService.searchTickets(request);
            
            if (issues.isEmpty()) {
                return "No Jira tickets found matching the search criteria";
            }
            
            StringBuilder result = new StringBuilder();
            result.append(String.format("Found %d Jira ticket(s):\n\n", issues.size()));
            
            for (JiraIssue issue : issues) {
                result.append(String.format("• %s - %s\n", issue.getKey(), issue.getSummary()));
                result.append(String.format("  Status: %s", issue.getStatus()));
                if (issue.getAssignee() != null) {
                    result.append(String.format(" | Assignee: %s", issue.getAssignee()));
                }
                if (issue.getPriority() != null) {
                    result.append(String.format(" | Priority: %s", issue.getPriority()));
                }
                result.append("\n");
                if (issue.getDescription() != null && !issue.getDescription().isEmpty()) {
                    String shortDesc = issue.getDescription().length() > 100 
                        ? issue.getDescription().substring(0, 100) + "..." 
                        : issue.getDescription();
                    result.append(String.format("  Description: %s\n", shortDesc));
                }
                result.append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to search Jira tickets: " + e.getMessage();
        }
    }
    
    @Tool(description = "Get detailed information about a specific Jira ticket by key")
    public String getJiraTicketDetails(String ticketKey) {
        System.out.println("Get Jira ticket details tool called");
        if (ticketKey == null || ticketKey.trim().isEmpty()) {
            return "Ticket key is required";
        }
        
        try {
            JiraIssue issue = jiraService.getTicket(ticketKey);
            
            StringBuilder result = new StringBuilder();
            result.append(String.format("Jira Ticket: %s\n", issue.getKey()));
            result.append(String.format("Title: %s\n", issue.getSummary()));
            result.append(String.format("Status: %s\n", issue.getStatus()));
            
            if (issue.getPriority() != null) {
                result.append(String.format("Priority: %s\n", issue.getPriority()));
            }
            if (issue.getAssignee() != null) {
                result.append(String.format("Assignee: %s\n", issue.getAssignee()));
            }
            if (issue.getReporter() != null) {
                result.append(String.format("Reporter: %s\n", issue.getReporter()));
            }
            if (issue.getProjectKey() != null) {
                result.append(String.format("Project: %s\n", issue.getProjectKey()));
            }
            if (issue.getCreated() != null) {
                result.append(String.format("Created: %s\n", issue.getCreated()));
            }
            if (issue.getUpdated() != null) {
                result.append(String.format("Updated: %s\n", issue.getUpdated()));
            }
            
            if (!issue.getLabels().isEmpty()) {
                result.append(String.format("Labels: %s\n", String.join(", ", issue.getLabels())));
            }
            
            if (issue.getDescription() != null && !issue.getDescription().isEmpty()) {
                result.append(String.format("\nDescription:\n%s\n", issue.getDescription()));
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to get Jira ticket details: " + e.getMessage();
        }
    }
    
    @Tool(description = "Update Jira ticket status, assignee, or priority")
    public String updateJiraTicket(String ticketKey, String field, String value) {
        System.out.println("Update Jira ticket tool called");
        if (ticketKey == null || ticketKey.trim().isEmpty()) {
            return "Ticket key is required";
        }
        if (field == null || field.trim().isEmpty()) {
            return "Field to update is required (status, assignee, priority, summary, description)";
        }
        if (value == null || value.trim().isEmpty()) {
            return "New value is required";
        }
        
        try {
            UpdateIssueRequest request = new UpdateIssueRequest();
            
            switch (field.toLowerCase()) {
                case "status":
                    request.setStatus(value);
                    break;
                case "assignee":
                    request.setAssignee(value);
                    break;
                case "priority":
                    request.setPriority(value);
                    break;
                case "summary":
                case "title":
                    request.setSummary(value);
                    break;
                case "description":
                    request.setDescription(value);
                    break;
                default:
                    return "Invalid field. Supported fields: status, assignee, priority, summary, description";
            }
            
            JiraIssue updatedIssue = jiraService.updateTicket(ticketKey, request);
            return String.format("Jira ticket '%s' has been updated successfully. %s set to: %s", 
                                ticketKey, field, value);
        } catch (Exception e) {
            return "Failed to update Jira ticket: " + e.getMessage();
        }
    }
    
    @Tool(description = "Add comment to existing Jira ticket")
    public String addJiraComment(String ticketKey, String comment) {
        System.out.println("Add Jira comment tool called");
        if (ticketKey == null || ticketKey.trim().isEmpty()) {
            return "Ticket key is required";
        }
        if (comment == null || comment.trim().isEmpty()) {
            return "Comment text is required";
        }
        
        try {
            jiraService.addComment(ticketKey, comment);
            return String.format("Comment has been added to Jira ticket '%s' successfully", ticketKey);
        } catch (Exception e) {
            return "Failed to add comment to Jira ticket: " + e.getMessage();
        }
    }
    
    @Tool(description = "Create Jira tickets from all requirements in a project")
    public String createJiraTicketsFromProject(String projectName) {
        System.out.println("Create Jira tickets from project tool called");
        if (projectName == null || projectName.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            List<JiraIssue> createdTickets = jiraService.createTicketsFromProject(projectName);
            
            if (createdTickets.isEmpty()) {
                return String.format("No tickets were created for project '%s'. The project may not have any requirements.", projectName);
            }
            
            StringBuilder result = new StringBuilder();
            result.append(String.format("Successfully created %d Jira ticket(s) from project '%s':\n\n", 
                                       createdTickets.size(), projectName));
            
            for (JiraIssue ticket : createdTickets) {
                result.append(String.format("• %s - %s\n", ticket.getKey(), ticket.getSummary()));
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to create Jira tickets from project: " + e.getMessage();
        }
    }
    
    @Tool(description = "List all available Jira projects")
    public String listJiraProjects() {
        System.out.println("List Jira projects tool called");
        
        try {
            List<JiraProject> projects = jiraService.getProjects();
            
            if (projects.isEmpty()) {
                return "No Jira projects found";
            }
            
            StringBuilder result = new StringBuilder();
            result.append(String.format("Available Jira projects (%d):\n\n", projects.size()));
            
            for (JiraProject project : projects) {
                result.append(String.format("• %s - %s\n", project.getKey(), project.getName()));
                if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                    String shortDesc = project.getDescription().length() > 100 
                        ? project.getDescription().substring(0, 100) + "..." 
                        : project.getDescription();
                    result.append(String.format("  Description: %s\n", shortDesc));
                }
                if (project.getLead() != null) {
                    result.append(String.format("  Lead: %s\n", project.getLead()));
                }
                result.append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Failed to list Jira projects: " + e.getMessage();
        }
    }
    
    @Tool(description = "Display available Jira commands and usage examples")
    public String jiraHelp() {
        System.out.println("Jira help tool called");
        StringBuilder response = new StringBuilder("Available Jira commands:\n\n");
        
        response.append("1. Create a new Jira ticket:\n");
        response.append("   Example: create jira ticket Fix login bug with description User cannot login to the system for project PROJ\n\n");
        
        response.append("2. Search for Jira tickets:\n");
        response.append("   Example: search jira tickets with keyword login\n");
        response.append("   Example: search jira tickets in project PROJ with status In Progress\n\n");
        
        response.append("3. Get ticket details:\n");
        response.append("   Example: get jira ticket details for PROJ-123\n\n");
        
        response.append("4. Update ticket:\n");
        response.append("   Example: update jira ticket PROJ-123 status to Done\n");
        response.append("   Example: update jira ticket PROJ-123 assignee to john.doe\n\n");
        
        response.append("5. Add comment to ticket:\n");
        response.append("   Example: add comment to jira ticket PROJ-123 with text Fixed the issue\n\n");
        
        response.append("6. Create tickets from project requirements:\n");
        response.append("   Example: create jira tickets from project MyProject\n\n");
        
        response.append("7. List Jira projects:\n");
        response.append("   Example: list jira projects\n\n");
        
        response.append("Supported ticket fields for updates: status, assignee, priority, summary, description\n");
        response.append("Common statuses: To Do, In Progress, Done, Blocked\n");
        response.append("Common priorities: Highest, High, Medium, Low, Lowest\n");
        
        return response.toString();
    }
}
