package com.example.springai.controller;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.entity.Story;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.springai.mcp.ChatResponse;
import com.example.springai.mcp.Generation;
import com.example.springai.mcp.AssistantMessage;
import com.example.springai.mcp.Prompt;
import com.example.springai.mcp.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AiController.class)
public class AiControllerBranchCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageRepository chatMessageRepository;
    
    @MockBean
    private ChatClient chatClient;
    
    @MockBean
    private ProjectService projectService;
    
    @BeforeEach
    public void setup() {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("Default AI response");
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        
        doReturn(chatResponse).when(chatClient).call(any(String.class));
        doReturn(chatResponse).when(chatClient).call(any(Prompt.class));
    }
    
    @Test
    public void testAddRequirementWithEmptyText() throws Exception {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("AI response");
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(chatResponse).when(chatClient).call(any(String.class));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add requirement  to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddRequirementWithMissingProjectName() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add requirement User login to project"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddStoryWithEmptyTitle() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add story  with description Test description to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddStoryWithEmptyDescription() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add story Test title with description  to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddStoryWithMissingProjectName() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add story Test title with description Test description to project"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddRiskWithEmptyDescription() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add risk  with mitigation Test mitigation to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddRiskWithEmptyMitigation() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add risk Test risk with mitigation  to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddRiskWithMissingProjectName() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add risk Test risk with mitigation Test mitigation to project"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddNFRWithEmptyCategory() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add nfr  with description Test description to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddNFRWithEmptyDescription() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add nfr Performance with description  to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddNFRWithMissingProjectName() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add nfr Performance with description Test description to project"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddQueryWithEmptyQuestion() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add query  with context Test context to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddQueryWithEmptyContext() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add query Test question with context  to project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAddQueryWithMissingProjectName() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add query Test question with context Test context to project"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testPrepareStoriesWithEmptyAIResponse() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        List<Requirement> requirements = new ArrayList<>();
        Requirement req = new Requirement();
        req.setText("Test requirement");
        requirements.add(req);
        project.setRequirements(requirements);
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("");
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(chatResponse).when(chatClient).call(any(Prompt.class));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testPrepareStoriesWithInvalidJsonResponse() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        List<Requirement> requirements = new ArrayList<>();
        Requirement req = new Requirement();
        req.setText("Test requirement");
        requirements.add(req);
        project.setRequirements(requirements);
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("This is not valid JSON");
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(chatResponse).when(chatClient).call(any(Prompt.class));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testPrepareStoriesWithMissingStoriesInJson() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        List<Requirement> requirements = new ArrayList<>();
        Requirement req = new Requirement();
        req.setText("Test requirement");
        requirements.add(req);
        project.setRequirements(requirements);
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("{\"notStories\": []}");
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(chatResponse).when(chatClient).call(any(Prompt.class));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testPrepareStoriesWithEmptyStoriesArray() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        List<Requirement> requirements = new ArrayList<>();
        Requirement req = new Requirement();
        req.setText("Test requirement");
        requirements.add(req);
        project.setRequirements(requirements);
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("{\"stories\": []}");
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(chatResponse).when(chatClient).call(any(Prompt.class));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testAnalyzeRequirementsWithEmptyAIResponse() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        List<Requirement> requirements = new ArrayList<>();
        Requirement req = new Requirement();
        req.setText("Test requirement");
        requirements.add(req);
        project.setRequirements(requirements);
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("");
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        doReturn(chatResponse).when(chatClient).call(any(Prompt.class));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "analyze requirements for project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testShowProjectWithNoRequirements() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test description");
        project.setRequirements(new ArrayList<>());
        project.setStories(new ArrayList<>());
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testShowProjectWithRequirementsButNoStories() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test description");
        
        List<Requirement> requirements = new ArrayList<>();
        Requirement req = new Requirement();
        req.setText("Test requirement");
        requirements.add(req);
        project.setRequirements(requirements);
        
        project.setStories(new ArrayList<>());
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testShowProjectWithStoriesButNoRequirements() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test description");
        project.setRequirements(new ArrayList<>());
        
        List<Story> stories = new ArrayList<>();
        Story story = new Story();
        story.setTitle("Test story");
        story.setDescription("Test story description");
        stories.add(story);
        project.setStories(stories);
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project TestProject"))
                .andExpect(status().isOk());
    }
}
