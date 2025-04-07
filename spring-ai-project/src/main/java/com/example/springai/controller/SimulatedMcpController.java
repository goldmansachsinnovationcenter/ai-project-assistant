package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for simulating MCP tool calling functionality
 * This is a workaround for the Ollama model not supporting tool calling
 */
@RestController
@RequestMapping("/api/ai/simulated-mcp")
public class SimulatedMcpController {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    private static final Pattern CREATE_PROJECT_PATTERN = Pattern.compile("(?i)create\\s+project\\s+(\\w+)");
    private static final Pattern LIST_PROJECTS_PATTERN = Pattern.compile("(?i)list\\s+projects");
    private static final Pattern SHOW_PROJECT_PATTERN = Pattern.compile("(?i)show\\s+project\\s+(\\w+)");
    private static final Pattern ADD_REQUIREMENT_PATTERN = Pattern.compile("(?i)add\\s+requirement\\s+(.+?)\\s+to\\s+project\\s+(\\w+)");
    
    /**
     * Simulate MCP tool calling by parsing the message and executing the appropriate action
     * @param message User's message
     * @return Response from the simulated tool execution
     */
    @GetMapping("/chat")
    public String simulatedMcpChat(@RequestParam String message) {
        String response;
        
        try {
            Matcher createProjectMatcher = CREATE_PROJECT_PATTERN.matcher(message);
            if (createProjectMatcher.find()) {
                String projectName = createProjectMatcher.group(1);
                response = handleCreateProject(projectName);
            }
            else if (LIST_PROJECTS_PATTERN.matcher(message).find()) {
                response = handleListProjects();
            }
            else if (SHOW_PROJECT_PATTERN.matcher(message).find()) {
                Matcher showProjectMatcher = SHOW_PROJECT_PATTERN.matcher(message);
                showProjectMatcher.find();
                String projectName = showProjectMatcher.group(1);
                response = handleShowProject(projectName);
            }
            else if (ADD_REQUIREMENT_PATTERN.matcher(message).find()) {
                Matcher addRequirementMatcher = ADD_REQUIREMENT_PATTERN.matcher(message);
                addRequirementMatcher.find();
                String requirementText = addRequirementMatcher.group(1);
                String projectName = addRequirementMatcher.group(2);
                response = handleAddRequirement(projectName, requirementText);
            }
            else {
                response = "I'm sorry, I don't understand that command. Please try one of the following:\n" +
                        "- create project ProjectName\n" +
                        "- list projects\n" +
                        "- show project ProjectName\n" +
                        "- add requirement RequirementText to project ProjectName";
            }
            
            saveChatMessage(message, response);
        } catch (Exception e) {
            response = "I'm sorry, I encountered an error processing your request. Please try again.";
            System.err.println("Error in simulated MCP chat: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
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
    
    private void saveChatMessage(String prompt, String response) {
        ChatMessage chatMessage = new ChatMessage(prompt, response);
        chatMessageRepository.save(chatMessage);
    }
}
