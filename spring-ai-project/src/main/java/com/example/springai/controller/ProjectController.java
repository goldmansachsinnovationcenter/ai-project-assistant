package com.example.springai.controller;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for direct project management operations
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * Get all projects
     * @return List of all projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Get a project by name
     * @param name Project name
     * @return Project if found
     */
    @GetMapping("/{name}")
    public ResponseEntity<Project> getProjectByName(@PathVariable String name) {
        Optional<Project> project = projectService.findProjectByName(name);
        return project.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new project
     * @param projectData Project data
     * @return Created project
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Map<String, String> projectData) {
        String name = projectData.get("name");
        String description = projectData.getOrDefault("description", "");
        
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Project project = projectService.createProject(name, description);
        return ResponseEntity.ok(project);
    }

    /**
     * Add a requirement to a project
     * @param name Project name
     * @param requirementData Requirement data
     * @return Updated project
     */
    @PostMapping("/{name}/requirements")
    public ResponseEntity<?> addRequirement(
            @PathVariable String name,
            @RequestBody Map<String, String> requirementData) {
        
        String text = requirementData.get("text");
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Project> projectOpt = projectService.findProjectByName(name);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Project project = projectOpt.get();
        Requirement requirement = projectService.addRequirement(project, text);
        
        return ResponseEntity.ok(requirement);
    }

    /**
     * Get all requirements for a project
     * @param name Project name
     * @return List of requirements
     */
    @GetMapping("/{name}/requirements")
    public ResponseEntity<?> getRequirements(@PathVariable String name) {
        Optional<Project> projectOpt = projectService.findProjectByName(name);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Project project = projectOpt.get();
        return ResponseEntity.ok(project.getRequirements());
    }
}
