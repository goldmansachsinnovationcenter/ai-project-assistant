package com.example.springai.tools;

import com.example.springai.dto.CreateIssueRequest;
import com.example.springai.dto.JiraSearchRequest;
import com.example.springai.dto.UpdateIssueRequest;
import com.example.springai.entity.JiraIssue;
import com.example.springai.entity.JiraProject;
import com.example.springai.service.JiraService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JiraManagementToolsTest {

    @Mock
    private JiraService jiraService;

    @InjectMocks
    private JiraManagementTools jiraManagementTools;

    private JiraIssue sampleIssue;
    private JiraProject sampleProject;

    @BeforeEach
    void setUp() {
        sampleIssue = new JiraIssue();
        sampleIssue.setKey("PROJ-123");
        sampleIssue.setSummary("Test Issue");
        sampleIssue.setDescription("Test Description");
        sampleIssue.setStatus("To Do");
        sampleIssue.setPriority("Medium");
        sampleIssue.setAssignee("john.doe");
        sampleIssue.setReporter("jane.smith");
        sampleIssue.setProjectKey("PROJ");
        sampleIssue.setCreated(LocalDateTime.now());
        sampleIssue.setUpdated(LocalDateTime.now());

        sampleProject = new JiraProject();
        sampleProject.setKey("PROJ");
        sampleProject.setName("Test Project");
        sampleProject.setDescription("Test Project Description");
        sampleProject.setLead("project.lead");
    }

    @Test
    void testCreateJiraTicket_Success() {
        when(jiraService.createTicket(any(CreateIssueRequest.class))).thenReturn(sampleIssue);

        String result = jiraManagementTools.createJiraTicket("Test Issue", "Test Description", "PROJ");

        assertNotNull(result);
        assertTrue(result.contains("PROJ-123"));
        assertTrue(result.contains("created successfully"));
        verify(jiraService).createTicket(any(CreateIssueRequest.class));
    }

    @Test
    void testCreateJiraTicket_EmptyTitle() {
        String result = jiraManagementTools.createJiraTicket("", "Test Description", "PROJ");

        assertEquals("Ticket title is required", result);
        verify(jiraService, never()).createTicket(any(CreateIssueRequest.class));
    }

    @Test
    void testCreateJiraTicket_NullTitle() {
        String result = jiraManagementTools.createJiraTicket(null, "Test Description", "PROJ");

        assertEquals("Ticket title is required", result);
        verify(jiraService, never()).createTicket(any(CreateIssueRequest.class));
    }

    @Test
    void testCreateJiraTicket_Exception() {
        when(jiraService.createTicket(any(CreateIssueRequest.class)))
            .thenThrow(new RuntimeException("API Error"));

        String result = jiraManagementTools.createJiraTicket("Test Issue", "Test Description", "PROJ");

        assertTrue(result.startsWith("Failed to create Jira ticket:"));
        assertTrue(result.contains("API Error"));
    }

    @Test
    void testSearchJiraTickets_Success() {
        List<JiraIssue> issues = Arrays.asList(sampleIssue);
        when(jiraService.searchTickets(any(JiraSearchRequest.class))).thenReturn(issues);

        String result = jiraManagementTools.searchJiraTickets("test", "PROJ", "To Do");

        assertNotNull(result);
        assertTrue(result.contains("Found 1 Jira ticket"));
        assertTrue(result.contains("PROJ-123"));
        assertTrue(result.contains("Test Issue"));
        verify(jiraService).searchTickets(any(JiraSearchRequest.class));
    }

    @Test
    void testSearchJiraTickets_NoResults() {
        when(jiraService.searchTickets(any(JiraSearchRequest.class))).thenReturn(Collections.emptyList());

        String result = jiraManagementTools.searchJiraTickets("nonexistent", null, null);

        assertEquals("No Jira tickets found matching the search criteria", result);
    }

    @Test
    void testSearchJiraTickets_Exception() {
        when(jiraService.searchTickets(any(JiraSearchRequest.class)))
            .thenThrow(new RuntimeException("Search Error"));

        String result = jiraManagementTools.searchJiraTickets("test", null, null);

        assertTrue(result.startsWith("Failed to search Jira tickets:"));
        assertTrue(result.contains("Search Error"));
    }

    @Test
    void testGetJiraTicketDetails_Success() {
        when(jiraService.getTicket("PROJ-123")).thenReturn(sampleIssue);

        String result = jiraManagementTools.getJiraTicketDetails("PROJ-123");

        assertNotNull(result);
        assertTrue(result.contains("PROJ-123"));
        assertTrue(result.contains("Test Issue"));
        assertTrue(result.contains("To Do"));
        assertTrue(result.contains("Medium"));
        assertTrue(result.contains("john.doe"));
        verify(jiraService).getTicket("PROJ-123");
    }

    @Test
    void testGetJiraTicketDetails_EmptyKey() {
        String result = jiraManagementTools.getJiraTicketDetails("");

        assertEquals("Ticket key is required", result);
        verify(jiraService, never()).getTicket(anyString());
    }

    @Test
    void testGetJiraTicketDetails_Exception() {
        when(jiraService.getTicket("PROJ-123")).thenThrow(new RuntimeException("Not Found"));

        String result = jiraManagementTools.getJiraTicketDetails("PROJ-123");

        assertTrue(result.startsWith("Failed to get Jira ticket details:"));
        assertTrue(result.contains("Not Found"));
    }

    @Test
    void testUpdateJiraTicket_Status() {
        when(jiraService.updateTicket(eq("PROJ-123"), any(UpdateIssueRequest.class))).thenReturn(sampleIssue);

        String result = jiraManagementTools.updateJiraTicket("PROJ-123", "status", "In Progress");

        assertTrue(result.contains("updated successfully"));
        assertTrue(result.contains("status set to: In Progress"));
        verify(jiraService).updateTicket(eq("PROJ-123"), any(UpdateIssueRequest.class));
    }

    @Test
    void testUpdateJiraTicket_Assignee() {
        when(jiraService.updateTicket(eq("PROJ-123"), any(UpdateIssueRequest.class))).thenReturn(sampleIssue);

        String result = jiraManagementTools.updateJiraTicket("PROJ-123", "assignee", "new.user");

        assertTrue(result.contains("updated successfully"));
        assertTrue(result.contains("assignee set to: new.user"));
    }

    @Test
    void testUpdateJiraTicket_InvalidField() {
        String result = jiraManagementTools.updateJiraTicket("PROJ-123", "invalid", "value");

        assertEquals("Invalid field. Supported fields: status, assignee, priority, summary, description", result);
        verify(jiraService, never()).updateTicket(anyString(), any(UpdateIssueRequest.class));
    }

    @Test
    void testUpdateJiraTicket_EmptyKey() {
        String result = jiraManagementTools.updateJiraTicket("", "status", "Done");

        assertEquals("Ticket key is required", result);
        verify(jiraService, never()).updateTicket(anyString(), any(UpdateIssueRequest.class));
    }

    @Test
    void testUpdateJiraTicket_Exception() {
        when(jiraService.updateTicket(eq("PROJ-123"), any(UpdateIssueRequest.class)))
            .thenThrow(new RuntimeException("Update Error"));

        String result = jiraManagementTools.updateJiraTicket("PROJ-123", "status", "Done");

        assertTrue(result.startsWith("Failed to update Jira ticket:"));
        assertTrue(result.contains("Update Error"));
    }

    @Test
    void testAddJiraComment_Success() {
        doNothing().when(jiraService).addComment("PROJ-123", "Test comment");

        String result = jiraManagementTools.addJiraComment("PROJ-123", "Test comment");

        assertTrue(result.contains("Comment has been added"));
        assertTrue(result.contains("PROJ-123"));
        verify(jiraService).addComment("PROJ-123", "Test comment");
    }

    @Test
    void testAddJiraComment_EmptyKey() {
        String result = jiraManagementTools.addJiraComment("", "Test comment");

        assertEquals("Ticket key is required", result);
        verify(jiraService, never()).addComment(anyString(), anyString());
    }

    @Test
    void testAddJiraComment_EmptyComment() {
        String result = jiraManagementTools.addJiraComment("PROJ-123", "");

        assertEquals("Comment text is required", result);
        verify(jiraService, never()).addComment(anyString(), anyString());
    }

    @Test
    void testAddJiraComment_Exception() {
        doThrow(new RuntimeException("Comment Error")).when(jiraService).addComment("PROJ-123", "Test comment");

        String result = jiraManagementTools.addJiraComment("PROJ-123", "Test comment");

        assertTrue(result.startsWith("Failed to add comment to Jira ticket:"));
        assertTrue(result.contains("Comment Error"));
    }

    @Test
    void testCreateJiraTicketsFromProject_Success() {
        List<JiraIssue> createdTickets = Arrays.asList(sampleIssue);
        when(jiraService.createTicketsFromProject("TestProject")).thenReturn(createdTickets);

        String result = jiraManagementTools.createJiraTicketsFromProject("TestProject");

        assertTrue(result.contains("Successfully created 1 Jira ticket"));
        assertTrue(result.contains("PROJ-123"));
        verify(jiraService).createTicketsFromProject("TestProject");
    }

    @Test
    void testCreateJiraTicketsFromProject_NoTickets() {
        when(jiraService.createTicketsFromProject("TestProject")).thenReturn(Collections.emptyList());

        String result = jiraManagementTools.createJiraTicketsFromProject("TestProject");

        assertTrue(result.contains("No tickets were created"));
    }

    @Test
    void testCreateJiraTicketsFromProject_EmptyName() {
        String result = jiraManagementTools.createJiraTicketsFromProject("");

        assertEquals("Project name is required", result);
        verify(jiraService, never()).createTicketsFromProject(anyString());
    }

    @Test
    void testCreateJiraTicketsFromProject_Exception() {
        when(jiraService.createTicketsFromProject("TestProject"))
            .thenThrow(new RuntimeException("Project Error"));

        String result = jiraManagementTools.createJiraTicketsFromProject("TestProject");

        assertTrue(result.startsWith("Failed to create Jira tickets from project:"));
        assertTrue(result.contains("Project Error"));
    }

    @Test
    void testListJiraProjects_Success() {
        List<JiraProject> projects = Arrays.asList(sampleProject);
        when(jiraService.getProjects()).thenReturn(projects);

        String result = jiraManagementTools.listJiraProjects();

        assertTrue(result.contains("Available Jira projects (1)"));
        assertTrue(result.contains("PROJ - Test Project"));
        assertTrue(result.contains("project.lead"));
        verify(jiraService).getProjects();
    }

    @Test
    void testListJiraProjects_NoProjects() {
        when(jiraService.getProjects()).thenReturn(Collections.emptyList());

        String result = jiraManagementTools.listJiraProjects();

        assertEquals("No Jira projects found", result);
    }

    @Test
    void testListJiraProjects_Exception() {
        when(jiraService.getProjects()).thenThrow(new RuntimeException("Projects Error"));

        String result = jiraManagementTools.listJiraProjects();

        assertTrue(result.startsWith("Failed to list Jira projects:"));
        assertTrue(result.contains("Projects Error"));
    }

    @Test
    void testJiraHelp() {
        String result = jiraManagementTools.jiraHelp();

        assertNotNull(result);
        assertTrue(result.contains("Available Jira commands"));
        assertTrue(result.contains("Create a new Jira ticket"));
        assertTrue(result.contains("Search for Jira tickets"));
        assertTrue(result.contains("Update ticket"));
        assertTrue(result.contains("Add comment"));
        assertTrue(result.contains("Common statuses"));
        assertTrue(result.contains("Common priorities"));
    }
}
