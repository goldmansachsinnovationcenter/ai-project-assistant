package com.example.springai.tools;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import com.github.ksuid.Ksuid;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectManagementTools {

    private final ProjectService projectService;

    public ProjectManagementTools(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Tool(description = "Create or Add Project for the given Project name and description as strings")
    public Project addProject(String name, String description) {
        System.out.println("Add project tool called");
//        Project prj = new Project();
//        prj.setId(Ksuid.newKsuid().toString());
//        prj.setName(name);
//        prj.setDescription(description);
        return this.projectService.createProject(name, description);
    }

    @Tool(description = "list or show all available Projects")
    public List<Project> listProject() {
        System.out.println("List projects tool called");
        return this.projectService.getAllProjects();
    }

    @Tool(description = "Show details of a specific project by name")
    public String showProject(String name) {
        System.out.println("Show project tool called");
        if (name == null || name.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(name);
            if (projectOpt.isEmpty()) {
                return String.format("Project '%s' not found. Please check the name and try again.", name);
            }
            
            var project = projectOpt.get();
            StringBuilder response = new StringBuilder();
            response.append(String.format("Project: %s\n", project.getName()));
            
            if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                response.append(String.format("Description: %s\n\n", project.getDescription()));
            }
            
            if (project.getRequirements().isEmpty()) {
                response.append("This project doesn't have any requirements yet.");
            } else {
                response.append("Requirements:\n");
                for (var req : project.getRequirements()) {
                    response.append(String.format("- %s\n", req.getText()));
                }
            }
            
            return response.toString();
        } catch (Exception e) {
            return "Failed to show project: " + e.getMessage();
        }
    }

    @Tool(description = "Add a requirement to a specific project")
    public String addRequirement(String project, String requirement) {
        System.out.println("Add requirement tool called");
        if (project == null || project.trim().isEmpty() || 
            requirement == null || requirement.trim().isEmpty()) {
            return "Project name and requirement text are required";
        }
        
        try {
            var projectOpt = this.projectService.findProjectByName(project);
            if (projectOpt.isEmpty()) {
                return String.format("Project '%s' not found. Please check the name and try again.", project);
            }
            
            var projectObj = projectOpt.get();
            this.projectService.addRequirement(projectObj, requirement);
            
            return String.format("Requirement '%s' has been added to project '%s'.", 
                                 requirement, projectObj.getName());
        } catch (Exception e) {
            return "Failed to add requirement: " + e.getMessage();
        }
    }

    @Tool(description = "Create a new project with the given name and description")
    public String createProject(String name, String description) {
        System.out.println("Create project tool called");
        if (name == null || name.trim().isEmpty()) {
            return "Project name is required";
        }
        
        try {
            if (this.projectService.findProjectByName(name).isPresent()) {
                return String.format("Project '%s' already exists", name);
            }
            
            var project = this.projectService.createProject(name, description);
            return String.format("Project '%s' has been created successfully", project.getName());
        } catch (Exception e) {
            return "Failed to create project: " + e.getMessage();
        }
    }

    @Tool(description = "Display available commands and usage examples")
    public String help() {
        System.out.println("Help tool called");
        StringBuilder response = new StringBuilder("Available commands:\n\n");
        
        response.append("1. Create a new project:\n");
        response.append("   Example: create project ProjectName\n\n");
        
        response.append("2. List all projects:\n");
        response.append("   Example: list projects\n\n");
        
        response.append("3. Show project details:\n");
        response.append("   Example: show project ProjectName\n\n");
        
        response.append("4. Add requirement to a project:\n");
        response.append("   Example: add requirement The system should allow user login to project ProjectName\n\n");
        
        if (this.projectService.hasProjects()) {
            var exampleProject = this.projectService.getFirstProject();
            response.append("Example with existing project:\n");
            response.append(String.format("- show project %s\n", exampleProject.getName()));
            response.append(String.format("- add requirement New login feature to project %s\n", exampleProject.getName()));
        }
        
        return response.toString();
    }
}
