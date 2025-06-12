package com.example.springai.controller;

import com.example.springai.entity.JiraIssue;
import com.example.springai.entity.JiraProject;
import com.example.springai.service.JiraService;
import com.example.springai.dto.JiraSearchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JiraControllerTest {

    @Mock
    private JiraService jiraService;

    @InjectMocks
    private JiraController jiraController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private JiraProject sampleProject;
    private JiraIssue sampleIssue;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jiraController).build();
        objectMapper = new ObjectMapper();

        sampleProject = new JiraProject();
        sampleProject.setKey("PROJ");
        sampleProject.setName("Test Project");
        sampleProject.setDescription("Test Description");

        sampleIssue = new JiraIssue();
        sampleIssue.setKey("PROJ-123");
        sampleIssue.setSummary("Test Issue");
        sampleIssue.setDescription("Test Description");
        sampleIssue.setStatus("To Do");
        sampleIssue.setProjectKey("PROJ");
    }

    @Test
    void testGetProjects_Success() throws Exception {
        List<JiraProject> projects = Arrays.asList(sampleProject);
        when(jiraService.getProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/jira/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].key").value("PROJ"))
                .andExpect(jsonPath("$[0].name").value("Test Project"));
    }

    @Test
    void testGetProjects_Error() throws Exception {
        when(jiraService.getProjects()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/jira/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testSearchIssues_Success() throws Exception {
        List<JiraIssue> issues = Arrays.asList(sampleIssue);
        when(jiraService.searchTickets(any(JiraSearchRequest.class))).thenReturn(issues);

        mockMvc.perform(get("/api/jira/search")
                .param("q", "test query")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].key").value("PROJ-123"))
                .andExpect(jsonPath("$[0].summary").value("Test Issue"));
    }

    @Test
    void testSearchIssues_Error() throws Exception {
        when(jiraService.searchTickets(any(JiraSearchRequest.class)))
                .thenThrow(new RuntimeException("Search error"));

        mockMvc.perform(get("/api/jira/search")
                .param("q", "test query")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetIssue_Success() throws Exception {
        when(jiraService.getTicket("PROJ-123")).thenReturn(sampleIssue);

        mockMvc.perform(get("/api/jira/issues/PROJ-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.key").value("PROJ-123"))
                .andExpect(jsonPath("$.summary").value("Test Issue"));
    }

    @Test
    void testGetIssue_NotFound() throws Exception {
        when(jiraService.getTicket("PROJ-999")).thenThrow(new RuntimeException("Issue not found"));

        mockMvc.perform(get("/api/jira/issues/PROJ-999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProjectIssues_Success() throws Exception {
        List<JiraIssue> issues = Arrays.asList(sampleIssue);
        when(jiraService.getProjectTickets("PROJ")).thenReturn(issues);

        mockMvc.perform(get("/api/jira/projects/PROJ/issues")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].key").value("PROJ-123"))
                .andExpect(jsonPath("$[0].projectKey").value("PROJ"));
    }

    @Test
    void testGetProjectIssues_Error() throws Exception {
        when(jiraService.getProjectTickets("PROJ")).thenThrow(new RuntimeException("Project error"));

        mockMvc.perform(get("/api/jira/projects/PROJ/issues")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
