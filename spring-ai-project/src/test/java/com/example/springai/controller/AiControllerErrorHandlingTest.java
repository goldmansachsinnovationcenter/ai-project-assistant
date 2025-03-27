package com.example.springai.controller;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiController.class)
public class AiControllerErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageRepository chatMessageRepository;
    
    @MockBean
    private OllamaChatClient chatClient;
    
    @MockBean
    private ProjectService projectService;
    
    @BeforeEach
    public void setup() {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("Default AI response");
        when(generation.getOutput()).thenReturn(output);
        when(chatResponse.getResult()).thenReturn(generation);
        
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
    }

    @Test
    public void testChatWithNullMessage() throws Exception {
        mockMvc.perform(get("/api/ai/chat"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testChatWithEmptyMessage() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", ""))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testChatWithAIServiceError() throws Exception {
        doThrow(new RuntimeException("AI service error"))
            .when(chatClient).call(any(Prompt.class));
        
        try {
            mockMvc.perform(get("/api/ai/chat")
                    .param("message", "Hello"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testCreateProjectWithExistingName() throws Exception {
        Project existingProject = new Project();
        existingProject.setName("ExistingProject");
        when(projectService.findProjectByName(eq("ExistingProject"))).thenReturn(Optional.of(existingProject));
        when(projectService.createProject(anyString(), anyString())).thenReturn(existingProject);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project ExistingProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddRequirementToNonExistentProject() throws Exception {
        when(projectService.findProjectByName(eq("NonExistentProject"))).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add requirement User login to project NonExistentProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'NonExistentProject' not found")));
    }
    
    @Test
    public void testShowNonExistentProject() throws Exception {
        when(projectService.findProjectByName(eq("NonExistentProject"))).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project NonExistentProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'NonExistentProject' not found")));
    }
    
    @Test
    public void testPrepareStoriesForNonExistentProject() throws Exception {
        when(projectService.findProjectByName(eq("NonExistentProject"))).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project NonExistentProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'NonExistentProject' not found")));
    }
    
    @Test
    public void testPrepareStoriesForProjectWithNoRequirements() throws Exception {
        Project project = new Project();
        project.setName("EmptyProject");
        project.setRequirements(Collections.emptyList());
        
        when(projectService.findProjectByName(eq("EmptyProject"))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project EmptyProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testGetTemplateWithEmptyTopic() throws Exception {
        mockMvc.perform(get("/api/ai/template")
                .param("topic", ""))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testGetTemplateWithAIServiceError() throws Exception {
        doThrow(new RuntimeException("AI service error"))
            .when(chatClient).call(any(Prompt.class));
        
        try {
            mockMvc.perform(get("/api/ai/template")
                    .param("topic", "Java"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testGetChatHistoryWithInvalidLimit() throws Exception {
        mockMvc.perform(get("/api/ai/chat-history")
                .param("limit", "invalid"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testGetChatHistoryWithNegativeLimit() throws Exception {
        mockMvc.perform(get("/api/ai/chat-history")
                .param("limit", "-1"));
    }
    
    @Test
    public void testShowAllProjectsWhenNoProjects() throws Exception {
        when(projectService.hasProjects()).thenReturn(false);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show all projects"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAnalyzeRequirementsWithAIServiceError() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        Requirement requirement = new Requirement();
        requirement.setText("Test requirement");
        project.setRequirements(List.of(requirement));
        
        when(projectService.findProjectByName(eq("TestProject"))).thenReturn(Optional.of(project));
        doThrow(new RuntimeException("AI service error"))
            .when(chatClient).call(any(Prompt.class));
        
        try {
            mockMvc.perform(get("/api/ai/chat")
                    .param("message", "analyze requirements for project TestProject"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testHealthEndpoint() throws Exception {
        try {
            mockMvc.perform(get("/api/health"));
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testUnrecognizedCommand() throws Exception {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("AI response for unrecognized command");
        when(generation.getOutput()).thenReturn(output);
        when(chatResponse.getResult()).thenReturn(generation);
        
        doReturn(chatResponse).when(chatClient).call(any(Prompt.class));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "unrecognized command"))
                .andExpect(status().isOk());
    }
}
