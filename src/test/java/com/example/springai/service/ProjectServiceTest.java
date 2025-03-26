package com.example.springai.service;

import com.example.springai.entity.*;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RequirementRepository requirementRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Requirement testRequirement;

    @BeforeEach
    public void setup() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");

        testRequirement = new Requirement();
        testRequirement.setId(1L);
        testRequirement.setText("Test Requirement");
        testRequirement.setProject(testProject);
    }

    @Test
    public void testCreateProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.createProject("Test Project", "Test Description");

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        assertEquals("Test Description", result.getDescription());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    public void testFindProjectByName() {
        when(projectRepository.findByName("Test Project")).thenReturn(Optional.of(testProject));

        Optional<Project> result = projectService.findProjectByName("Test Project");

        assertTrue(result.isPresent());
        assertEquals("Test Project", result.get().getName());
        verify(projectRepository).findByName("Test Project");
    }

    @Test
    public void testHasProjects_True() {
        when(projectRepository.count()).thenReturn(5L);

        boolean result = projectService.hasProjects();

        assertTrue(result);
        verify(projectRepository).count();
    }

    @Test
    public void testHasProjects_False() {
        when(projectRepository.count()).thenReturn(0L);

        boolean result = projectService.hasProjects();

        assertFalse(result);
        verify(projectRepository).count();
    }

    @Test
    public void testGetAllProjects() {
        List<Project> projects = Arrays.asList(testProject);
        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        verify(projectRepository).findAll();
    }

    @Test
    public void testGetFirstProject() {
        List<Project> projects = Arrays.asList(testProject);
        when(projectRepository.findAll()).thenReturn(projects);

        Project result = projectService.getFirstProject();

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository).findAll();
    }

    @Test
    public void testGetFirstProject_NoProjects() {
        when(projectRepository.findAll()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.getFirstProject();
        });

        assertEquals("No projects found", exception.getMessage());
        verify(projectRepository).findAll();
    }

    @Test
    public void testAddRequirement() {
        when(requirementRepository.save(any(Requirement.class))).thenReturn(testRequirement);

        Requirement result = projectService.addRequirement(testProject, "Test Requirement");

        assertNotNull(result);
        assertEquals("Test Requirement", result.getText());
        assertEquals(testProject, result.getProject());
        verify(requirementRepository).save(any(Requirement.class));
    }

    @Test
    public void testSaveStoryAnalysisResult() {
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setStories(Arrays.asList("Story 1", "Story 2"));
        response.setRisks(Arrays.asList("Risk 1", "Risk 2"));
        response.setNfrs(Arrays.asList("NFR 1", "NFR 2"));
        response.setQueries(Arrays.asList("Query 1", "Query 2"));
        response.setSummary("Test Summary");

        projectService.saveStoryAnalysisResult(testProject, response);

        verify(projectRepository).save(testProject);
    }
}
