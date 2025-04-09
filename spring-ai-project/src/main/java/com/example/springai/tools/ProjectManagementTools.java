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
        System.out.println("Add project tool called");
        return this.projectService.getAllProjects();
    }

}
