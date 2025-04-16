package com.example.springai.tools;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectManagementToolsTest {

    @Mock
    private ProjectService projectService;
    
    @Mock
    private OllamaChatModel chatModel;

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
        assertTrue(result.contains("Refine requirements for a project"));
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
    
    @Test
    void testRefineRequirements_Success() {
        Requirement req = new Requirement();
        req.setText("Test Requirement");
        testProject.getRequirements().add(req);
        
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        ChatClient mockChatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        CallResponseSpec mockResponseSpec = mock(CallResponseSpec.class);
        
        try (MockedStatic<ChatClient> mockedChatClient = mockStatic(ChatClient.class)) {
            mockedChatClient.when(() -> ChatClient.create(any(OllamaChatModel.class))).thenReturn(mockChatClient);
            when(mockChatClient.prompt(anyString())).thenReturn(mockRequestSpec);
            when(mockRequestSpec.call()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.content()).thenReturn(
                "{\"stories\":[\"As a user, I want to login\"],\"nfrs\":[\"Security\"],\"risks\":[\"Data breach\"],\"queries\":[\"What authentication method?\"],\"summary\":\"Test\"}");
            
            String result = projectManagementTools.refineRequirements("Test Project");
            
            assertTrue(result.contains("Requirements for project 'Test Project' have been refined successfully"));
            assertTrue(result.contains("Generated User Stories:"));
            assertTrue(result.contains("- As a user, I want to login"));
            assertTrue(result.contains("Non-Functional Requirements:"));
            assertTrue(result.contains("- Security"));
            assertTrue(result.contains("Potential Risks:"));
            assertTrue(result.contains("- Data breach"));
            assertTrue(result.contains("Queries for Clarification:"));
            assertTrue(result.contains("- What authentication method?"));
            
            verify(projectService).findProjectByName("Test Project");
            verify(projectService).saveStoryAnalysisResult(eq(testProject), any(StoryAnalysisResponse.class));
        }
    }
    
    @Test
    void testRefineRequirements_Exception() {
        Requirement req = new Requirement();
        req.setText("Test Requirement");
        testProject.getRequirements().add(req);
        
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        try (MockedStatic<ChatClient> mockedChatClient = mockStatic(ChatClient.class)) {
            ChatClient mockChatClient = mock(ChatClient.class);
            ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
            
            mockedChatClient.when(() -> ChatClient.create(any(OllamaChatModel.class))).thenReturn(mockChatClient);
            when(mockChatClient.prompt(anyString())).thenReturn(mockRequestSpec);
            when(mockRequestSpec.call()).thenThrow(new RuntimeException("Test exception"));
            
            String result = projectManagementTools.refineRequirements("Test Project");
            
            assertTrue(result.contains("Failed to refine requirements"));
            verify(projectService).findProjectByName("Test Project");
        }
    }
    
    @Test
    void testRefineRequirements_ProjectNotFound() {
        when(projectService.findProjectByName("Non-existent")).thenReturn(Optional.empty());
        
        String result = projectManagementTools.refineRequirements("Non-existent");
        
        assertTrue(result.contains("not found"));
        verify(projectService).findProjectByName("Non-existent");
        verify(projectService, never()).saveStoryAnalysisResult(any(), any());
    }
    
    @Test
    void testRefineRequirements_NoRequirements() {
        testProject.setRequirements(new ArrayList<>());
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        String result = projectManagementTools.refineRequirements("Test Project");
        
        assertTrue(result.contains("doesn't have any requirements yet"));
        verify(projectService).findProjectByName("Test Project");
        verify(projectService, never()).saveStoryAnalysisResult(any(), any());
    }
    
    @Test
    void testRefineRequirements_NullName() {
        String result = projectManagementTools.refineRequirements(null);
        assertEquals("Project name is required", result);
    }
    
    @Test
    void testAnalyzeProjectRequirements_Success() throws Exception {
        Requirement req = new Requirement();
        req.setText("Test Requirement");
        testProject.getRequirements().add(req);
        
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        ChatClient mockChatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        CallResponseSpec mockResponseSpec = mock(CallResponseSpec.class);
        
        try (MockedStatic<ChatClient> mockedChatClient = mockStatic(ChatClient.class)) {
            mockedChatClient.when(() -> ChatClient.create(any(OllamaChatModel.class))).thenReturn(mockChatClient);
            when(mockChatClient.prompt(anyString())).thenReturn(mockRequestSpec);
            when(mockRequestSpec.call()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.content()).thenReturn(
                "{\"stories\":[\"As a user, I want to login\"],\"nfrs\":[\"Security\"],\"risks\":[\"Data breach\"],\"queries\":[\"What authentication method?\"],\"summary\":\"Test\"}");
            
            String result = projectManagementTools.analyzeProjectRequirements("Test Project");
            
            assertTrue(result.contains("Requirements for project 'Test Project' have been analyzed successfully"));
            assertTrue(result.contains("Generated User Stories:"));
            assertTrue(result.contains("- As a user, I want to login"));
            assertTrue(result.contains("Non-Functional Requirements:"));
            assertTrue(result.contains("- Security"));
            assertTrue(result.contains("Potential Risks:"));
            assertTrue(result.contains("- Data breach"));
            assertTrue(result.contains("Queries for Clarification:"));
            assertTrue(result.contains("- What authentication method?"));
            
            verify(projectService).findProjectByName("Test Project");
        }
    }
    
    @Test
    void testAnalyzeProjectRequirements_ProjectNotFound() {
        when(projectService.findProjectByName("Non-existent")).thenReturn(Optional.empty());
        
        String result = projectManagementTools.analyzeProjectRequirements("Non-existent");
        
        assertTrue(result.contains("not found"));
        verify(projectService).findProjectByName("Non-existent");
    }
    
    @Test
    void testAnalyzeProjectRequirements_NoRequirements() {
        testProject.setRequirements(new ArrayList<>());
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        String result = projectManagementTools.analyzeProjectRequirements("Test Project");
        
        assertTrue(result.contains("doesn't have any requirements yet"));
        verify(projectService).findProjectByName("Test Project");
    }
    
    @Test
    void testAnalyzeProjectRequirements_NullName() {
        String result = projectManagementTools.analyzeProjectRequirements(null);
        assertEquals("Project name is required", result);
    }
    
    @Test
    void testAnalyzeProjectRequirements_Exception() {
        Requirement req = new Requirement();
        req.setText("Test Requirement");
        testProject.getRequirements().add(req);
        
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        try (MockedStatic<ChatClient> mockedChatClient = mockStatic(ChatClient.class)) {
            ChatClient mockChatClient = mock(ChatClient.class);
            ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
            
            mockedChatClient.when(() -> ChatClient.create(any(OllamaChatModel.class))).thenReturn(mockChatClient);
            when(mockChatClient.prompt(anyString())).thenReturn(mockRequestSpec);
            when(mockRequestSpec.call()).thenThrow(new RuntimeException("Test exception"));
            
            String result = projectManagementTools.analyzeProjectRequirements("Test Project");
            
            assertTrue(result.contains("Failed to analyze project requirements"));
            verify(projectService).findProjectByName("Test Project");
        }
    }
    
    @Test
    void testSaveRefinedRequirements_Success() throws Exception {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        ChatClient mockChatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        CallResponseSpec mockResponseSpec = mock(CallResponseSpec.class);
        
        try (MockedStatic<ChatClient> mockedChatClient = mockStatic(ChatClient.class)) {
            mockedChatClient.when(() -> ChatClient.create(any(OllamaChatModel.class))).thenReturn(mockChatClient);
            when(mockChatClient.prompt(anyString())).thenReturn(mockRequestSpec);
            when(mockRequestSpec.call()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.content()).thenReturn(
                "{\"stories\":[\"As a user, I want to login\"],\"nfrs\":[\"Security\"],\"risks\":[\"Data breach\"],\"queries\":[\"What authentication method?\"],\"summary\":\"Test\"}");
            
            String result = projectManagementTools.saveRefinedRequirements("Test Project");
            
            assertTrue(result.contains("Refined requirements for project 'Test Project' have been saved successfully"));
            verify(projectService).findProjectByName("Test Project");
            verify(projectService).saveStoryAnalysisResult(eq(testProject), any(StoryAnalysisResponse.class));
        }
    }
    
    @Test
    void testSaveRefinedRequirements_ProjectNotFound() {
        when(projectService.findProjectByName("Non-existent")).thenReturn(Optional.empty());
        
        String result = projectManagementTools.saveRefinedRequirements("Non-existent");
        
        assertTrue(result.contains("not found"));
        verify(projectService).findProjectByName("Non-existent");
        verify(projectService, never()).saveStoryAnalysisResult(any(), any());
    }
    
    @Test
    void testSaveRefinedRequirements_NullName() {
        String result = projectManagementTools.saveRefinedRequirements(null);
        assertEquals("Project name is required", result);
    }
    
    @Test
    void testSaveRefinedRequirements_Exception() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        try (MockedStatic<ChatClient> mockedChatClient = mockStatic(ChatClient.class)) {
            ChatClient mockChatClient = mock(ChatClient.class);
            ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
            
            mockedChatClient.when(() -> ChatClient.create(any(OllamaChatModel.class))).thenReturn(mockChatClient);
            when(mockChatClient.prompt(anyString())).thenReturn(mockRequestSpec);
            when(mockRequestSpec.call()).thenThrow(new RuntimeException("Test exception"));
            
            String result = projectManagementTools.saveRefinedRequirements("Test Project");
            
            assertTrue(result.contains("Failed to save refined requirements"));
            verify(projectService).findProjectByName("Test Project");
        }
    }
    
    @Test
    void testSaveRefinedRequirements_ParseError() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        ChatClient mockChatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec mockResponseSpec = mock(ChatClient.CallResponseSpec.class);
        
        try (MockedStatic<ChatClient> mockedChatClient = mockStatic(ChatClient.class)) {
            mockedChatClient.when(() -> ChatClient.create(any(OllamaChatModel.class))).thenReturn(mockChatClient);
            when(mockChatClient.prompt(anyString())).thenReturn(mockRequestSpec);
            when(mockRequestSpec.call()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.content()).thenReturn("Invalid JSON");
            
            String result = projectManagementTools.saveRefinedRequirements("Test Project");
            
            assertTrue(result.contains("Failed to parse the most recent analysis"));
            verify(projectService).findProjectByName("Test Project");
            verify(projectService, never()).saveStoryAnalysisResult(any(), any());
        }
    }
}
