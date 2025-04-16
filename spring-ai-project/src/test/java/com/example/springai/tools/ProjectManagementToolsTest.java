package com.example.springai.tools;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectManagementToolsTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectManagementTools projectManagementTools;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId("test-id");
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setRequirements(new ArrayList<>());
    }

    @Test
    void testAddProject() {
        when(projectService.createProject(anyString(), anyString())).thenReturn(testProject);

        Project result = projectManagementTools.addProject("Test Project", "Test Description");

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        assertEquals("Test Description", result.getDescription());
        verify(projectService).createProject("Test Project", "Test Description");
    }

    @Test
    void testListProject() {
        List<Project> projects = List.of(testProject);
        when(projectService.getAllProjects()).thenReturn(projects);

        List<Project> result = projectManagementTools.listProject();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        verify(projectService).getAllProjects();
    }

    @Test
    void testShowProject_Success() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));

        String result = projectManagementTools.showProject("Test Project");

        assertTrue(result.contains("Project: Test Project"));
        assertTrue(result.contains("Description: Test Description"));
        assertTrue(result.contains("This project doesn't have any requirements yet."));
        verify(projectService).findProjectByName("Test Project");
    }

    @Test
    void testShowProject_WithRequirements() {
        Requirement req = new Requirement();
        req.setText("Test Requirement");
        testProject.getRequirements().add(req);
        
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));

        String result = projectManagementTools.showProject("Test Project");

        assertTrue(result.contains("Project: Test Project"));
        assertTrue(result.contains("Requirements:"));
        assertTrue(result.contains("- Test Requirement"));
        verify(projectService).findProjectByName("Test Project");
    }

    @Test
    void testShowProject_NotFound() {
        when(projectService.findProjectByName("Non-existent")).thenReturn(Optional.empty());

        String result = projectManagementTools.showProject("Non-existent");

        assertTrue(result.contains("not found"));
        verify(projectService).findProjectByName("Non-existent");
    }

    @Test
    void testShowProject_NullName() {
        String result = projectManagementTools.showProject(null);
        assertEquals("Project name is required", result);
    }

    @Test
    void testAddRequirement_Success() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        String result = projectManagementTools.addRequirement("Test Project", "New Requirement");

        assertTrue(result.contains("has been added to project"));
        verify(projectService).findProjectByName("Test Project");
        verify(projectService).addRequirement(testProject, "New Requirement");
    }

    @Test
    void testAddRequirement_ProjectNotFound() {
        when(projectService.findProjectByName("Non-existent")).thenReturn(Optional.empty());

        String result = projectManagementTools.addRequirement("Non-existent", "New Requirement");

        assertTrue(result.contains("not found"));
        verify(projectService).findProjectByName("Non-existent");
        verify(projectService, never()).addRequirement(any(), anyString());
    }

    @Test
    void testAddRequirement_MissingParams() {
        String result1 = projectManagementTools.addRequirement(null, "Req");
        String result2 = projectManagementTools.addRequirement("Proj", null);
        String result3 = projectManagementTools.addRequirement("", "");
        
        assertEquals("Project name and requirement text are required", result1);
        assertEquals("Project name and requirement text are required", result2);
        assertEquals("Project name and requirement text are required", result3);
    }

    @Test
    void testCreateProject_Success() {
        when(projectService.findProjectByName("New Project")).thenReturn(Optional.empty());
        when(projectService.createProject("New Project", "New Description")).thenReturn(testProject);

        String result = projectManagementTools.createProject("New Project", "New Description");

        assertTrue(result.contains("has been created successfully"));
        verify(projectService).findProjectByName("New Project");
        verify(projectService).createProject("New Project", "New Description");
    }

    @Test
    void testCreateProject_AlreadyExists() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));

        String result = projectManagementTools.createProject("Test Project", "New Description");

        assertTrue(result.contains("already exists"));
        verify(projectService).findProjectByName("Test Project");
        verify(projectService, never()).createProject(anyString(), anyString());
    }

    @Test
    void testCreateProject_NullName() {
        String result = projectManagementTools.createProject(null, "Desc");
        assertEquals("Project name is required", result);
    }

    @Test
    void testHelp_NoProjects() {
        when(projectService.hasProjects()).thenReturn(false);

        String result = projectManagementTools.help();

        assertTrue(result.contains("Available commands"));
        assertTrue(result.contains("Create a new project"));
        assertTrue(result.contains("List all projects"));
        assertTrue(result.contains("Show project details"));
        assertTrue(result.contains("Add requirement to a project"));
        assertFalse(result.contains("Example with existing project"));
        verify(projectService).hasProjects();
    }

    @Test
    void testHelp_WithProjects() {
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getFirstProject()).thenReturn(testProject);

        String result = projectManagementTools.help();

        assertTrue(result.contains("Available commands"));
        assertTrue(result.contains("Example with existing project"));
        assertTrue(result.contains("Test Project"));
        verify(projectService).hasProjects();
        verify(projectService).getFirstProject();
    }
}
