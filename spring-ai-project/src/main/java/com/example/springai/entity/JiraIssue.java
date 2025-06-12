package com.example.springai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jira_issues")
public class JiraIssue {
    
    @Id
    @Column(name = "`key`")
    private String key;
    
    @Column(nullable = false)
    private String summary;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String status;
    
    private String priority;
    private String assignee;
    private String reporter;
    
    @Column(name = "created_date")
    private LocalDateTime created;
    
    @Column(name = "updated_date")
    private LocalDateTime updated;
    
    @Column(name = "project_key")
    private String projectKey;
    
    @ElementCollection
    @CollectionTable(name = "jira_issue_labels", joinColumns = @JoinColumn(name = "issue_key"))
    @Column(name = "label")
    private List<String> labels = new ArrayList<>();
    
    @OneToMany(mappedBy = "issueKey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JiraComment> comments = new ArrayList<>();
    
    public JiraIssue() {}
    
    public JiraIssue(String key, String summary, String description, String status) {
        this.key = key;
        this.summary = summary;
        this.description = description;
        this.status = status;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getReporter() {
        return reporter;
    }
    
    public void setReporter(String reporter) {
        this.reporter = reporter;
    }
    
    public LocalDateTime getCreated() {
        return created;
    }
    
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
    
    public LocalDateTime getUpdated() {
        return updated;
    }
    
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
    
    public String getProjectKey() {
        return projectKey;
    }
    
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
    
    public List<String> getLabels() {
        return labels;
    }
    
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
    
    public List<JiraComment> getComments() {
        return comments;
    }
    
    public void setComments(List<JiraComment> comments) {
        this.comments = comments;
    }
    
    public void addComment(JiraComment comment) {
        comments.add(comment);
        comment.setIssueKey(this.key);
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}
