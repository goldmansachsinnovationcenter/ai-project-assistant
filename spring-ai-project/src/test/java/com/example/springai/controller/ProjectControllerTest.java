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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private MockMvc mockMvc;
    private Project project1;
    private Project project2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

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
    void getAllProjects_ReturnsListOfProjects() throws Exception {
        List<Project> projects = Arrays.asList(project1, project2);
        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("proj1_id")))
                .andExpect(jsonPath("$[0].name", is("Project One")))
                .andExpect(jsonPath("$[1].id", is("proj2_id")))
                .andExpect(jsonPath("$[1].name", is("Project Two")));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void createProject_Success_ReturnsCreatedProject() throws Exception {
        when(projectService.createProject(anyString(), anyString())).thenReturn(project1);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Project One\", \"description\":\"Description One\"}"))
                .andExpect(status().isOk()) // Expect 200 OK to match actual implementation
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("proj1_id")))
                .andExpect(jsonPath("$.name", is("Project One")));

        verify(projectService, times(1)).createProject("Project One", "Description One");
    }

     @Test
    void createProject_InvalidInput_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Description One\"}"))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

         mockMvc.perform(post("/api/projects")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content("{\"name\":\"\", \"description\":\"Description One\"}"))
                 .andExpect(status().isBadRequest()); // Expect 400 Bad Request


        verify(projectService, never()).createProject(anyString(), anyString());
    }
}
