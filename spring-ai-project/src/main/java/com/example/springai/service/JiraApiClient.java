package com.example.springai.service;

import com.example.springai.config.JiraConfigProperties;
import com.example.springai.dto.CreateIssueRequest;
import com.example.springai.dto.JiraSearchRequest;
import com.example.springai.dto.UpdateIssueRequest;
import com.example.springai.entity.JiraIssue;
import com.example.springai.entity.JiraProject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class JiraApiClient {
    
    private final RestTemplate restTemplate;
    private final JiraConfigProperties jiraConfig;
    private final ObjectMapper objectMapper;
    
    public JiraApiClient(JiraConfigProperties jiraConfig) {
        this.jiraConfig = jiraConfig;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (jiraConfig.getUsername() != null && jiraConfig.getApiToken() != null) {
            String auth = jiraConfig.getUsername() + ":" + jiraConfig.getApiToken();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
        }
        
        return headers;
    }
    
    public JiraIssue createIssue(CreateIssueRequest request) {
        try {
            String url = jiraConfig.getBaseUrl() + "/rest/api/2/issue";
            
            Map<String, Object> issueData = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            
            Map<String, Object> project = new HashMap<>();
            project.put("key", request.getProjectKey());
            fields.put("project", project);
            
            Map<String, Object> issueType = new HashMap<>();
            issueType.put("name", request.getIssueType() != null ? request.getIssueType() : "Task");
            fields.put("issuetype", issueType);
            
            fields.put("summary", request.getSummary());
            if (request.getDescription() != null) {
                fields.put("description", request.getDescription());
            }
            
            if (request.getPriority() != null) {
                Map<String, Object> priority = new HashMap<>();
                priority.put("name", request.getPriority());
                fields.put("priority", priority);
            }
            
            if (request.getAssignee() != null) {
                Map<String, Object> assignee = new HashMap<>();
                assignee.put("name", request.getAssignee());
                fields.put("assignee", assignee);
            }
            
            if (request.getLabels() != null && !request.getLabels().isEmpty()) {
                fields.put("labels", request.getLabels());
            }
            
            issueData.put("fields", fields);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(issueData, createHeaders());
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode responseNode = objectMapper.readTree(response.getBody());
                String issueKey = responseNode.get("key").asText();
                return getIssue(issueKey);
            }
            
            throw new RuntimeException("Failed to create Jira issue: " + response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating Jira issue: " + e.getMessage(), e);
        }
    }
    
    public JiraIssue getIssue(String issueKey) {
        try {
            String url = jiraConfig.getBaseUrl() + "/rest/api/2/issue/" + issueKey;
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return parseJiraIssue(response.getBody());
            }
            
            throw new RuntimeException("Failed to get Jira issue: " + response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("Error getting Jira issue: " + e.getMessage(), e);
        }
    }
    
    public List<JiraIssue> searchIssues(JiraSearchRequest request) {
        try {
            String url = jiraConfig.getBaseUrl() + "/rest/api/2/search";
            
            Map<String, Object> searchData = new HashMap<>();
            searchData.put("jql", request.buildJql());
            searchData.put("maxResults", request.getMaxResults());
            searchData.put("startAt", request.getStartAt());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(searchData, createHeaders());
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode responseNode = objectMapper.readTree(response.getBody());
                JsonNode issues = responseNode.get("issues");
                
                List<JiraIssue> result = new ArrayList<>();
                for (JsonNode issueNode : issues) {
                    result.add(parseJiraIssueFromNode(issueNode));
                }
                return result;
            }
            
            throw new RuntimeException("Failed to search Jira issues: " + response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("Error searching Jira issues: " + e.getMessage(), e);
        }
    }
    
    public JiraIssue updateIssue(String issueKey, UpdateIssueRequest request) {
        try {
            String url = jiraConfig.getBaseUrl() + "/rest/api/2/issue/" + issueKey;
            
            Map<String, Object> updateData = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            
            if (request.getSummary() != null) {
                fields.put("summary", request.getSummary());
            }
            if (request.getDescription() != null) {
                fields.put("description", request.getDescription());
            }
            if (request.getStatus() != null) {
                Map<String, Object> status = new HashMap<>();
                status.put("name", request.getStatus());
                fields.put("status", status);
            }
            if (request.getPriority() != null) {
                Map<String, Object> priority = new HashMap<>();
                priority.put("name", request.getPriority());
                fields.put("priority", priority);
            }
            if (request.getAssignee() != null) {
                Map<String, Object> assignee = new HashMap<>();
                assignee.put("name", request.getAssignee());
                fields.put("assignee", assignee);
            }
            
            updateData.put("fields", fields);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updateData, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return getIssue(issueKey);
            }
            
            throw new RuntimeException("Failed to update Jira issue: " + response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("Error updating Jira issue: " + e.getMessage(), e);
        }
    }
    
    public void addComment(String issueKey, String comment) {
        try {
            String url = jiraConfig.getBaseUrl() + "/rest/api/2/issue/" + issueKey + "/comment";
            
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("body", comment);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(commentData, createHeaders());
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("Failed to add comment to Jira issue: " + response.getBody());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error adding comment to Jira issue: " + e.getMessage(), e);
        }
    }
    
    public List<JiraProject> getProjects() {
        try {
            String url = jiraConfig.getBaseUrl() + "/rest/api/2/project";
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode projects = objectMapper.readTree(response.getBody());
                List<JiraProject> result = new ArrayList<>();
                
                for (JsonNode projectNode : projects) {
                    JiraProject project = new JiraProject();
                    project.setKey(projectNode.get("key").asText());
                    project.setName(projectNode.get("name").asText());
                    if (projectNode.has("description") && !projectNode.get("description").isNull()) {
                        project.setDescription(projectNode.get("description").asText());
                    }
                    if (projectNode.has("lead") && !projectNode.get("lead").isNull()) {
                        project.setLead(projectNode.get("lead").get("displayName").asText());
                    }
                    result.add(project);
                }
                
                return result;
            }
            
            throw new RuntimeException("Failed to get Jira projects: " + response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("Error getting Jira projects: " + e.getMessage(), e);
        }
    }
    
    private JiraIssue parseJiraIssue(String jsonResponse) {
        try {
            JsonNode issueNode = objectMapper.readTree(jsonResponse);
            return parseJiraIssueFromNode(issueNode);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Jira issue: " + e.getMessage(), e);
        }
    }
    
    private JiraIssue parseJiraIssueFromNode(JsonNode issueNode) {
        JiraIssue issue = new JiraIssue();
        
        issue.setKey(issueNode.get("key").asText());
        
        JsonNode fields = issueNode.get("fields");
        issue.setSummary(fields.get("summary").asText());
        
        if (fields.has("description") && !fields.get("description").isNull()) {
            issue.setDescription(fields.get("description").asText());
        }
        
        if (fields.has("status")) {
            issue.setStatus(fields.get("status").get("name").asText());
        }
        
        if (fields.has("priority") && !fields.get("priority").isNull()) {
            issue.setPriority(fields.get("priority").get("name").asText());
        }
        
        if (fields.has("assignee") && !fields.get("assignee").isNull()) {
            issue.setAssignee(fields.get("assignee").get("displayName").asText());
        }
        
        if (fields.has("reporter") && !fields.get("reporter").isNull()) {
            issue.setReporter(fields.get("reporter").get("displayName").asText());
        }
        
        if (fields.has("project")) {
            issue.setProjectKey(fields.get("project").get("key").asText());
        }
        
        if (fields.has("created")) {
            String createdStr = fields.get("created").asText();
            issue.setCreated(parseJiraDateTime(createdStr));
        }
        
        if (fields.has("updated")) {
            String updatedStr = fields.get("updated").asText();
            issue.setUpdated(parseJiraDateTime(updatedStr));
        }
        
        if (fields.has("labels")) {
            List<String> labels = new ArrayList<>();
            for (JsonNode labelNode : fields.get("labels")) {
                labels.add(labelNode.asText());
            }
            issue.setLabels(labels);
        }
        
        return issue;
    }
    
    private LocalDateTime parseJiraDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr.substring(0, 19), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
