package com.example.springai.entity;

import com.github.ksuid.Ksuid;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jira_comments")
public class JiraComment {
    
    @Id
    private String id;
    
    @Column(name = "issue_key", nullable = false)
    private String issueKey;
    
    @Column(nullable = false)
    private String author;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;
    
    @Column(name = "created_date")
    private LocalDateTime created;
    
    @Column(name = "updated_date")
    private LocalDateTime updated;
    
    public JiraComment() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = Ksuid.newKsuid().toString();
        }
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
    
    public JiraComment(String issueKey, String author, String body) {
        this();
        this.issueKey = issueKey;
        this.author = author;
        this.body = body;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getIssueKey() {
        return issueKey;
    }
    
    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
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
    
    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}
