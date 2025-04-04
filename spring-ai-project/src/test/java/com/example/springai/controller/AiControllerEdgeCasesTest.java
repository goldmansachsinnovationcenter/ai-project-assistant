package com.example.springai.controller;

import com.example.springai.entity.Project;
import com.example.springai.model.StoryAnalysisResponse;
import com.example.springai.service.ProjectService;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.mcp.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AiControllerEdgeCasesTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ProjectService projectService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AiController aiController;

    private ChatResponse mockChatResponse;
    private Generation mockGeneration;

    @Test
    void testExtractProjectNameWithEmptyMessage() throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractProjectName", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "");
        
        assertEquals("", result, "Empty message should return empty project name");
    }
    
    @Test
    void testExtractProjectNameWithNoProjectKeyword() throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractProjectName", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "This message has no keyword");
        
        assertEquals("", result, "Message without 'project' keyword should return empty project name");
    }
    
    @Test
    void testExtractProjectNameWithProjectAtEnd() throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractProjectName", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "This is about a project");
        
        assertEquals("", result, "Message with 'project' at the end should return empty project name");
    }
    
    @Test
    void testExtractProjectNameWithColonFormat() throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractProjectName", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "create project: TestProject with details");
        
        assertEquals("TestProject", result, "Should extract project name after colon");
    }
    
    @Test
    void testExtractRequirementWithEmptyMessage() throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractRequirement", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "");
        
        assertEquals("", result, "Empty message should return empty requirement");
    }
    
    @Test
    void testExtractRequirementWithNoRequirementKeyword() throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractRequirement", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "This message has no keyword");
        
        assertEquals("", result, "Message without 'requirement' keyword should return empty requirement");
    }
    
    @Test
    void testExtractRequirementWithRequirementAtEnd() throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractRequirement", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "This is about a requirement");
        
        assertEquals("", result, "Message with 'requirement' at the end should return empty requirement");
    }
    
    @Test
    void testHandleCreateProjectWithEmptyName() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handleCreateProject", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "");
        
        assertTrue(result.contains("couldn't understand"), "Should return error message for empty project name");
    }
    
    @Test
    void testHandleListProjectsWithNoProjects() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handleListProjects");
        method.setAccessible(true);
        
        when(projectService.hasProjects()).thenReturn(false);
        
        String result = (String) method.invoke(aiController);
        
        assertTrue(result.contains("don't have any projects"), "Should return message about no projects");
    }
    
    @Test
    void testHandleShowProjectWithEmptyName() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handleShowProject", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "");
        
        assertTrue(result.contains("couldn't understand"), "Should return error message for empty project name");
    }
    
    @Test
    void testHandleShowProjectWithNonExistentProject() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handleShowProject", String.class);
        method.setAccessible(true);
        
        when(projectService.findProjectByName("NonExistentProject")).thenReturn(Optional.empty());
        
        String result = (String) method.invoke(aiController, "NonExistentProject");
        
        assertTrue(result.contains("not found"), "Should return not found message for non-existent project");
    }
    
    @Test
    void testHandleAddRequirementWithEmptyProjectName() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handleAddRequirement", String.class, String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "", "Some requirement");
        
        assertTrue(result.contains("couldn't understand"), "Should return error message for empty project name");
    }
    
    @Test
    void testHandleAddRequirementWithEmptyRequirement() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handleAddRequirement", String.class, String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "TestProject", "");
        
        assertTrue(result.contains("couldn't understand"), "Should return error message for empty requirement");
    }
    
    @Test
    void testHandlePrepareStoriesWithEmptyProjectName() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handlePrepareStories", String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "");
        
        assertTrue(result.contains("couldn't understand"), "Should return error message for empty project name");
    }
    
    @Test
    void testHandlePrepareStoriesWithNonExistentProject() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handlePrepareStories", String.class);
        method.setAccessible(true);
        
        when(projectService.findProjectByName("NonExistentProject")).thenReturn(Optional.empty());
        
        String result = (String) method.invoke(aiController, "NonExistentProject");
        
        assertTrue(result.contains("not found"), "Should return not found message for non-existent project");
    }
    
    @Test
    void testHandlePrepareStoriesWithNoRequirements() throws Exception {
        Method method = AiController.class.getDeclaredMethod("handlePrepareStories", String.class);
        method.setAccessible(true);
        
        Project project = new Project();
        project.setName("TestProject");
        project.setRequirements(new ArrayList<>());
        
        when(projectService.findProjectByName("TestProject")).thenReturn(Optional.of(project));
        
        String result = (String) method.invoke(aiController, "TestProject");
        
        assertTrue(result.contains("doesn't have any requirements"), "Should return message about no requirements");
    }
    
    
    @Test
    void testExtractExtractionFieldWithInvalidJson() throws Exception {
        lenient().when(objectMapper.readTree(anyString()))
            .thenThrow(new RuntimeException("Invalid JSON"));
        
        Method method = AiController.class.getDeclaredMethod("extractExtractionField", String.class, String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "Invalid JSON", "fieldName");
        
        assertEquals("", result, "Should return empty string for invalid JSON");
    }
    
    @Test
    void testExtractExtractionFieldWithMissingField() throws Exception {
        Method method = AiController.class.getDeclaredMethod("extractExtractionField", String.class, String.class);
        method.setAccessible(true);
        
        String result = (String) method.invoke(aiController, "{\"otherField\": \"value\"}", "missingField");
        
        assertEquals("", result, "Should return empty string for missing field");
    }
}
