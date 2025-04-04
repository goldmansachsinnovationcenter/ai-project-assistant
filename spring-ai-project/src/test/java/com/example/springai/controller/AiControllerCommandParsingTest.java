package com.example.springai.controller;

import com.example.springai.entity.Project;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiController.class)
public class AiControllerCommandParsingTest {

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
        when(generation.getContent()).thenReturn("Default AI response");
        when(chatResponse.getResult()).thenReturn(generation);
        
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);
    }

    @Test
    public void testExtractProjectNameWithColonSeparator() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.createProject(eq("TestProject"), anyString())).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project: TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TestProject")));
    }
    
    @Test
    public void testExtractProjectNameWithSpaceSeparator() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.createProject(eq("TestProject"), anyString())).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TestProject")));
    }
    
    @Test
    public void testExtractProjectNameWithMultipleWords() throws Exception {
        Project project = new Project();
        project.setName("Test");
        
        when(projectService.createProject(eq("Test"), anyString())).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project Test Project"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test")));
    }
    
    @Test
    public void testExtractProjectNameWithNoSpaceAfterProject() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "create project"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("couldn't understand the project name")));
    }
    
    @Test
    public void testExtractRequirementWithToProject() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName(eq("TestProject"))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add requirement User login feature to project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User login feature")));
    }
    
    @Test
    public void testExtractRequirementWithoutToProject() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.findProjectByName(eq("TestProject"))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add requirement User login feature TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testExtractRequirementWithNoTextAfterRequirement() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "add requirement"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Default AI response")));
    }
    
    @Test
    public void testIsCreateProjectCommandWithNewProject() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.createProject(eq("TestProject"), anyString())).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "new project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TestProject")));
    }
    
    @Test
    public void testIsListProjectsCommandWithShowAllProjects() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        List<Project> projects = Collections.singletonList(project);
        
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getAllProjects()).thenReturn(projects);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show all projects"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TestProject")));
    }
    
    @Test
    public void testIsShowProjectCommandWithAboutProject() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        project.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName(eq("TestProject"))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "about project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TestProject")));
    }
    
    @Test
    public void testIsPrepareStoriesCommandWithAnalyzeRequirements() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName(eq("TestProject"))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "analyze requirements for project TestProject"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testIsHelpCommandWithCommands() throws Exception {
        when(projectService.hasProjects()).thenReturn(false);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "commands"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Available commands")));
    }
    
    @Test
    public void testHandleHelpCommandWithExistingProject() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        
        when(projectService.hasProjects()).thenReturn(true);
        when(projectService.getFirstProject()).thenReturn(project);
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "help"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Example with existing project")))
                .andExpect(content().string(containsString("TestProject")));
    }
    
    @Test
    public void testHandleShowProjectWithEmptyProjectName() throws Exception {
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("couldn't understand which project")));
    }
    
    @Test
    public void testHandleShowProjectWithProjectHavingDescription() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("Test Description");
        project.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName(eq("TestProject"))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Description: Test Description")));
    }
    
    @Test
    public void testHandleShowProjectWithProjectHavingEmptyDescription() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription("");
        project.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName(eq("TestProject"))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Description:"))));
    }
    
    @Test
    public void testHandleShowProjectWithProjectHavingNullDescription() throws Exception {
        Project project = new Project();
        project.setName("TestProject");
        project.setDescription(null);
        project.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName(eq("TestProject"))).thenReturn(Optional.of(project));
        
        mockMvc.perform(get("/api/ai/chat")
                .param("message", "show project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Description:"))));
    }
}
