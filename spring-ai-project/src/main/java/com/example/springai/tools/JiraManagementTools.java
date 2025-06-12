package com.example.springai.tools;

import com.example.springai.service.JiraService;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class JiraManagementTools {
    
    private final JiraService jiraService;
    private final OllamaChatModel chatModel;
    
    public JiraManagementTools(JiraService jiraService, OllamaChatModel chatModel) {
        this.jiraService = jiraService;
        this.chatModel = chatModel;
        System.out.println("JiraManagementTools bean created successfully!");
    }
}
