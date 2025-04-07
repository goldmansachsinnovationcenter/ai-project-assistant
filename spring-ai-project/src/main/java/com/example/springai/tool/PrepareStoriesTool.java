package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.service.ProjectService;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.server.McpTool;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tool for preparing user stories for a project
 */
@Component
public class PrepareStoriesTool implements McpTool {
    private final ProjectService projectService;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final String name = "prepare-stories";
    private final String description = "Prepare user stories for a specific project";

    public PrepareStoriesTool(ProjectService projectService, ChatClient chatClient) {
        this.projectService = projectService;
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String execute(Map<String, Object> parameters) {
        String projectName = (String) parameters.get("project");
        
        if (projectName == null || projectName.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            Optional<Project> projectOpt = projectService.findProjectByName(projectName);
            if (projectOpt.isEmpty()) {
                return String.format("Project '%s' not found. Please check the name and try again.", projectName);
            }
            
            Project project = projectOpt.get();
            if (project.getRequirements().isEmpty()) {
                return String.format("Project '%s' doesn't have any requirements yet. Please add requirements first.", project.getName());
            }
            
            StringBuilder prompt = new StringBuilder();
            prompt.append("Analyze the following project requirements and create user stories, identify risks, non-functional requirements, and any clarification queries:\n\n");
            prompt.append("Project: ").append(project.getName()).append("\n");
            if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                prompt.append("Description: ").append(project.getDescription()).append("\n\n");
            }
            
            prompt.append("Requirements:\n");
            for (Requirement req : project.getRequirements()) {
                prompt.append("- ").append(req.getText()).append("\n");
            }
            
            prompt.append("\nPlease return a JSON with the following structure:\n");
            prompt.append("{\n");
            prompt.append("  \"stories\": [\"story 1\", \"story 2\", ...],\n");
            prompt.append("  \"risks\": [\"risk 1\", \"risk 2\", ...],\n");
            prompt.append("  \"nfrs\": [\"nfr 1\", \"nfr 2\", ...],\n");
            prompt.append("  \"queries\": [\"query 1\", \"query 2\", ...],\n");
            prompt.append("  \"summary\": \"overall project summary\"\n");
            prompt.append("}\n");
            
            ChatResponse aiResponse = chatClient.call(new Prompt(prompt.toString()));
            String responseText = aiResponse.getResult().getContent();
            
            try {
                StoryAnalysisResponse analysisResponse = parseStoryAnalysisResponse(responseText);
                
                projectService.saveStoryAnalysisResult(project, analysisResponse);
                
                StringBuilder response = new StringBuilder();
                response.append(String.format("I've analyzed the requirements for project '%s' and created:\n\n", project.getName()));
                
                response.append("User Stories:\n");
                for (String story : analysisResponse.getStories()) {
                    response.append("- ").append(story).append("\n");
                }
                
                response.append("\nRisks:\n");
                for (String risk : analysisResponse.getRisks()) {
                    response.append("- ").append(risk).append("\n");
                }
                
                response.append("\nNon-Functional Requirements:\n");
                for (String nfr : analysisResponse.getNfrs()) {
                    response.append("- ").append(nfr).append("\n");
                }
                
                response.append("\nQueries for Clarification:\n");
                for (String query : analysisResponse.getQueries()) {
                    response.append("- ").append(query).append("\n");
                }
                
                response.append("\nSummary: ").append(analysisResponse.getSummary());
                
                return response.toString();
                
            } catch (Exception e) {
                return "I encountered an error while analyzing the requirements: " + e.getMessage();
            }
        } catch (Exception e) {
            return "Failed to prepare stories: " + e.getMessage();
        }
    }

    @Override
    public Map<String, Object> getParameters() {
        return Map.of("project", "string");
    }
    
    protected StoryAnalysisResponse parseStoryAnalysisResponse(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, StoryAnalysisResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }
}
