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

import java.util.*;

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

    @BeforeEach
    public void setup() {
        testProject = new Project();
        testProject.setId("test-id");
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setStories(new ArrayList<>());
        testProject.setRisks(new ArrayList<>());
        testProject.setNfrs(new ArrayList<>());
        testProject.setQueries(new ArrayList<>());
    }

    @Test
    public void testCreateProject() {
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Project result = projectService.createProject("Test Project", "Test Description");
        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals("Test Description", result.getDescription());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(testProject));
        
        List<Project> projects = projectService.getAllProjects();
        
        assertNotNull(projects);
        assertEquals(1, projects.size());
        assertEquals("Test Project", projects.get(0).getName());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testFindProjectByName() {
        when(projectRepository.findByName("Test Project")).thenReturn(Optional.of(testProject));
        
        Optional<Project> projectOpt = projectService.findProjectByName("Test Project");
        
        assertTrue(projectOpt.isPresent());
        assertEquals("Test Project", projectOpt.get().getName());
        verify(projectRepository, times(1)).findByName("Test Project");
    }

    @Test
    public void testFindProjectByNameNotFound() {
        when(projectRepository.findByName("Nonexistent Project")).thenReturn(Optional.empty());
        
        Optional<Project> projectOpt = projectService.findProjectByName("Nonexistent Project");
        
        assertFalse(projectOpt.isPresent());
        verify(projectRepository, times(1)).findByName("Nonexistent Project");
    }

    @Test
    public void testHasProjects() {
        when(projectRepository.count()).thenReturn(5L);
        
        boolean result = projectService.hasProjects();
        
        assertTrue(result);
        verify(projectRepository, times(1)).count();
    }

    @Test
    public void testHasProjectsEmpty() {
        when(projectRepository.count()).thenReturn(0L);
        
        boolean result = projectService.hasProjects();
        
        assertFalse(result);
        verify(projectRepository, times(1)).count();
    }

    @Test
    public void testGetFirstProject() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(testProject));
        
        Project result = projectService.getFirstProject();
        
        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testGetFirstProjectEmpty() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.getFirstProject();
        });
        
        assertEquals("No projects found", exception.getMessage());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testAddRequirement() {
        when(requirementRepository.save(any(Requirement.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Requirement requirement = projectService.addRequirement(testProject, "Test Requirement");
        
        assertNotNull(requirement);
        assertEquals("Test Requirement", requirement.getText());
        assertEquals(testProject, requirement.getProject());
        verify(requirementRepository, times(1)).save(any(Requirement.class));
    }

    @Test
    public void testSaveStoryAnalysisResult() {
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setStories(Arrays.asList("Story 1", "Story 2"));
        response.setRisks(Arrays.asList("Risk 1", "Risk 2"));
        response.setNfrs(Arrays.asList("NFR 1", "NFR 2"));
        response.setQueries(Arrays.asList("Query 1", "Query 2"));
        response.setSummary("Test Summary");
        
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        projectService.saveStoryAnalysisResult(testProject, response);
        
        assertEquals(2, testProject.getStories().size());
        assertEquals(2, testProject.getRisks().size());
        assertEquals(2, testProject.getNfrs().size());
        assertEquals(2, testProject.getQueries().size());
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    public void testSaveStoryAnalysisResultWithNullLists() {
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setStories(Collections.emptyList());
        response.setRisks(Collections.emptyList());
        response.setNfrs(Collections.emptyList());
        response.setQueries(Collections.emptyList());
        response.setSummary("Test Summary");
        
        projectService.saveStoryAnalysisResult(testProject, response);
        
        assertEquals(0, testProject.getStories().size());
        assertEquals(0, testProject.getRisks().size());
        assertEquals(0, testProject.getNfrs().size());
        assertEquals(0, testProject.getQueries().size());
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    public void testSaveStoryAnalysisResultWithEmptyLists() {
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setStories(Collections.emptyList());
        response.setRisks(Collections.emptyList());
        response.setNfrs(Collections.emptyList());
        response.setQueries(Collections.emptyList());
        response.setSummary("Test Summary");
        
        projectService.saveStoryAnalysisResult(testProject, response);
        
        assertEquals(0, testProject.getStories().size());
        assertEquals(0, testProject.getRisks().size());
        assertEquals(0, testProject.getNfrs().size());
        assertEquals(0, testProject.getQueries().size());
        verify(projectRepository, times(1)).save(testProject);
    }
}
