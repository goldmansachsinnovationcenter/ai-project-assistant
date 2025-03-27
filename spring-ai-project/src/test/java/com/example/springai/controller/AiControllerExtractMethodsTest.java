package com.example.springai.controller;

import com.example.springai.entity.Project;
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
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests specifically targeting the private extract methods in AiController
 * using reflection to access and test these methods directly
 */
@WebMvcTest(AiController.class)
public class AiControllerExtractMethodsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AiController aiController;

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
    public void testExtractProjectNameWithNoProjectKeyword() throws Exception {
        String result = invokeExtractProjectName("message without project keyword");
        assertEquals("keyword", result);
    }
    
    @Test
    public void testExtractProjectNameWithProjectKeywordAtEnd() throws Exception {
        String result = invokeExtractProjectName("this is a project");
        assertEquals("", result);
    }
    
    @Test
    public void testExtractProjectNameWithColonSeparator() throws Exception {
        String result = invokeExtractProjectName("create project: TestProject");
        assertEquals("TestProject", result);
    }
    
    @Test
    public void testExtractProjectNameWithColonAndMultipleWords() throws Exception {
        String result = invokeExtractProjectName("create project: Test Project Description");
        assertEquals("Test", result);
    }
    
    @Test
    public void testExtractProjectNameWithSpaceOnly() throws Exception {
        String result = invokeExtractProjectName("create project TestProject");
        assertEquals("TestProject", result);
    }
    
    @Test
    public void testExtractProjectNameWithSpaceAndMultipleWords() throws Exception {
        String result = invokeExtractProjectName("create project Test Project Description");
        assertEquals("Test", result);
    }
    
    @Test
    public void testExtractRequirementWithToProjectKeyword() throws Exception {
        String result = invokeExtractRequirement("add requirement User login feature to project TestProject");
        assertEquals("User login feature", result);
    }
    
    @Test
    public void testExtractRequirementWithoutToProjectKeyword() throws Exception {
        String result = invokeExtractRequirement("add requirement User login feature");
        assertEquals("User login feature", result);
    }
    
    @Test
    public void testExtractRequirementWithNoTextAfterRequirement() throws Exception {
        String result = invokeExtractRequirement("add requirement");
        assertEquals("", result);
    }
    
    @Test
    public void testExtractRequirementWithRequirementAtEnd() throws Exception {
        String result = invokeExtractRequirement("this is a requirement");
        assertEquals("", result);
    }
    
    @Test
    public void testIsCreateProjectCommandWithCreateProject() throws Exception {
        boolean result = invokeIsCreateProjectCommand("create project TestProject");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsCreateProjectCommandWithNewProject() throws Exception {
        boolean result = invokeIsCreateProjectCommand("new project TestProject");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsCreateProjectCommandWithUnrelatedMessage() throws Exception {
        boolean result = invokeIsCreateProjectCommand("hello world");
        assertEquals(false, result);
    }
    
    @Test
    public void testIsAddRequirementCommandWithToProject() throws Exception {
        boolean result = invokeIsAddRequirementCommand("add requirement User login to project TestProject");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsAddRequirementCommandWithoutToProject() throws Exception {
        boolean result = invokeIsAddRequirementCommand("add requirement User login");
        assertEquals(false, result);
    }
    
    @Test
    public void testIsListProjectsCommandWithListProjects() throws Exception {
        boolean result = invokeIsListProjectsCommand("list projects");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsListProjectsCommandWithShowAllProjects() throws Exception {
        boolean result = invokeIsListProjectsCommand("show all projects");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsShowProjectCommandWithShowProject() throws Exception {
        boolean result = invokeIsShowProjectCommand("show project TestProject");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsShowProjectCommandWithAboutProject() throws Exception {
        boolean result = invokeIsShowProjectCommand("about project TestProject");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsPrepareStoriesCommandWithPrepareStories() throws Exception {
        boolean result = invokeIsPrepareStoriesCommand("prepare stories for project TestProject");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsPrepareStoriesCommandWithAnalyzeRequirements() throws Exception {
        boolean result = invokeIsPrepareStoriesCommand("analyze requirements for project TestProject");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsHelpCommandWithHelp() throws Exception {
        boolean result = invokeIsHelpCommand("help");
        assertEquals(true, result);
    }
    
    @Test
    public void testIsHelpCommandWithCommands() throws Exception {
        boolean result = invokeIsHelpCommand("commands");
        assertEquals(true, result);
    }
    
    
    private String invokeExtractProjectName(String message) throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractProjectName", String.class);
        method.setAccessible(true);
        return (String) method.invoke(aiController, message);
    }
    
    private String invokeExtractRequirement(String message) throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractRequirement", String.class);
        method.setAccessible(true);
        return (String) method.invoke(aiController, message);
    }
    
    private boolean invokeIsCreateProjectCommand(String message) throws Exception {
        Method method = AiController.class.getDeclaredMethod("isCreateProjectCommand", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(aiController, message);
    }
    
    private boolean invokeIsAddRequirementCommand(String message) throws Exception {
        Method method = AiController.class.getDeclaredMethod("isAddRequirementCommand", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(aiController, message);
    }
    
    private boolean invokeIsListProjectsCommand(String message) throws Exception {
        Method method = AiController.class.getDeclaredMethod("isListProjectsCommand", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(aiController, message);
    }
    
    private boolean invokeIsShowProjectCommand(String message) throws Exception {
        Method method = AiController.class.getDeclaredMethod("isShowProjectCommand", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(aiController, message);
    }
    
    private boolean invokeIsPrepareStoriesCommand(String message) throws Exception {
        Method method = AiController.class.getDeclaredMethod("isPrepareStoriesCommand", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(aiController, message);
    }
    
    private boolean invokeIsHelpCommand(String message) throws Exception {
        Method method = AiController.class.getDeclaredMethod("isHelpCommand", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(aiController, message);
    }
}
