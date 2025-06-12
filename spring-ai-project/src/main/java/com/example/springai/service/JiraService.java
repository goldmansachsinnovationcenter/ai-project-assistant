package com.example.springai.service;

import com.example.springai.dto.CreateIssueRequest;
import com.example.springai.dto.JiraSearchRequest;
import com.example.springai.dto.UpdateIssueRequest;
import com.example.springai.entity.JiraIssue;
import com.example.springai.entity.JiraProject;
import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.repository.JiraIssueRepository;
import com.example.springai.repository.JiraProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JiraService {
    
    private final JiraApiClient jiraApiClient;
    private final JiraIssueRepository jiraIssueRepository;
    private final JiraProjectRepository jiraProjectRepository;
    private final ProjectService projectService;
    
    public JiraService(JiraApiClient jiraApiClient, 
                      JiraIssueRepository jiraIssueRepository,
                      JiraProjectRepository jiraProjectRepository,
                      ProjectService projectService) {
        this.jiraApiClient = jiraApiClient;
        this.jiraIssueRepository = jiraIssueRepository;
        this.jiraProjectRepository = jiraProjectRepository;
        this.projectService = projectService;
    }
    
    public JiraIssue createTicket(CreateIssueRequest request) {
        try {
            JiraIssue issue = jiraApiClient.createIssue(request);
            return jiraIssueRepository.save(issue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Jira ticket: " + e.getMessage(), e);
        }
    }
    
    public JiraIssue getTicket(String issueKey) {
        Optional<JiraIssue> localIssue = jiraIssueRepository.findByKey(issueKey);
        if (localIssue.isPresent()) {
            try {
                JiraIssue remoteIssue = jiraApiClient.getIssue(issueKey);
                return jiraIssueRepository.save(remoteIssue);
            } catch (Exception e) {
                return localIssue.get();
            }
        }
        
        try {
            JiraIssue issue = jiraApiClient.getIssue(issueKey);
            return jiraIssueRepository.save(issue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Jira ticket: " + e.getMessage(), e);
        }
    }
    
    public List<JiraIssue> searchTickets(JiraSearchRequest request) {
        try {
            List<JiraIssue> issues = jiraApiClient.searchIssues(request);
            for (JiraIssue issue : issues) {
                jiraIssueRepository.save(issue);
            }
            return issues;
        } catch (Exception e) {
            if (request.getProjectKey() != null) {
                return jiraIssueRepository.findByProjectKey(request.getProjectKey());
            }
            if (request.getKeyword() != null) {
                return jiraIssueRepository.findByKeyword(request.getKeyword());
            }
            throw new RuntimeException("Failed to search Jira tickets: " + e.getMessage(), e);
        }
    }
    
    public JiraIssue updateTicket(String issueKey, UpdateIssueRequest request) {
        try {
            JiraIssue issue = jiraApiClient.updateIssue(issueKey, request);
            return jiraIssueRepository.save(issue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Jira ticket: " + e.getMessage(), e);
        }
    }
    
    public void addComment(String issueKey, String comment) {
        try {
            jiraApiClient.addComment(issueKey, comment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add comment to Jira ticket: " + e.getMessage(), e);
        }
    }
    
    public JiraIssue createTicketFromRequirement(Project project, Requirement requirement) {
        try {
            CreateIssueRequest request = new CreateIssueRequest();
            request.setProjectKey(determineJiraProjectKey(project));
            request.setSummary(requirement.getText());
            request.setDescription(String.format("Requirement from project: %s\n\nDescription: %s\n\nRequirement: %s", 
                                                project.getName(), 
                                                project.getDescription() != null ? project.getDescription() : "No description", 
                                                requirement.getText()));
            request.setIssueType("Story");
            
            return createTicket(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Jira ticket from requirement: " + e.getMessage(), e);
        }
    }
    
    public List<JiraIssue> getProjectTickets(String projectKey) {
        try {
            JiraSearchRequest request = new JiraSearchRequest();
            request.setProjectKey(projectKey);
            return searchTickets(request);
        } catch (Exception e) {
            return jiraIssueRepository.findByProjectKey(projectKey);
        }
    }
    
    public void syncTicketStatus(String issueKey) {
        try {
            JiraIssue remoteIssue = jiraApiClient.getIssue(issueKey);
            jiraIssueRepository.save(remoteIssue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync ticket status: " + e.getMessage(), e);
        }
    }
    
    public List<JiraProject> getProjects() {
        try {
            List<JiraProject> projects = jiraApiClient.getProjects();
            for (JiraProject project : projects) {
                jiraProjectRepository.save(project);
            }
            return projects;
        } catch (Exception e) {
            return jiraProjectRepository.findAll();
        }
    }
    
    public List<JiraIssue> createTicketsFromProject(String projectName) {
        try {
            Optional<Project> projectOpt = projectService.findProjectByName(projectName);
            if (projectOpt.isEmpty()) {
                throw new RuntimeException("Project not found: " + projectName);
            }
            
            Project project = projectOpt.get();
            if (project.getRequirements().isEmpty()) {
                throw new RuntimeException("Project has no requirements to create tickets from");
            }
            
            List<JiraIssue> createdTickets = new java.util.ArrayList<>();
            for (Requirement requirement : project.getRequirements()) {
                try {
                    JiraIssue ticket = createTicketFromRequirement(project, requirement);
                    createdTickets.add(ticket);
                } catch (Exception e) {
                    System.err.println("Failed to create ticket for requirement: " + requirement.getText() + " - " + e.getMessage());
                }
            }
            
            return createdTickets;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tickets from project: " + e.getMessage(), e);
        }
    }
    
    private String determineJiraProjectKey(Project project) {
        String projectName = project.getName().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (projectName.length() > 10) {
            projectName = projectName.substring(0, 10);
        }
        if (projectName.length() < 2) {
            projectName = "PROJ";
        }
        return projectName;
    }
}
