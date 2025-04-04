package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.model.CommandExtraction;
import com.example.springai.model.Parameters;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.springai.mcp.ChatResponse;
import com.example.springai.mcp.Generation;
import com.example.springai.mcp.Prompt;
import com.example.springai.mcp.ChatClient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AiControllerExtendedTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ProjectService projectService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AiController aiController;

    @Test
    public void testExtractExtractionField() {
        // Test the private method using reflection
        String jsonString = "{\"command\":\"create_project\",\"parameters\":{\"project_name\":\"Test Project\"}}";
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "extractExtractionField", 
                                                                 jsonString, 
                                                                 "command");
        assertEquals("create_project", result);
    }

    @Test
    public void testExtractExtractionFieldWithEmptyJson() {
        String jsonString = "{}";
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "extractExtractionField", 
                                                                 jsonString, 
                                                                 "command");
        assertEquals("", result);
    }

    @Test
    public void testExtractExtractionFieldWithInvalidJson() {
        String jsonString = "This is not valid JSON";
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "extractExtractionField", 
                                                                 jsonString, 
                                                                 "command");
        assertEquals("", result);
    }

    @Test
    public void testIsCreateProjectCommand() {
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isCreateProjectCommand", 
                                                            "create project test"));
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isCreateProjectCommand", 
                                                            "I want to create a new project"));
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                             "isCreateProjectCommand", 
                                                             "show me all projects"));
    }

    @Test
    public void testIsListProjectsCommand() {
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isListProjectsCommand", 
                                                            "list projects"));
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isListProjectsCommand", 
                                                            "show all projects"));
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                             "isListProjectsCommand", 
                                                             "create project test"));
    }

    @Test
    public void testIsShowProjectCommand() {
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isShowProjectCommand", 
                                                            "show project test"));
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isShowProjectCommand", 
                                                            "tell me about project xyz"));
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                             "isShowProjectCommand", 
                                                             "list all projects"));
    }

    @Test
    public void testIsAddRequirementCommand() {
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isAddRequirementCommand", 
                                                            "add requirement xyz to project test"));
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                             "isAddRequirementCommand", 
                                                             "add requirement xyz"));
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                             "isAddRequirementCommand", 
                                                             "show project test"));
    }

    @Test
    public void testIsPrepareStoriesCommand() {
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isPrepareStoriesCommand", 
                                                            "prepare stories for project test"));
        assertTrue((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                            "isPrepareStoriesCommand", 
                                                            "analyze requirements for project test"));
        assertFalse((boolean) ReflectionTestUtils.invokeMethod(aiController, 
                                                             "isPrepareStoriesCommand", 
                                                             "show project test"));
    }

    @Test
    public void testHandleAddRequirementWithMissingParameters() {
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleAddRequirement", 
                                                                 "", 
                                                                 "requirement text");
        assertTrue(result.contains("couldn't understand"));
        
        result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                          "handleAddRequirement", 
                                                          "project name", 
                                                          "");
        assertTrue(result.contains("couldn't understand"));
    }

    @Test
    public void testHandleAddRequirementWithNonExistentProject() {
        when(projectService.findProjectByName(anyString())).thenReturn(Optional.empty());
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleAddRequirement", 
                                                                 "Non-existent Project", 
                                                                 "requirement text");
        assertTrue(result.contains("not found"));
    }

    @Test
    public void testHandleAddRequirementSuccess() {
        Project project = new Project();
        project.setName("Test Project");
        
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(project));
        when(projectService.addRequirement(any(Project.class), anyString())).thenReturn(new Requirement());
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleAddRequirement", 
                                                                 "Test Project", 
                                                                 "requirement text");
        assertTrue(result.contains("has been added"));
    }

    @Test
    public void testHandleCreateProjectWithMissingName() {
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleCreateProject", 
                                                                 "");
        assertTrue(result.contains("couldn't understand"));
    }

    @Test
    public void testHandleCreateProjectSuccess() {
        Project project = new Project();
        project.setName("New Project");
        
        when(projectService.createProject(eq("New Project"), anyString())).thenReturn(project);
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleCreateProject", 
                                                                 "New Project");
        assertTrue(result.contains("has been created"));
    }

    @Test
    public void testHandleListProjectsWhenEmpty() {
        when(projectService.hasProjects()).thenReturn(false);
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleListProjects");
        assertTrue(result.contains("don't have any projects"));
    }

    @Test
    public void testHandleListProjectsWithProjects() {
        List<Project> projects = new ArrayList<>();
        Project p1 = new Project();
        p1.setName("Project 1");
        Project p2 = new Project();
        p2.setName("Project 2");
        projects.add(p1);
        projects.add(p2);
        
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getAllProjects()).thenReturn(projects);
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleListProjects");
        assertTrue(result.contains("Project 1"));
        assertTrue(result.contains("Project 2"));
    }

    @Test
    public void testHandleShowProjectWithMissingName() {
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleShowProject", 
                                                                 "");
        assertTrue(result.contains("couldn't understand"));
    }

    @Test
    public void testHandleShowProjectWithNonExistentProject() {
        when(projectService.findProjectByName(anyString())).thenReturn(Optional.empty());
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleShowProject", 
                                                                 "Non-existent Project");
        assertTrue(result.contains("not found"));
    }

    @Test
    public void testHandleShowProjectSuccess() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(project));
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handleShowProject", 
                                                                 "Test Project");
        assertTrue(result.contains("Test Project"));
        assertTrue(result.contains("Test Description"));
    }

    @Test
    public void testHandlePrepareStoriesWithMissingName() {
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handlePrepareStories", 
                                                                 "");
        assertTrue(result.contains("couldn't understand"));
    }

    @Test
    public void testHandlePrepareStoriesWithNonExistentProject() {
        when(projectService.findProjectByName(anyString())).thenReturn(Optional.empty());
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                                 "handlePrepareStories", 
                                                                 "Non-existent Project");
        assertTrue(result.contains("not found"));
    }

    @Test
    public void testHandlePrepareStoriesWithRequirements() {
        Project project = new Project();
        project.setName("Test Project");
        
        Requirement req = new Requirement();
        req.setText("Test Requirement");
        req.setProject(project);
        List<Requirement> requirements = new ArrayList<>();
        requirements.add(req);
        project.setRequirements(requirements);
        
        when(projectService.findProjectByName("Test Project")).thenReturn(Optional.of(project));
        
        // Create a mock ChatResponse
        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        com.example.springai.mcp.AssistantMessage mockAssistantMessage = mock(com.example.springai.mcp.AssistantMessage.class);
        when(mockAssistantMessage.getContent()).thenReturn("{\"stories\":[\"Story 1\"],\"risks\":[\"Risk 1\"],\"nfrs\":[\"NFR 1\"],\"queries\":[\"Query 1\"],\"summary\":\"Test summary\"}");
        when(mockGeneration.getOutput()).thenReturn(mockAssistantMessage);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        
        // Use any() matcher for Prompt
        when(chatClient.call(any(Prompt.class))).thenReturn(mockResponse);
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                               "handlePrepareStories", 
                                                               "Test Project");
        // Just verify the method was called and doesn't throw an exception
        verify(projectService).saveStoryAnalysisResult(any(Project.class), any());
    }
    
    @Test
    public void testHandleHelpCommand() {
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getFirstProject()).thenReturn(new Project());
        
        String result = (String) ReflectionTestUtils.invokeMethod(aiController, 
                                                               "handleHelpCommand");
        assertTrue(result.contains("Available commands"));
        assertTrue(result.contains("create project"));
        assertTrue(result.contains("list projects"));
    }
    
    @Test
    public void testSaveAndRetrieveChatMessages() {
        ChatMessage message = new ChatMessage("test prompt", "test response");
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(message);
        
        // Test saving message
        ReflectionTestUtils.invokeMethod(aiController, "saveChatMessage", "test prompt", "test response");
        verify(chatMessageRepository).save(any(ChatMessage.class));
        
        // Test retrieving messages
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(message);
        when(chatMessageRepository.findRecentMessages(anyInt())).thenReturn(messages);
        
        List<ChatMessage> result = aiController.getChatHistory(10);
        assertEquals(1, result.size());
        assertEquals("test prompt", result.get(0).getPrompt());
    }
}
