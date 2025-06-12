package com.example.springai.service;

import com.example.springai.dto.CreateIssueRequest;
import com.example.springai.dto.JiraSearchRequest;
import com.example.springai.dto.UpdateIssueRequest;
import com.example.springai.entity.JiraIssue;
import com.example.springai.entity.JiraProject;
import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.repository.JiraIssueRepository;
import com.example.springai.repository.JiraProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JiraServiceTest {

    @Mock
    private JiraApiClient jiraApiClient;

    @Mock
    private JiraIssueRepository jiraIssueRepository;

    @Mock
    private JiraProjectRepository jiraProjectRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private JiraService jiraService;

    private JiraIssue sampleIssue;
    private JiraProject sampleProject;
    private Project sampleProjectEntity;
    private Requirement sampleRequirement;

    @BeforeEach
    void setUp() {
        sampleIssue = new JiraIssue();
        sampleIssue.setKey("PROJ-123");
        sampleIssue.setSummary("Test Issue");
        sampleIssue.setDescription("Test Description");
        sampleIssue.setStatus("To Do");
        sampleIssue.setProjectKey("PROJ");

        sampleProject = new JiraProject();
        sampleProject.setKey("PROJ");
        sampleProject.setName("Test Project");

        sampleProjectEntity = new Project();
        sampleProjectEntity.setName("Test Project");
        sampleProjectEntity.setDescription("Test Description");

        sampleRequirement = new Requirement();
        sampleRequirement.setText("Test requirement");
    }

    @Test
    void testCreateTicket_Success() {
        CreateIssueRequest request = new CreateIssueRequest("PROJ", "Test Issue", "Test Description");
        when(jiraApiClient.createIssue(request)).thenReturn(sampleIssue);
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        JiraIssue result = jiraService.createTicket(request);

        assertNotNull(result);
        assertEquals("PROJ-123", result.getKey());
        verify(jiraApiClient).createIssue(request);
        verify(jiraIssueRepository).save(sampleIssue);
    }

    @Test
    void testCreateTicket_Exception() {
        CreateIssueRequest request = new CreateIssueRequest("PROJ", "Test Issue", "Test Description");
        when(jiraApiClient.createIssue(request)).thenThrow(new RuntimeException("API Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jiraService.createTicket(request);
        });

        assertTrue(exception.getMessage().contains("Failed to create Jira ticket"));
        assertTrue(exception.getMessage().contains("API Error"));
    }

    @Test
    void testGetTicket_LocalCacheHit() {
        when(jiraIssueRepository.findByKey("PROJ-123")).thenReturn(Optional.of(sampleIssue));
        when(jiraApiClient.getIssue("PROJ-123")).thenReturn(sampleIssue);
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        JiraIssue result = jiraService.getTicket("PROJ-123");

        assertNotNull(result);
        assertEquals("PROJ-123", result.getKey());
        verify(jiraIssueRepository).findByKey("PROJ-123");
        verify(jiraApiClient).getIssue("PROJ-123");
    }

    @Test
    void testGetTicket_LocalCacheMiss() {
        when(jiraIssueRepository.findByKey("PROJ-123")).thenReturn(Optional.empty());
        when(jiraApiClient.getIssue("PROJ-123")).thenReturn(sampleIssue);
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        JiraIssue result = jiraService.getTicket("PROJ-123");

        assertNotNull(result);
        assertEquals("PROJ-123", result.getKey());
        verify(jiraApiClient).getIssue("PROJ-123");
        verify(jiraIssueRepository).save(sampleIssue);
    }

    @Test
    void testGetTicket_ApiFailureWithLocalCache() {
        when(jiraIssueRepository.findByKey("PROJ-123")).thenReturn(Optional.of(sampleIssue));
        when(jiraApiClient.getIssue("PROJ-123")).thenThrow(new RuntimeException("API Error"));

        JiraIssue result = jiraService.getTicket("PROJ-123");

        assertNotNull(result);
        assertEquals("PROJ-123", result.getKey());
        verify(jiraIssueRepository).findByKey("PROJ-123");
    }

    @Test
    void testSearchTickets_Success() {
        JiraSearchRequest request = new JiraSearchRequest("test");
        List<JiraIssue> issues = Arrays.asList(sampleIssue);
        when(jiraApiClient.searchIssues(request)).thenReturn(issues);
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        List<JiraIssue> result = jiraService.searchTickets(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PROJ-123", result.get(0).getKey());
        verify(jiraApiClient).searchIssues(request);
    }

    @Test
    void testSearchTickets_ApiFailureWithFallback() {
        JiraSearchRequest request = new JiraSearchRequest("test");
        request.setProjectKey("PROJ");
        when(jiraApiClient.searchIssues(request)).thenThrow(new RuntimeException("API Error"));
        when(jiraIssueRepository.findByProjectKey("PROJ")).thenReturn(Arrays.asList(sampleIssue));

        List<JiraIssue> result = jiraService.searchTickets(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jiraIssueRepository).findByProjectKey("PROJ");
    }

    @Test
    void testUpdateTicket_Success() {
        UpdateIssueRequest request = new UpdateIssueRequest();
        request.setStatus("In Progress");
        when(jiraApiClient.updateIssue("PROJ-123", request)).thenReturn(sampleIssue);
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        JiraIssue result = jiraService.updateTicket("PROJ-123", request);

        assertNotNull(result);
        verify(jiraApiClient).updateIssue("PROJ-123", request);
        verify(jiraIssueRepository).save(sampleIssue);
    }

    @Test
    void testAddComment_Success() {
        doNothing().when(jiraApiClient).addComment("PROJ-123", "Test comment");

        assertDoesNotThrow(() -> {
            jiraService.addComment("PROJ-123", "Test comment");
        });

        verify(jiraApiClient).addComment("PROJ-123", "Test comment");
    }

    @Test
    void testCreateTicketFromRequirement_Success() {
        when(jiraApiClient.createIssue(any(CreateIssueRequest.class))).thenReturn(sampleIssue);
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        JiraIssue result = jiraService.createTicketFromRequirement(sampleProjectEntity, sampleRequirement);

        assertNotNull(result);
        assertEquals("PROJ-123", result.getKey());
        verify(jiraApiClient).createIssue(any(CreateIssueRequest.class));
    }

    @Test
    void testGetProjectTickets_Success() {
        JiraSearchRequest expectedRequest = new JiraSearchRequest();
        expectedRequest.setProjectKey("PROJ");
        when(jiraApiClient.searchIssues(any(JiraSearchRequest.class))).thenReturn(Arrays.asList(sampleIssue));
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        List<JiraIssue> result = jiraService.getProjectTickets("PROJ");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jiraApiClient).searchIssues(any(JiraSearchRequest.class));
    }

    @Test
    void testSyncTicketStatus_Success() {
        when(jiraApiClient.getIssue("PROJ-123")).thenReturn(sampleIssue);
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        assertDoesNotThrow(() -> {
            jiraService.syncTicketStatus("PROJ-123");
        });

        verify(jiraApiClient).getIssue("PROJ-123");
        verify(jiraIssueRepository).save(sampleIssue);
    }

    @Test
    void testGetProjects_Success() {
        List<JiraProject> projects = Arrays.asList(sampleProject);
        when(jiraApiClient.getProjects()).thenReturn(projects);
        when(jiraProjectRepository.save(sampleProject)).thenReturn(sampleProject);

        List<JiraProject> result = jiraService.getProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jiraApiClient).getProjects();
    }

    @Test
    void testGetProjects_ApiFailureWithFallback() {
        when(jiraApiClient.getProjects()).thenThrow(new RuntimeException("API Error"));
        when(jiraProjectRepository.findAll()).thenReturn(Arrays.asList(sampleProject));

        List<JiraProject> result = jiraService.getProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jiraProjectRepository).findAll();
    }

    @Test
    void testCreateTicketsFromProject_Success() {
        sampleProjectEntity.getRequirements().add(sampleRequirement);
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(sampleProjectEntity));
        when(jiraApiClient.createIssue(any(CreateIssueRequest.class))).thenReturn(sampleIssue);
        when(jiraIssueRepository.save(sampleIssue)).thenReturn(sampleIssue);

        List<JiraIssue> result = jiraService.createTicketsFromProject("Test Project");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectService).findProjectByName("Test Project");
    }

    @Test
    void testCreateTicketsFromProject_ProjectNotFound() {
        when(projectService.findProjectByName("Nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jiraService.createTicketsFromProject("Nonexistent");
        });

        assertTrue(exception.getMessage().contains("Project not found"));
    }

    @Test
    void testCreateTicketsFromProject_NoRequirements() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(sampleProjectEntity));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jiraService.createTicketsFromProject("Test Project");
        });

        assertTrue(exception.getMessage().contains("no requirements"));
    }
}
