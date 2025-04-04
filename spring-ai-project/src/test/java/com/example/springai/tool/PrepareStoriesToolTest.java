package com.example.springai.tool;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.example.springai.mcp.ChatResponse;
import com.example.springai.mcp.Generation;
import com.example.springai.mcp.AssistantMessage;
import com.example.springai.mcp.UserMessage;
import com.example.springai.mcp.Prompt;
import com.example.springai.mcp.ChatClient;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class PrepareStoriesToolTest {

    @Test
    public void testPrepareStoriesSuccess() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        ChatClient chatClient = Mockito.mock(ChatClient.class);
        
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        
        List<Requirement> requirements = new ArrayList<>();
        Requirement req1 = new Requirement();
        req1.setText("The system should allow user login");
        
        Requirement req2 = new Requirement();
        req2.setText("The system should allow user registration");
        
        requirements.add(req1);
        requirements.add(req2);
        project.setRequirements(requirements);
        
        StoryAnalysisResponse analysisResponse = new StoryAnalysisResponse();
        analysisResponse.setStories(Arrays.asList("As a user, I want to login", "As a user, I want to register"));
        analysisResponse.setRisks(Arrays.asList("Security risk"));
        analysisResponse.setNfrs(Arrays.asList("Performance requirement"));
        analysisResponse.setQueries(Arrays.asList("What authentication method?"));
        analysisResponse.setSummary("Project summary");
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.of(project));
        
        ChatResponse mockResponse = Mockito.mock(ChatResponse.class);
        Generation mockGeneration = Mockito.mock(Generation.class);
        AssistantMessage mockMessage = Mockito.mock(AssistantMessage.class);
        
        Mockito.when(chatClient.call(any(Prompt.class))).thenReturn(mockResponse);
        Mockito.when(mockResponse.getResult()).thenReturn(mockGeneration);
        Mockito.when(mockGeneration.getOutput()).thenReturn(mockMessage);
        Mockito.when(mockMessage.getContent()).thenReturn(
                "{\"stories\":[\"As a user, I want to login\",\"As a user, I want to register\"]," +
                "\"risks\":[\"Security risk\"]," +
                "\"nfrs\":[\"Performance requirement\"]," +
                "\"queries\":[\"What authentication method?\"]," +
                "\"summary\":\"Project summary\"}"
        );
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("project", "TestProject");
        
        PrepareStoriesTool tool = new PrepareStoriesTool(projectService, chatClient) {
            @Override
            protected StoryAnalysisResponse parseStoryAnalysisResponse(String jsonString) {
                return analysisResponse;
            }
        };
        
        ToolResult result = tool.execute(parameters);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("As a user, I want to login"));
        assertTrue(result.getMessage().contains("Security risk"));
        
        Mockito.verify(projectService).findProjectByName("TestProject");
        Mockito.verify(chatClient).call(any(Prompt.class));
        Mockito.verify(projectService).saveStoryAnalysisResult(Mockito.eq(project), Mockito.any(StoryAnalysisResponse.class));
    }
    
    @Test
    public void testPrepareStoriesProjectNotFound() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        ChatClient chatClient = Mockito.mock(ChatClient.class);
        
        Mockito.when(projectService.findProjectByName("NonExistentProject"))
                .thenReturn(Optional.empty());
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("project", "NonExistentProject");
        
        PrepareStoriesTool tool = new PrepareStoriesTool(projectService, chatClient);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        
        Mockito.verify(chatClient, Mockito.never()).call(any(Prompt.class));
    }
    
    @Test
    public void testPrepareStoriesNoRequirements() {
        ProjectService projectService = Mockito.mock(ProjectService.class);
        ChatClient chatClient = Mockito.mock(ChatClient.class);
        
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        project.setRequirements(Collections.emptyList());
        
        Mockito.when(projectService.findProjectByName("TestProject"))
                .thenReturn(Optional.of(project));
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("project", "TestProject");
        
        PrepareStoriesTool tool = new PrepareStoriesTool(projectService, chatClient);
        ToolResult result = tool.execute(parameters);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("doesn't have any requirements"));
        
        Mockito.verify(chatClient, Mockito.never()).call(any(Prompt.class));
    }
}
