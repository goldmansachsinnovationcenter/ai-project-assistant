package com.example.springai.config;

import com.example.springai.service.ProjectService;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpTool;
import org.springframework.ai.mcp.server.McpToolDefinition;
import org.springframework.ai.mcp.server.McpToolParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for MCP server and tools
 */
@Configuration
public class McpConfig {

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private McpServer mcpServer;

    /**
     * Register MCP tools with the server
     */
    @Bean
    public void registerMcpTools() {
        McpToolDefinition createProjectTool = McpToolDefinition.builder()
                .name("create-project")
                .description("Create a new project with the given name")
                .addParameter(McpToolParameter.builder()
                        .name("name")
                        .description("Name of the project to create")
                        .type("string")
                        .required(true)
                        .build())
                .addParameter(McpToolParameter.builder()
                        .name("description")
                        .description("Description of the project")
                        .type("string")
                        .required(false)
                        .build())
                .build();
        
        McpToolDefinition listProjectsTool = McpToolDefinition.builder()
                .name("list-projects")
                .description("List all available projects")
                .build();
        
        McpToolDefinition showProjectTool = McpToolDefinition.builder()
                .name("show-project")
                .description("Show details of a specific project")
                .addParameter(McpToolParameter.builder()
                        .name("name")
                        .description("Name of the project to show")
                        .type("string")
                        .required(true)
                        .build())
                .build();
        
        McpToolDefinition addRequirementTool = McpToolDefinition.builder()
                .name("add-requirement")
                .description("Add a requirement to a project")
                .addParameter(McpToolParameter.builder()
                        .name("project_name")
                        .description("Name of the project to add the requirement to")
                        .type("string")
                        .required(true)
                        .build())
                .addParameter(McpToolParameter.builder()
                        .name("requirement_text")
                        .description("Text of the requirement to add")
                        .type("string")
                        .required(true)
                        .build())
                .build();
        
        McpToolDefinition prepareStoriesTool = McpToolDefinition.builder()
                .name("prepare-stories")
                .description("Generate user stories, risks, and NFRs for a project")
                .addParameter(McpToolParameter.builder()
                        .name("project_name")
                        .description("Name of the project to prepare stories for")
                        .type("string")
                        .required(true)
                        .build())
                .build();
        
        McpToolDefinition helpTool = McpToolDefinition.builder()
                .name("help")
                .description("Show available commands and how to use them")
                .build();
        
        mcpServer.registerTool(createProjectTool, this::executeCreateProject);
        mcpServer.registerTool(listProjectsTool, this::executeListProjects);
        mcpServer.registerTool(showProjectTool, this::executeShowProject);
        mcpServer.registerTool(addRequirementTool, this::executeAddRequirement);
        mcpServer.registerTool(prepareStoriesTool, this::executePrepareStories);
        mcpServer.registerTool(helpTool, this::executeHelp);
    }
    
    private String executeCreateProject(Map<String, Object> parameters) {
        String name = (String) parameters.get("name");
        String description = parameters.getOrDefault("description", "").toString();
        
        if (name == null || name.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            if (projectService.findProjectByName(name).isPresent()) {
                return String.format("Project '%s' already exists", name);
            }
            
            projectService.createProject(name, description);
            return String.format("Project '%s' has been created successfully. You can now add requirements to it.", name);
        } catch (Exception e) {
            return "Failed to create project: " + e.getMessage();
        }
    }
    
    private String executeListProjects(Map<String, Object> parameters) {
        try {
            if (!projectService.hasProjects()) {
                return "You don't have any projects yet. Would you like to create a new project?";
            }
            
            StringBuilder response = new StringBuilder("Here are your projects:\n");
            projectService.getAllProjects().forEach(project -> 
                response.append("- ").append(project.getName()).append("\n")
            );
            
            return response.toString();
        } catch (Exception e) {
            return "Failed to list projects: " + e.getMessage();
        }
    }
    
    private String executeShowProject(Map<String, Object> parameters) {
        String name = (String) parameters.get("name");
        
        if (name == null || name.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            return projectService.findProjectByName(name)
                .map(project -> {
                    StringBuilder response = new StringBuilder(String.format("Project: %s\n", project.getName()));
                    
                    if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                        response.append(String.format("Description: %s\n\n", project.getDescription()));
                    }
                    
                    response.append("Requirements:\n");
                    if (project.getRequirements().isEmpty()) {
                        response.append("No requirements yet.\n");
                    } else {
                        project.getRequirements().forEach(req -> 
                            response.append("- ").append(req.getText()).append("\n")
                        );
                    }
                    
                    return response.toString();
                })
                .orElse(String.format("Project '%s' not found", name));
        } catch (Exception e) {
            return "Failed to show project: " + e.getMessage();
        }
    }
    
    private String executeAddRequirement(Map<String, Object> parameters) {
        String projectName = (String) parameters.get("project_name");
        String requirementText = (String) parameters.get("requirement_text");
        
        if (projectName == null || projectName.trim().isEmpty()) {
            return "Project name is required";
        }
        
        if (requirementText == null || requirementText.trim().isEmpty()) {
            return "Requirement text is required";
        }
        
        try {
            return projectService.findProjectByName(projectName)
                .map(project -> {
                    projectService.addRequirement(project, requirementText);
                    return String.format("Requirement '%s' has been added to project '%s'", 
                                        requirementText, project.getName());
                })
                .orElse(String.format("Project '%s' not found", projectName));
        } catch (Exception e) {
            return "Failed to add requirement: " + e.getMessage();
        }
    }
    
    private String executePrepareStories(Map<String, Object> parameters) {
        String projectName = (String) parameters.get("project_name");
        
        if (projectName == null || projectName.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            return projectService.findProjectByName(projectName)
                .map(project -> {
                    if (project.getRequirements().isEmpty()) {
                        return String.format("Project '%s' has no requirements. Please add requirements first.", project.getName());
                    }
                    
                    return String.format("Preparing stories for project '%s'. This feature is not fully implemented yet.", project.getName());
                })
                .orElse(String.format("Project '%s' not found", projectName));
        } catch (Exception e) {
            return "Failed to prepare stories: " + e.getMessage();
        }
    }
    
    private String executeHelp(Map<String, Object> parameters) {
        return "Available commands:\n\n" +
               "- create-project: Create a new project\n" +
               "  Parameters: name (required), description (optional)\n\n" +
               "- list-projects: List all available projects\n\n" +
               "- show-project: Show details of a specific project\n" +
               "  Parameters: name (required)\n\n" +
               "- add-requirement: Add a requirement to a project\n" +
               "  Parameters: project_name (required), requirement_text (required)\n\n" +
               "- prepare-stories: Generate user stories, risks, and NFRs for a project\n" +
               "  Parameters: project_name (required)\n\n" +
               "- help: Show this help message";
    }
}
