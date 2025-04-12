package com.example.springai.controller;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
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

    @Test
    void getProjectByName_Found_ReturnsProject() throws Exception {
        when(projectService.findProjectByName("Project One")).thenReturn(Optional.of(project1));

        mockMvc.perform(get("/api/projects/Project One"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("proj1_id")))
                .andExpect(jsonPath("$.name", is("Project One")));

        verify(projectService, times(1)).findProjectByName("Project One");
    }

    @Test
    void getProjectByName_NotFound_ReturnsNotFound() throws Exception {
        when(projectService.findProjectByName("NonExistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/NonExistent"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).findProjectByName("NonExistent");
    }

    @Test
    void addRequirement_Success_ReturnsRequirement() throws Exception {
        Requirement requirement = new Requirement();
        requirement.setId(1L);
        requirement.setText("New requirement text");
        requirement.setProject(project1);

        when(projectService.findProjectByName("Project One")).thenReturn(Optional.of(project1));
        when(projectService.addRequirement(any(Project.class), eq("New requirement text"))).thenReturn(requirement);

        mockMvc.perform(post("/api/projects/Project One/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"New requirement text\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1))) // Check against Integer
                .andExpect(jsonPath("$.text", is("New requirement text")));

        verify(projectService, times(1)).findProjectByName("Project One");
        verify(projectService, times(1)).addRequirement(project1, "New requirement text");
    }

    @Test
    void addRequirement_ProjectNotFound_ReturnsNotFound() throws Exception {
        when(projectService.findProjectByName("NonExistent")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/projects/NonExistent/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"New requirement text\"}"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).findProjectByName("NonExistent");
        verify(projectService, never()).addRequirement(any(), anyString());
    }

    @Test
    void addRequirement_InvalidInput_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/projects/Project One/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/projects/Project One/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).findProjectByName(anyString());
        verify(projectService, never()).addRequirement(any(), anyString());
    }

    @Test
    void getRequirements_Success_ReturnsRequirementsList() throws Exception {
        Requirement req1 = new Requirement();
        req1.setId(1L); req1.setText("Req 1");
        Requirement req2 = new Requirement();
        req2.setId(2L); req2.setText("Req 2");
        project1.setRequirements(Arrays.asList(req1, req2));

        when(projectService.findProjectByName("Project One")).thenReturn(Optional.of(project1));

        mockMvc.perform(get("/api/projects/Project One/requirements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1))) // Check against Integer
                .andExpect(jsonPath("$[1].id", is(2))); // Check against Integer

        verify(projectService, times(1)).findProjectByName("Project One");
    }

    @Test
    void getRequirements_ProjectNotFound_ReturnsNotFound() throws Exception {
        when(projectService.findProjectByName("NonExistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/NonExistent/requirements"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).findProjectByName("NonExistent");
    }

}
