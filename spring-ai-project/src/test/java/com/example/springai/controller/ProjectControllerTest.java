package com.example.springai.controller;

import com.example.springai.entity.Project;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private MockMvc mockMvc;
    private Project testProject;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        
        testProject = new Project();
        testProject.setId("test-id");
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
    }

    @Test
    public void testGetAllProjects() throws Exception {
        List<Project> projects = Arrays.asList(testProject);
        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    public void testGetProjectByName() throws Exception {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));

        mockMvc.perform(get("/api/projects/Test Project")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));

        verify(projectService, times(1)).findProjectByName("Test Project");
    }

    @Test
    public void testGetProjectByNameNotFound() throws Exception {
        when(projectService.findProjectByName("Non-existent Project")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/Non-existent Project")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).findProjectByName("Non-existent Project");
    }

    @Test
    public void testCreateProject() throws Exception {
        when(projectService.createProject(anyString(), anyString())).thenReturn(testProject);

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Project\",\"description\":\"Test Description\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Project"));

        verify(projectService, times(1)).createProject("Test Project", "Test Description");
    }

    @Test
    public void testCreateProjectMissingName() throws Exception {
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Test Description\"}"))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(anyString(), anyString());
    }
}
