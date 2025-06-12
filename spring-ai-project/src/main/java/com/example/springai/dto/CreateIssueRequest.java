package com.example.springai.dto;

import java.util.List;

public class CreateIssueRequest {
    
    private String projectKey;
    private String summary;
    private String description;
    private String issueType;
    private String priority;
    private String assignee;
    private List<String> labels;
    
    public CreateIssueRequest() {}
    
    public CreateIssueRequest(String projectKey, String summary, String description) {
        this.projectKey = projectKey;
        this.summary = summary;
        this.description = description;
        this.issueType = "Task";
    }
    
    public String getProjectKey() {
        return projectKey;
    }
    
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIssueType() {
        return issueType;
    }
    
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getAssignee() {
        return assignee;
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    
    public List<String> getLabels() {
        return labels;
    }
    
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
