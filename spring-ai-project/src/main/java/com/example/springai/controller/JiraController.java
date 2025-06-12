package com.example.springai.controller;

import com.example.springai.entity.JiraIssue;
import com.example.springai.entity.JiraProject;
import com.example.springai.service.JiraService;
import com.example.springai.dto.JiraSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jira")
@CrossOrigin(origins = "*")
public class JiraController {
    
    private final JiraService jiraService;
    
    public JiraController(JiraService jiraService) {
        this.jiraService = jiraService;
    }
    
    @GetMapping("/projects")
    public ResponseEntity<List<JiraProject>> getProjects() {
        try {
            List<JiraProject> projects = jiraService.getProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<JiraIssue>> searchIssues(@RequestParam("q") String query) {
        try {
            JiraSearchRequest request = new JiraSearchRequest(query);
            List<JiraIssue> issues = jiraService.searchTickets(request);
            return ResponseEntity.ok(issues);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/issues/{key}")
    public ResponseEntity<JiraIssue> getIssue(@PathVariable String key) {
        try {
            JiraIssue issue = jiraService.getTicket(key);
            return ResponseEntity.ok(issue);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/projects/{projectKey}/issues")
    public ResponseEntity<List<JiraIssue>> getProjectIssues(@PathVariable String projectKey) {
        try {
            List<JiraIssue> issues = jiraService.getProjectTickets(projectKey);
            return ResponseEntity.ok(issues);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
