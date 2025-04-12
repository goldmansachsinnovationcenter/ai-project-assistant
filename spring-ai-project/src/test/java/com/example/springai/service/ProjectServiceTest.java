package com.example.springai.service;

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

}
