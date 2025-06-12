package com.example.springai.dto;

public class JiraSearchRequest {
    
    private String jql;
    private String projectKey;
    private String status;
    private String assignee;
    private String keyword;
    private int maxResults = 50;
    private int startAt = 0;
    
    public JiraSearchRequest() {}
    
    public JiraSearchRequest(String keyword) {
        this.keyword = keyword;
    }
    
    public String getJql() {
        return jql;
    }
    
    public void setJql(String jql) {
        this.jql = jql;
    }
    
    public String getProjectKey() {
        return projectKey;
    }
    
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAssignee() {
        return assignee;
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public int getMaxResults() {
        return maxResults;
    }
    
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    public int getStartAt() {
        return startAt;
    }
    
    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }
    
    public String buildJql() {
        if (jql != null && !jql.isEmpty()) {
            return jql;
        }
        
        StringBuilder jqlBuilder = new StringBuilder();
        
        if (projectKey != null && !projectKey.isEmpty()) {
            jqlBuilder.append("project = ").append(projectKey);
        }
        
        if (status != null && !status.isEmpty()) {
            if (jqlBuilder.length() > 0) jqlBuilder.append(" AND ");
            jqlBuilder.append("status = \"").append(status).append("\"");
        }
        
        if (assignee != null && !assignee.isEmpty()) {
            if (jqlBuilder.length() > 0) jqlBuilder.append(" AND ");
            jqlBuilder.append("assignee = \"").append(assignee).append("\"");
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            if (jqlBuilder.length() > 0) jqlBuilder.append(" AND ");
            jqlBuilder.append("(summary ~ \"").append(keyword).append("\" OR description ~ \"").append(keyword).append("\")");
        }
        
        if (jqlBuilder.length() == 0) {
            return "order by created DESC";
        }
        
        jqlBuilder.append(" order by created DESC");
        return jqlBuilder.toString();
    }
}
