package com.example.springai.mcp;

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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class PrepareStoriesToolTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private PrepareStoriesTool prepareStoriesTool;

    private Project testProject;
    private Map<String, String> parameters;
    private ChatResponse chatResponse;
    private Generation generation;

    @BeforeEach
    public void setup() {
        testProject = new Project();
        testProject.setId("test-id");
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        
        List<Requirement> requirements = new ArrayList<>();
        Requirement req1 = new Requirement();
        req1.setText("Requirement 1");
        req1.setProject(testProject);
        requirements.add(req1);
        
        Requirement req2 = new Requirement();
        req2.setText("Requirement 2");
        req2.setProject(testProject);
        requirements.add(req2);
        
        testProject.setRequirements(requirements);
        
        parameters = new HashMap<>();
        parameters.put("project", "Test Project");
        
        generation = mock(Generation.class);
        lenient().when(generation.getContent()).thenReturn(
            "{\n" +
            "  \"stories\": [\"User story 1\", \"User story 2\"],\n" +
            "  \"risks\": [\"Risk 1\", \"Risk 2\"],\n" +
            "  \"nfrs\": [\"NFR 1\", \"NFR 2\"],\n" +
            "  \"queries\": [\"Query 1\", \"Query 2\"],\n" +
            "  \"summary\": \"Project summary\"\n" +
            "}"
        );
        
        chatResponse = mock(ChatResponse.class);
        lenient().when(chatResponse.getResult()).thenReturn(generation);
    }

    @Test
    public void testExecute_Success() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        ToolResult result = prepareStoriesTool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("User story 1"));
        assertTrue(result.getMessage().contains("Risk 1"));
        assertTrue(result.getMessage().contains("NFR 1"));
        assertTrue(result.getMessage().contains("Query 1"));
        assertTrue(result.getMessage().contains("Project summary"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(chatClient, times(1)).call(any(Prompt.class));
        verify(projectService, times(1)).saveStoryAnalysisResult(eq(testProject), any(StoryAnalysisResponse.class));
    }
    
    @Test
    public void testExecute_ProjectNotFound() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.empty());
        
        ToolResult result = prepareStoriesTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(chatClient, never()).call(any(Prompt.class));
        verify(projectService, never()).saveStoryAnalysisResult(any(Project.class), any(StoryAnalysisResponse.class));
    }
    
    @Test
    public void testExecute_NoRequirements() {
        testProject.setRequirements(Collections.emptyList());
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        
        ToolResult result = prepareStoriesTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("doesn't have any requirements"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(chatClient, never()).call(any(Prompt.class));
        verify(projectService, never()).saveStoryAnalysisResult(any(Project.class), any(StoryAnalysisResponse.class));
    }
    
    @Test
    public void testExecute_MissingProjectName() {
        parameters.remove("project");
        
        ToolResult result = prepareStoriesTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
        verify(projectService, never()).findProjectByName(anyString());
        verify(chatClient, never()).call(any(Prompt.class));
        verify(projectService, never()).saveStoryAnalysisResult(any(Project.class), any(StoryAnalysisResponse.class));
    }
    
    @Test
    public void testExecute_EmptyProjectName() {
        parameters.put("project", "  ");
        
        ToolResult result = prepareStoriesTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("required"));
        verify(projectService, never()).findProjectByName(anyString());
        verify(chatClient, never()).call(any(Prompt.class));
        verify(projectService, never()).saveStoryAnalysisResult(any(Project.class), any(StoryAnalysisResponse.class));
    }
    
    @Test
    public void testExecute_AIException() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        when(chatClient.call(any(Prompt.class))).thenThrow(new RuntimeException("AI service error"));
        
        ToolResult result = prepareStoriesTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to prepare stories"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(chatClient, times(1)).call(any(Prompt.class));
        verify(projectService, never()).saveStoryAnalysisResult(any(Project.class), any(StoryAnalysisResponse.class));
    }
    
    @Test
    public void testExecute_InvalidJSONResponse() {
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(testProject));
        when(generation.getContent()).thenReturn("Invalid JSON");
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        ToolResult result = prepareStoriesTool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("error while analyzing"));
        verify(projectService, times(1)).findProjectByName("Test Project");
        verify(chatClient, times(1)).call(any(Prompt.class));
        verify(projectService, never()).saveStoryAnalysisResult(any(Project.class), any(StoryAnalysisResponse.class));
    }
    
    @Test
    public void testGetParameterNames() {
        String[] paramNames = prepareStoriesTool.getParameterNames();
        
        assertEquals(1, paramNames.length);
        assertEquals("project", paramNames[0]);
    }
    
    @Test
    public void testGetName() {
        assertEquals("prepare-stories", prepareStoriesTool.getName());
    }
    
    @Test
    public void testGetDescription() {
        assertTrue(prepareStoriesTool.getDescription().contains("Prepare user stories"));
    }
    
    @Test
    public void testGetUri() {
        assertTrue(prepareStoriesTool.getUri().contains("prepare-stories"));
    }
}
