package com.example.springai.controller;

import com.example.springai.entity.*;
import com.example.springai.mcp.Tool;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.service.McpToolService;
import com.example.springai.service.ProjectService;
import org.junit.jupiter.api.Test;
import com.example.springai.mcp.ChatResponse;
import com.example.springai.mcp.Generation;
import com.example.springai.mcp.AssistantMessage;
import com.example.springai.mcp.Message;
import com.example.springai.mcp.Prompt;
import com.example.springai.mcp.McpClient;
import com.example.springai.mcp.McpTool;
import com.example.springai.mcp.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiController.class)
public class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageRepository chatMessageRepository;
    
    @MockBean
    private ChatClient chatClient;
    
    @MockBean
    private ProjectService projectService;
    
    @MockBean
    private McpToolService mcpToolService;
    
    @MockBean
    private McpClient mcpClient;

    @Test
    public void testGetChatHistory() throws Exception {
        ChatMessage message1 = new ChatMessage("prompt1", "response1");
        ChatMessage message2 = new ChatMessage("prompt2", "response2");
        List<ChatMessage> messages = Arrays.asList(message1, message2);
        
        when(chatMessageRepository.findRecentMessages(anyInt())).thenReturn(messages);
        
        mockMvc.perform(get("/api/ai/chat-history")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prompt").value("prompt1"))
                .andExpect(jsonPath("$[0].response").value("response1"))
                .andExpect(jsonPath("$[1].prompt").value("prompt2"))
                .andExpect(jsonPath("$[1].response").value("response2"));
    }

    @Test
    public void testCreateProjectCommand() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.createProject(anyString(), anyString())).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'TestProject' has been created")));
        
        verify(projectService).createProject("TestProject", "");
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    public void testCreateProjectCommandWithInvalidName() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("couldn't understand the project name")));
    }

    @Test
    public void testListProjectsCommandWithNoProjects() throws Exception {
        when(projectService.hasProjects()).thenReturn(false);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "list projects"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("You don't have any projects yet")));
    }

    @Test
    public void testListProjectsCommandWithProjects() throws Exception {
        Project project1 = new Project();
        project1.setName("Project1");
        Project project2 = new Project();
        project2.setName("Project2");
        
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getAllProjects()).thenReturn(Arrays.asList(project1, project2));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "list projects"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project1")))
                .andExpect(content().string(containsString("Project2")));
    }

    @Test
    public void testShowProjectCommandWithInvalidName() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("couldn't understand which project")));
    }

    @Test
    public void testShowProjectCommandWithNonexistentProject() throws Exception {
        when(projectService.findProjectByName(anyString())).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project NonExistent"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'NonExistent' not found")));
    }

    @Test
    public void testAddRequirementCommand() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add requirement User login feature to project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("has been added to project")));
        
        verify(projectService).addRequirement(eq(project), anyString());
    }

    @Test
    public void testPrepareStoriesCommandWithNoRequirements() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "prepare stories for project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("doesn't have any requirements yet")));
    }

    @Test
    public void testHelpCommandWithNoProjects() throws Exception {
        when(projectService.hasProjects()).thenReturn(false);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "help"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Available commands")))
                .andExpect(content().string(not(containsString("Example with existing project"))));
    }

    @Test
    public void testHelpCommandWithExistingProject() throws Exception {
        Project project = new Project();
        project.setName("ExampleProject");
        
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getFirstProject()).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "help"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Example with existing project")))
                .andExpect(content().string(containsString("ExampleProject")));
    }

    @Test
    public void testNonCommandChat() throws Exception {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        when(output.getContent()).thenReturn("AI response");
        when(generation.getOutput()).thenReturn(output);
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "Hello AI"))
                .andExpect(status().isOk())
                .andExpect(content().string("AI response"));
        
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    public void testGetTemplate() throws Exception {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        when(output.getContent()).thenReturn("Template response");
        when(generation.getOutput()).thenReturn(output);
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        mockMvc.perform(get("/api/ai/template")
                .param("topic", "Java"))
                .andExpect(status().isOk())
                .andExpect(content().string("Template response"));
    }
    
    @Test
    public void testMcpToolCalling() throws Exception {
        List<Tool> mcpTools = Arrays.asList(
                mock(Tool.class),
                mock(Tool.class)
        );
        when(mcpToolService.getTools()).thenReturn(mcpTools);
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        when(output.getContent()).thenReturn("Project 'TestProject' has been created");
        when(generation.getOutput()).thenReturn(output);
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'TestProject' has been created")));
        
        verify(chatClient).call(any(Prompt.class));
        
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }
    
    @Test
    public void testMcpClientChat() throws Exception {
        when(mcpClient.getTools()).thenReturn(Arrays.asList(mock(Tool.class)));
        
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        when(output.getContent()).thenReturn("MCP client response");
        when(generation.getOutput()).thenReturn(output);
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
        
        mockMvc.perform(get("/api/mcp-client/chat")
                .param("message", "create project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("MCP client response")));
        
        verify(chatClient).call(any(Prompt.class));
    }
}
