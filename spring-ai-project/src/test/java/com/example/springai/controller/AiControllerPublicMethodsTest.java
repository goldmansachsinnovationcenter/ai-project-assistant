package com.example.springai.controller;

import com.example.springai.entity.*;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.service.ProjectService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiController.class)
public class AiControllerPublicMethodsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageRepository chatMessageRepository;
    
    @MockBean
    private ChatClient chatClient;
    
    @MockBean
    private ProjectService projectService;
    
    @MockBean
    private ObjectMapper objectMapper;

    @Test
    public void testShowProjectWithEmptyDescription() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("");
        project.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project: TestProject")))
                .andExpect(content().string(containsString("No requirements yet")));
    }
    
    @Test
    public void testPrepareStoriesWithJsonParsingError() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        Requirement req1 = new Requirement();
        req1.setText("Requirement 1");
        project.setRequirements(Arrays.asList(req1));
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        String invalidJsonResponse = "This is not valid JSON";
        
        when(output.getContent()).thenReturn(invalidJsonResponse);
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        when(objectMapper.readValue(anyString(), eq(StoryAnalysisResponse.class)))
                .thenThrow(new com.fasterxml.jackson.core.JsonParseException(null, "Invalid JSON"));
        
        when(objectMapper.readTree(anyString()))
                .thenThrow(new com.fasterxml.jackson.core.JsonParseException(null, "Invalid JSON"));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("I encountered an error while analyzing")));
    }
    
    @Test
    public void testPrepareStoriesWithFallbackParsing() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        Requirement req1 = new Requirement();
        req1.setText("Requirement 1");
        project.setRequirements(Arrays.asList(req1));
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        String jsonResponse = "{\n" +
                "  \"stories\": [\"Story 1\"],\n" +
                "  \"risks\": [\"Risk 1\"],\n" +
                "  \"nfrs\": [\"NFR 1\"],\n" +
                "  \"queries\": [\"Query 1\"],\n" +
                "  \"summary\": \"Project summary\"\n" +
                "}";
        
        when(output.getContent()).thenReturn(jsonResponse);
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        when(objectMapper.readValue(anyString(), eq(StoryAnalysisResponse.class)))
                .thenThrow(new com.fasterxml.jackson.core.JsonParseException(null, "Invalid JSON"));
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode storiesNode = mock(JsonNode.class);
        JsonNode risksNode = mock(JsonNode.class);
        JsonNode nfrsNode = mock(JsonNode.class);
        JsonNode queriesNode = mock(JsonNode.class);
        JsonNode summaryNode = mock(JsonNode.class);
        JsonNode storyItem = mock(JsonNode.class);
        JsonNode riskItem = mock(JsonNode.class);
        JsonNode nfrItem = mock(JsonNode.class);
        JsonNode queryItem = mock(JsonNode.class);
        
        when(objectMapper.readTree(anyString())).thenReturn(rootNode);
        when(rootNode.has("stories")).thenReturn(true);
        when(rootNode.has("risks")).thenReturn(true);
        when(rootNode.has("nfrs")).thenReturn(true);
        when(rootNode.has("queries")).thenReturn(true);
        when(rootNode.has("summary")).thenReturn(true);
        when(rootNode.get("stories")).thenReturn(storiesNode);
        when(rootNode.get("risks")).thenReturn(risksNode);
        when(rootNode.get("nfrs")).thenReturn(nfrsNode);
        when(rootNode.get("queries")).thenReturn(queriesNode);
        when(rootNode.get("summary")).thenReturn(summaryNode);
        
        when(storiesNode.iterator()).thenReturn(Collections.singletonList(storyItem).iterator());
        when(risksNode.iterator()).thenReturn(Collections.singletonList(riskItem).iterator());
        when(nfrsNode.iterator()).thenReturn(Collections.singletonList(nfrItem).iterator());
        when(queriesNode.iterator()).thenReturn(Collections.singletonList(queryItem).iterator());
        
        when(storyItem.asText()).thenReturn("Story 1");
        when(riskItem.asText()).thenReturn("Risk 1");
        when(nfrItem.asText()).thenReturn("NFR 1");
        when(queryItem.asText()).thenReturn("Query 1");
        when(summaryNode.asText()).thenReturn("Project summary");
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("I've analyzed the requirements")))
                .andExpect(content().string(containsString("Story 1")))
                .andExpect(content().string(containsString("Risk 1")))
                .andExpect(content().string(containsString("NFR 1")))
                .andExpect(content().string(containsString("Query 1")))
                .andExpect(content().string(containsString("Project summary")));
    }
    
    @Test
    public void testExtractProjectNameWithComplexInput() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.createProject(eq("TestProject"), anyString())).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project: TestProject with some additional text"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'TestProject' has been created")));
        
        verify(projectService).createProject(eq("TestProject"), anyString());
    }
    
    @Test
    public void testExtractRequirementWithComplexInput() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add requirement User should be able to login with email and password to project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("has been added to project")));
        
        verify(projectService).addRequirement(eq(project), contains("User should be able to login with email and password"));
    }
    
    @Test
    public void testChatWithMultipleCommandPatterns() throws Exception {
        Project project = new Project();
        project.setName("NewProject");
        
        when(projectService.createProject(eq("NewProject"), anyString())).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "I want to create a new project called NewProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'NewProject' has been created")));
        
        List<Project> projects = new ArrayList<>();
        Project p1 = new Project();
        p1.setName("Project1");
        projects.add(p1);
        
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getAllProjects()).thenReturn(projects);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show all projects please"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project1")));
        
        Project p2 = new Project();
        p2.setName("Project2");
        p2.setDescription("Description");
        p2.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName("Project2")).thenReturn(Optional.of(p2));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "tell me about project Project2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project: Project2")));
        
        Project p3 = new Project();
        p3.setName("Project3");
        Requirement req = new Requirement();
        req.setText("Requirement");
        p3.setRequirements(Arrays.asList(req));
        
        when(projectService.findProjectByName("Project3")).thenReturn(Optional.of(p3));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        String jsonResponse = "{\"stories\":[\"Story\"],\"risks\":[\"Risk\"],\"nfrs\":[\"NFR\"],\"queries\":[\"Query\"],\"summary\":\"Summary\"}";
        
        when(output.getContent()).thenReturn(jsonResponse);
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        StoryAnalysisResponse analysisResponse = new StoryAnalysisResponse();
        analysisResponse.setStories(Arrays.asList("Story"));
        analysisResponse.setRisks(Arrays.asList("Risk"));
        analysisResponse.setNfrs(Arrays.asList("NFR"));
        analysisResponse.setQueries(Arrays.asList("Query"));
        analysisResponse.setSummary("Summary");
        
        when(objectMapper.readValue(anyString(), eq(StoryAnalysisResponse.class))).thenReturn(analysisResponse);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "analyze requirements for project Project3"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("I've analyzed the requirements")));
    }
    
    @Test
    public void testGetTemplateWithDifferentTopics() throws Exception {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        
        when(output.getContent()).thenReturn("Template response");
        when(generation.getOutput()).thenReturn(output);
        when(generation.getContent()).thenReturn(output.getContent());
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        mockMvc.perform(get("/api/ai/template")
                .param("topic", "Java"))
                .andExpect(status().isOk())
                .andExpect(content().string("Template response"));
        
        mockMvc.perform(get("/api/ai/template")
                .param("topic", "Artificial Intelligence and Machine Learning"))
                .andExpect(status().isOk())
                .andExpect(content().string("Template response"));
        
        verify(chatClient, times(2)).call(any(Prompt.class));
    }
    
    @Test
    public void testGetChatHistoryWithDefaultLimit() throws Exception {
        ChatMessage message = new ChatMessage("prompt", "response");
        List<ChatMessage> messages = Arrays.asList(message);
        
        when(chatMessageRepository.findRecentMessages(eq(20))).thenReturn(messages);
        
        mockMvc.perform(get("/api/ai/chat-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prompt").value("prompt"))
                .andExpect(jsonPath("$[0].response").value("response"));
        
        verify(chatMessageRepository).findRecentMessages(20);
    }
    
    @Test
    public void testGetChatHistoryWithCustomLimit() throws Exception {
        ChatMessage message = new ChatMessage("prompt", "response");
        List<ChatMessage> messages = Arrays.asList(message);
        
        when(chatMessageRepository.findRecentMessages(eq(5))).thenReturn(messages);
        
        mockMvc.perform(get("/api/ai/chat-history")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prompt").value("prompt"))
                .andExpect(jsonPath("$[0].response").value("response"));
        
        verify(chatMessageRepository).findRecentMessages(5);
    }
    
    @Test
    public void testShowProjectWithRequirements() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Description");
        
        Requirement req1 = new Requirement();
        req1.setText("Requirement 1");
        Requirement req2 = new Requirement();
        req2.setText("Requirement 2");
        
        project.setRequirements(Arrays.asList(req1, req2));
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project: TestProject")))
                .andExpect(content().string(containsString("Description: Description")))
                .andExpect(content().string(containsString("Requirement 1")))
                .andExpect(content().string(containsString("Requirement 2")));
    }
    
    @Test
    public void testCommandsWithHelpRequest() throws Exception {
        when(projectService.hasProjects()).thenReturn(true);
        Project project = new Project();
        project.setName("ExampleProject");
        when(projectService.getFirstProject()).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "what commands can I use?"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Available commands")))
                .andExpect(content().string(containsString("create project")))
                .andExpect(content().string(containsString("list projects")))
                .andExpect(content().string(containsString("show project")))
                .andExpect(content().string(containsString("add requirement")))
                .andExpect(content().string(containsString("prepare stories")))
                .andExpect(content().string(containsString("ExampleProject")));
    }
}
