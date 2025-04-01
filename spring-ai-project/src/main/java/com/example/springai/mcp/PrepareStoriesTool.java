package com.example.springai.mcp;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.service.ProjectService;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

/**
 * Tool for preparing user stories for a specific project
 */
@Component("mcpPrepareStoriesTool")
public class PrepareStoriesTool extends ProjectManagementTool {
    private final ProjectService projectService;
    private final OllamaChatClient chatClient;
    private final ObjectMapper objectMapper;

    public PrepareStoriesTool(ProjectService projectService, OllamaChatClient chatClient) {
        super("prepare-stories", "Prepare user stories for a specific project");
        this.projectService = projectService;
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ToolResult execute(Map<String, String> parameters) {
        String projectName = parameters.get("project");
        
        if (projectName == null || projectName.trim().isEmpty()) {
            return ToolResult.failure("Project name is required");
        }
        
        try {
            Optional<Project> projectOpt = projectService.findProjectByName(projectName);
            if (projectOpt.isEmpty()) {
                return ToolResult.failure(String.format("Project '%s' not found. Please check the name and try again.", projectName));
            }
            
            Project project = projectOpt.get();
            if (project.getRequirements().isEmpty()) {
                return ToolResult.failure(String.format("Project '%s' doesn't have any requirements yet. Please add requirements first.", project.getName()));
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
            String responseText = aiResponse.getResult().getOutput().getContent();
            
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
                
                return ToolResult.success(response.toString());
                
            } catch (Exception e) {
                return ToolResult.failure("I encountered an error while analyzing the requirements: " + e.getMessage());
            }
        } catch (Exception e) {
            return ToolResult.failure("Failed to prepare stories: " + e.getMessage());
        }
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"project"};
    }
    
    protected StoryAnalysisResponse parseStoryAnalysisResponse(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, StoryAnalysisResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }
}
