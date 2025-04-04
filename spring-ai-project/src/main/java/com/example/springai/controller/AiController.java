package com.example.springai.controller;

import com.example.springai.entity.*;
import com.example.springai.model.*;
import com.example.springai.service.ProjectService;
import com.example.springai.service.McpToolService;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.mcp.ToolResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private OllamaChatClient chatClient;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private McpToolService mcpToolService;

    private static final Pattern TOOL_CALL_PATTERN = Pattern.compile("\\{\\s*\"tool\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"parameters\"\\s*:\\s*\\{([^}]+)\\}\\s*\\}");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"");

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        String response;
        
        try {
            Prompt prompt = Prompt.builder()
                    .withSystemMessage("You are an AI assistant for project management. " +
                        "If the user is asking to create, list, or show projects, add requirements, or prepare stories, " +
                        "use the appropriate tool to help them.")
                    .withUserMessage(message)
                    .withTools(mcpToolService.getMcpTools())
                    .build();
            
            ChatResponse aiResponse = chatClient.call(prompt);
            
            response = aiResponse.getResult().getOutput().getContent();
            
            System.out.println("DEBUG - LLM Response: " + response);
        } catch (Exception e) {
            response = "I'm sorry, I encountered an error processing your request. Please try again or use one of the available commands. Type 'help' to see the available commands.";
            System.err.println("Error in AI chat: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Save chat message
        saveChatMessage(message, response);
        
        return response;
    }

    private void saveChatMessage(String prompt, String response) {
        ChatMessage chatMessage = new ChatMessage(prompt, response);
        chatMessageRepository.save(chatMessage);
    }

    @GetMapping("/chat-history")
    public List<ChatMessage> getChatHistory(@RequestParam(defaultValue = "20") int limit) {
        return chatMessageRepository.findRecentMessages(limit);
    }

    @GetMapping("/template")
    public String getTemplate(@RequestParam String topic) {
        String prompt = String.format("Give me a summary about %s", topic);
        ChatResponse response = chatClient.call(new Prompt(prompt));
        return response.getResult().getOutput().getContent();
    }
    
    private boolean isCreateProjectCommand(String message) {
        return message.toLowerCase().contains("create project") || 
               message.toLowerCase().contains("new project");
    }
    
    private boolean isListProjectsCommand(String message) {
        return message.toLowerCase().contains("list project") || 
               message.toLowerCase().contains("show all project");
    }
    
    private boolean isShowProjectCommand(String message) {
        return message.toLowerCase().contains("show project") || 
               message.toLowerCase().contains("about project");
    }
    
    private boolean isAddRequirementCommand(String message) {
        return message.toLowerCase().contains("add requirement") && 
               message.toLowerCase().contains("to project");
    }
    
    private boolean isPrepareStoriesCommand(String message) {
        return message.toLowerCase().contains("prepare stories") || 
               message.toLowerCase().contains("analyze requirements");
    }
    
    private boolean isHelpCommand(String message) {
        return message.toLowerCase().contains("help") || 
               message.toLowerCase().contains("commands");
    }
    
    private String extractProjectName(String message) {
        // Simple extraction logic - can be improved
        if (message.contains("project")) {
            int index = message.indexOf("project") + "project".length();
            if (index < message.length()) {
                String rest = message.substring(index).trim();
                if (rest.startsWith(":")) {
                    rest = rest.substring(1).trim();
                }
                if (rest.contains(" ")) {
                    return rest.substring(0, rest.indexOf(" "));
                }
                return rest;
            }
        }
        return "";
    }
    
    private String extractRequirement(String message) {
        if (message.contains("requirement")) {
            int index = message.indexOf("requirement") + "requirement".length();
            if (index < message.length()) {
                String rest = message.substring(index).trim();
                if (rest.contains("to project")) {
                    return rest.substring(0, rest.indexOf("to project")).trim();
                }
                return rest;
            }
        }
        return "";
    }
    
    private String handleCreateProject(String projectName) {
        if (projectName.isEmpty()) {
            return "I couldn't understand the project name. Please try again with a clear project name.";
        }
        
        Project project = projectService.createProject(projectName, "");
        return String.format("Project '%s' has been created. You can now add requirements to it.", project.getName());
    }
    
    private String handleListProjects() {
        if (!projectService.hasProjects()) {
            return "You don't have any projects yet. Would you like to create a new project?";
        }
        
        List<Project> projects = projectService.getAllProjects();
        StringBuilder response = new StringBuilder("Here are your projects:\n");
        for (Project project : projects) {
            response.append("- ").append(project.getName()).append("\n");
        }
        return response.toString();
    }
    
    private String handleShowProject(String projectName) {
        if (projectName.isEmpty()) {
            return "I couldn't understand which project you want to see. Please specify a project name.";
        }
        
        Optional<Project> projectOpt = projectService.findProjectByName(projectName);
        if (projectOpt.isEmpty()) {
            return String.format("Project '%s' not found. Please check the name and try again.", projectName);
        }
        
        Project project = projectOpt.get();
        StringBuilder response = new StringBuilder(String.format("Project: %s\n", project.getName()));
        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            response.append(String.format("Description: %s\n\n", project.getDescription()));
        }
        
        response.append("Requirements:\n");
        if (project.getRequirements().isEmpty()) {
            response.append("No requirements yet.\n");
        } else {
            for (Requirement req : project.getRequirements()) {
                response.append("- ").append(req.getText()).append("\n");
            }
        }
        
        return response.toString();
    }
    
    private String handleAddRequirement(String projectName, String requirementText) {
        if (projectName.isEmpty() || requirementText.isEmpty()) {
            return "I couldn't understand the project name or requirement. Please try again with a clear format.";
        }
        
        Optional<Project> projectOpt = projectService.findProjectByName(projectName);
        if (projectOpt.isEmpty()) {
            return String.format("Project '%s' not found. Please check the name and try again.", projectName);
        }
        
        Project project = projectOpt.get();
        projectService.addRequirement(project, requirementText);
        
        return String.format("Requirement '%s' has been added to project '%s'.", 
                            requirementText, project.getName());
    }
    
    private String handlePrepareStories(String projectName) {
        if (projectName.isEmpty()) {
            return "I couldn't understand which project you want to prepare stories for. Please specify a project name.";
        }
        
        Optional<Project> projectOpt = projectService.findProjectByName(projectName);
        if (projectOpt.isEmpty()) {
            return String.format("Project '%s' not found. Please check the name and try again.", projectName);
        }
        
        Project project = projectOpt.get();
        if (project.getRequirements().isEmpty()) {
            return String.format("Project '%s' doesn't have any requirements yet. Please add requirements first.", project.getName());
        }
        
        // Prepare prompt for AI
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
        
        // Call AI
        ChatResponse aiResponse = chatClient.call(new Prompt(prompt.toString()));
        String responseText = aiResponse.getResult().getOutput().getContent();
        
        try {
            // Parse JSON response
            StoryAnalysisResponse analysisResponse = parseStoryAnalysisResponse(responseText);
            
            // Save to database
            projectService.saveStoryAnalysisResult(project, analysisResponse);
            
            // Format response for user
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
            return "I encountered an error while analyzing the requirements. Please try again.";
        }
    }
    
    private StoryAnalysisResponse parseStoryAnalysisResponse(String jsonString) {
        try {
            // Extract JSON from the response (in case AI included other text)
            Pattern jsonPattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
            java.util.regex.Matcher matcher = jsonPattern.matcher(jsonString);
            if (matcher.find()) {
                jsonString = matcher.group(0);
            }
            
            return objectMapper.readValue(jsonString, StoryAnalysisResponse.class);
        } catch (Exception e) {
            // Fallback to manual parsing
            StoryAnalysisResponse response = new StoryAnalysisResponse();
            response.setStories(new ArrayList<>());
            response.setRisks(new ArrayList<>());
            response.setNfrs(new ArrayList<>());
            response.setQueries(new ArrayList<>());
            response.setSummary("Analysis completed");
            
            try {
                JsonNode rootNode = objectMapper.readTree(jsonString);
                if (rootNode.has("stories")) {
                    for (JsonNode story : rootNode.get("stories")) {
                        response.getStories().add(story.asText());
                    }
                }
                if (rootNode.has("risks")) {
                    for (JsonNode risk : rootNode.get("risks")) {
                        response.getRisks().add(risk.asText());
                    }
                }
                if (rootNode.has("nfrs")) {
                    for (JsonNode nfr : rootNode.get("nfrs")) {
                        response.getNfrs().add(nfr.asText());
                    }
                }
                if (rootNode.has("queries")) {
                    for (JsonNode query : rootNode.get("queries")) {
                        response.getQueries().add(query.asText());
                    }
                }
                if (rootNode.has("summary")) {
                    response.setSummary(rootNode.get("summary").asText());
                }
            } catch (JsonProcessingException ex) {
                // If all parsing fails, return empty response
            }
            
            return response;
        }
    }
    
    private String handleHelpCommand() {
        StringBuilder response = new StringBuilder("Available commands:\n\n");
        
        response.append("1. Create a new project:\n");
        response.append("   Example: create project ProjectName\n\n");
        
        response.append("2. List all projects:\n");
        response.append("   Example: list projects\n\n");
        
        response.append("3. Show project details:\n");
        response.append("   Example: show project ProjectName\n\n");
        
        response.append("4. Add requirement to a project:\n");
        response.append("   Example: add requirement The system should allow user login to project ProjectName\n\n");
        
        response.append("5. Prepare stories for a project:\n");
        response.append("   Example: prepare stories for project ProjectName\n\n");
        
        // Add example with an existing project if available
        if (projectService.hasProjects()) {
            Project exampleProject = projectService.getFirstProject();
            response.append("Example with existing project:\n");
            response.append(String.format("- show project %s\n", exampleProject.getName()));
            response.append(String.format("- add requirement New login feature to project %s\n", exampleProject.getName()));
            response.append(String.format("- prepare stories for project %s\n", exampleProject.getName()));
        }
        
        return response.toString();
    }
    
    private String extractExtractionField(String jsonString, String fieldName) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            if (rootNode.has(fieldName)) {
                return rootNode.get(fieldName).asText();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Check if the LLM response contains a tool call
     * @param response LLM response text
     * @return true if the response contains a tool call
     */
    private boolean containsToolCall(String response) {
        return TOOL_CALL_PATTERN.matcher(response).find();
    }
    
    /**
     * Extract tool name and parameters from LLM response
     * @param response LLM response text
     * @return Map.Entry with tool name as key and parameters map as value, or null if extraction fails
     */
    private Map.Entry<String, Map<String, String>> extractToolCall(String response) {
        Matcher matcher = TOOL_CALL_PATTERN.matcher(response);
        if (matcher.find()) {
            String toolName = matcher.group(1);
            String parametersString = matcher.group(2);
            
            Map<String, String> parameters = new HashMap<>();
            Matcher paramMatcher = PARAMETER_PATTERN.matcher(parametersString);
            while (paramMatcher.find()) {
                String paramName = paramMatcher.group(1);
                String paramValue = paramMatcher.group(2);
                parameters.put(paramName, paramValue);
            }
            
            return Map.entry(toolName, parameters);
        }
        return null;
    }
}
