package com.example.springai.service;

import com.example.springai.entity.*;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RequirementRepository requirementRepository;

    public Project createProject(String name, String description) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        return projectRepository.save(project);
    }

    public Optional<Project> findProjectByName(String name) {
        return projectRepository.findByName(name);
    }

    public boolean hasProjects() {
        return projectRepository.count() > 0;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getFirstProject() {
        return projectRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new RuntimeException("No projects found"));
    }

    public Requirement addRequirement(Project project, String text) {
        Requirement requirement = new Requirement();
        requirement.setText(text);
        requirement.setProject(project);
        return requirementRepository.save(requirement);
    }

    public void saveStoryAnalysisResult(Project project, StoryAnalysisResponse response) {
        // Save stories
        response.getStories().forEach(description -> {
            Story story = new Story();
            story.setDescription(description);
            story.setProject(project);
            project.getStories().add(story);
        });

        // Save risks
        response.getRisks().forEach(description -> {
            Risk risk = new Risk();
            risk.setDescription(description);
            risk.setProject(project);
            project.getRisks().add(risk);
        });

        // Save NFRs
        response.getNfrs().forEach(description -> {
            NFR nfr = new NFR();
            nfr.setDescription(description);
            nfr.setProject(project);
            project.getNfrs().add(nfr);
        });

        // Save queries
        response.getQueries().forEach(question -> {
            Query query = new Query();
            query.setQuestion(question);
            query.setProject(project);
            project.getQueries().add(query);
        });

        projectRepository.save(project);
    }
}
