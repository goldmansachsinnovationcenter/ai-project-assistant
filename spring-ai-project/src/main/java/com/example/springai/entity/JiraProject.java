package com.example.springai.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jira_projects")
public class JiraProject {
    
    @Id
    @Column(name = "project_key")
    private String key;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String lead;
    
    @ElementCollection
    @CollectionTable(name = "jira_project_issue_types", joinColumns = @JoinColumn(name = "project_key"))
    @Column(name = "issue_type")
    private List<String> issueTypes = new ArrayList<>();
    
    public JiraProject() {}
    
    public JiraProject(String key, String name, String description) {
        this.key = key;
        this.name = name;
        this.description = description;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLead() {
        return lead;
    }
    
    public void setLead(String lead) {
        this.lead = lead;
    }
    
    public List<String> getIssueTypes() {
        return issueTypes;
    }
    
    public void setIssueTypes(List<String> issueTypes) {
        this.issueTypes = issueTypes;
    }
    
    public void addIssueType(String issueType) {
        if (!this.issueTypes.contains(issueType)) {
            this.issueTypes.add(issueType);
        }
    }
}
