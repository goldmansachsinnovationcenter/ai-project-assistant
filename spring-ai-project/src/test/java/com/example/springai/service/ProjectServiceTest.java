package com.example.springai.service;

import com.example.springai.entity.Requirement;
import com.example.springai.repository.RequirementRepository;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.entity.Story;
import com.example.springai.entity.Risk;
import com.example.springai.entity.NFR;
import com.example.springai.entity.Query;
import java.util.ArrayList;
import java.util.Collections;

import com.example.springai.entity.Project;
import com.example.springai.repository.ProjectRepository;
import com.github.ksuid.Ksuid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;


    @Mock
    private RequirementRepository requirementRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project1;
    private Project project2;

    @BeforeEach
    void setUp() {
        project1 = new Project();
        project1.setId("proj1_id");
        project1.setName("Project One");
        project1.setDescription("Description One");

        project2 = new Project();
        project2.setId("proj2_id");
        project2.setName("Project Two");
        project2.setDescription("Description Two");
    }

    @Test
    void createProject_Success() {
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            assertNotNull(p.getId(), "ID should be generated before saving");
            assertEquals("New Project", p.getName());
            assertEquals("New Desc", p.getDescription());
            return p; // Return the saved project
        });

        Project createdProject = projectService.createProject("New Project", "New Desc");

        assertNotNull(createdProject);
        assertEquals("New Project", createdProject.getName());
        assertEquals("New Desc", createdProject.getDescription());
        assertNotNull(createdProject.getId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void getAllProjects_ReturnsListOfProjects() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project1, project2));

        List<Project> projects = projectService.getAllProjects();

        assertNotNull(projects);
        assertEquals(2, projects.size());
        assertEquals("Project One", projects.get(0).getName());
        assertEquals("Project Two", projects.get(1).getName());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void findProjectByName_ProjectExists_ReturnsProject() {
        when(projectRepository.findByName("Project One")).thenReturn(Optional.of(project1));

        Optional<Project> foundProject = projectService.findProjectByName("Project One");

        assertTrue(foundProject.isPresent());
        assertEquals("proj1_id", foundProject.get().getId());
        verify(projectRepository, times(1)).findByName("Project One");
    }

    @Test
    void findProjectByName_ProjectDoesNotExist_ReturnsEmptyOptional() {
        when(projectRepository.findByName("Non Existent Project")).thenReturn(Optional.empty());

        Optional<Project> foundProject = projectService.findProjectByName("Non Existent Project");

        assertFalse(foundProject.isPresent());
        verify(projectRepository, times(1)).findByName("Non Existent Project");
    }

    @Test
    void hasProjects_WhenProjectsExist_ReturnsTrue() {
        when(projectRepository.count()).thenReturn(5L); // Mock count > 0

        boolean result = projectService.hasProjects();

        assertTrue(result);
        verify(projectRepository, times(1)).count();
    }

    @Test
    void hasProjects_WhenNoProjectsExist_ReturnsFalse() {
        when(projectRepository.count()).thenReturn(0L); // Mock count = 0

        boolean result = projectService.hasProjects();

        assertFalse(result);
        verify(projectRepository, times(1)).count();
    }

    @Test
    void addRequirement_Success() {
        Requirement requirement = new Requirement();
        requirement.setText("New Req");
        requirement.setProject(project1);

        when(requirementRepository.save(any(Requirement.class))).thenReturn(requirement);

        Requirement savedRequirement = projectService.addRequirement(project1, "New Req");

        assertNotNull(savedRequirement);
        assertEquals("New Req", savedRequirement.getText());
        assertEquals(project1, savedRequirement.getProject());
        verify(requirementRepository, times(1)).save(any(Requirement.class));
    }

    @Test
    void getFirstProject_WhenProjectsExist_ReturnsFirstProject() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project1, project2));

        Project firstProject = projectService.getFirstProject();

        assertNotNull(firstProject);
        assertEquals(project1.getId(), firstProject.getId());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void getFirstProject_WhenNoProjectsExist_ThrowsException() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.getFirstProject();
        });

        assertEquals("No projects found", exception.getMessage());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void saveStoryAnalysisResult_SavesAllEntities() {
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setStories(Arrays.asList("Story 1", "Story 2"));
        response.setRisks(Arrays.asList("Risk 1"));
        response.setNfrs(Arrays.asList("NFR 1", "NFR 2", "NFR 3"));
        response.setQueries(Arrays.asList("Query 1"));

        project1.setStories(new ArrayList<>());
        project1.setRisks(new ArrayList<>());
        project1.setNfrs(new ArrayList<>());
        project1.setQueries(new ArrayList<>());

        when(projectRepository.save(any(Project.class))).thenReturn(project1);

        projectService.saveStoryAnalysisResult(project1, response);

        assertEquals(2, project1.getStories().size());
        assertEquals("Story 1", project1.getStories().get(0).getDescription());
        assertEquals(project1, project1.getStories().get(0).getProject());

        assertEquals(1, project1.getRisks().size());
        assertEquals("Risk 1", project1.getRisks().get(0).getDescription());
        assertEquals(project1, project1.getRisks().get(0).getProject());

        assertEquals(3, project1.getNfrs().size());
        assertEquals("NFR 1", project1.getNfrs().get(0).getDescription());
        assertEquals(project1, project1.getNfrs().get(0).getProject());

        assertEquals(1, project1.getQueries().size());
        assertEquals("Query 1", project1.getQueries().get(0).getQuestion());
        assertEquals(project1, project1.getQueries().get(0).getProject());

        verify(projectRepository, times(1)).save(project1);
    }



}
