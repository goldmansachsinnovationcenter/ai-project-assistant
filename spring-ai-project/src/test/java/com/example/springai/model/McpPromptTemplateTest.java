package com.example.springai.model;

import com.example.springai.tool.Tool; // Corrected import
import com.example.springai.mcp.McpClient; // Keep needed mcp imports
import com.example.springai.mcp.Message;
import com.example.springai.mcp.Prompt;
import com.example.springai.mcp.SystemMessage;
import com.example.springai.mcp.UserMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class McpPromptTemplateTest {

    @Mock
    private Tool mockTool1;

    @Mock
    private Tool mockTool2;
    
    @Mock
    private Tool mockToolNoParams;

    @Mock
    private McpClient mockMcpClient;

    @InjectMocks
    private McpPromptTemplate mcpPromptTemplate;

    private List<Tool> toolsWithParams;
    private List<Tool> toolsMixed;
    private List<Tool> toolsEmpty;

    @BeforeEach
    void setUp() {
        lenient().when(mockTool1.getName()).thenReturn("tool-one");
        lenient().when(mockTool1.getDescription()).thenReturn("Description for tool one");
        lenient().when(mockTool1.getParameterNames()).thenReturn(new String[]{"paramA", "paramB"});

        lenient().when(mockTool2.getName()).thenReturn("tool-two");
        lenient().when(mockTool2.getDescription()).thenReturn("Description for tool two");
        lenient().when(mockTool2.getParameterNames()).thenReturn(new String[]{"paramC"});
        
        lenient().when(mockToolNoParams.getName()).thenReturn("tool-no-params");
        lenient().when(mockToolNoParams.getDescription()).thenReturn("Description for tool with no params");
        lenient().when(mockToolNoParams.getParameterNames()).thenReturn(new String[]{});

        toolsWithParams = Arrays.asList(mockTool1, mockTool2);
        toolsMixed = Arrays.asList(mockTool1, mockToolNoParams);
        toolsEmpty = Collections.emptyList();

        lenient().when(mockMcpClient.getTools()).thenReturn(toolsWithParams); // Default mock behavior
    }


    @Test
    void createToolCallingPrompt_WithToolsAndParams_GeneratesCorrectString() {
        String userMessage = "Use tool one";
        String expectedPrompt = "You are an AI assistant for project management. " +
                "If the user is asking to perform any of the following actions, " +
                "use the appropriate tool to help them.\n\n" +
                "Available tools:\n" +
                "- tool-one: Description for tool one\n" +
                "  Parameters: paramA, paramB\n" +
                "- tool-two: Description for tool two\n" +
                "  Parameters: paramC\n" +
                "\nUser message: Use tool one";

        String actualPrompt = mcpPromptTemplate.createToolCallingPrompt(userMessage, toolsWithParams);
        assertEquals(expectedPrompt, actualPrompt);
    }
    
    @Test
    void createToolCallingPrompt_WithMixedTools_GeneratesCorrectString() {
        String userMessage = "Use tool one or the other";
        String expectedPrompt = "You are an AI assistant for project management. " +
                "If the user is asking to perform any of the following actions, " +
                "use the appropriate tool to help them.\n\n" +
                "Available tools:\n" +
                "- tool-one: Description for tool one\n" +
                "  Parameters: paramA, paramB\n" +
                "- tool-no-params: Description for tool with no params\n" + // No parameters line
                "\nUser message: Use tool one or the other";

        String actualPrompt = mcpPromptTemplate.createToolCallingPrompt(userMessage, toolsMixed);
        assertEquals(expectedPrompt, actualPrompt);
    }

    @Test
    void createToolCallingPrompt_WithNoTools_GeneratesCorrectString() {
        String userMessage = "Hello";
        String expectedPrompt = "You are an AI assistant for project management. " +
                "If the user is asking to perform any of the following actions, " +
                "use the appropriate tool to help them.\n\n" +
                "Available tools:\n" + // Still includes header
                "\nUser message: Hello";

        String actualPrompt = mcpPromptTemplate.createToolCallingPrompt(userMessage, toolsEmpty);
        assertEquals(expectedPrompt, actualPrompt);
    }


    @Test
    void createMcpPrompt_WithToolsAndParams_GeneratesCorrectPromptObject() {
        String userMessage = "Create a project";
        when(mockMcpClient.getTools()).thenReturn(toolsWithParams); // Ensure correct tools are returned

        Prompt prompt = mcpPromptTemplate.createMcpPrompt(userMessage, mockMcpClient);

        assertNotNull(prompt);
        assertEquals(2, prompt.getMessages().size());

        Message systemMsg = prompt.getMessages().get(0);
        assertTrue(systemMsg instanceof SystemMessage);
        String expectedSystemContent = "You are an AI assistant for project management. " +
                "If the user is asking to create, list, or show projects, add requirements, or prepare stories, " +
                "use the appropriate tool to help them.\n\n" +
                "Available tools:\n" +
                "- tool-one: Description for tool one\n" +
                "  Parameters: paramA, paramB\n" +
                "- tool-two: Description for tool two\n" +
                "  Parameters: paramC\n" +
                "\nWhen the user asks to perform an action, respond with a JSON object containing the tool name and parameters.\n" +
                "Example: {\"tool\": \"create-project\", \"parameters\": {\"name\": \"MyProject\", \"description\": \"A sample project\"}}\n\n";
        assertEquals(expectedSystemContent.trim(), systemMsg.getContent().trim());

        Message userMsg = prompt.getMessages().get(1);
        assertTrue(userMsg instanceof UserMessage);
        assertEquals(userMessage, userMsg.getContent());
    }
    
    @Test
    void createMcpPrompt_WithMixedTools_GeneratesCorrectPromptObject() {
        String userMessage = "Use tool one or the other";
        when(mockMcpClient.getTools()).thenReturn(toolsMixed); // Ensure correct tools are returned

        Prompt prompt = mcpPromptTemplate.createMcpPrompt(userMessage, mockMcpClient);

        assertNotNull(prompt);
        assertEquals(2, prompt.getMessages().size());

        Message systemMsg = prompt.getMessages().get(0);
        assertTrue(systemMsg instanceof SystemMessage);
        String expectedSystemContent = "You are an AI assistant for project management. " +
                "If the user is asking to create, list, or show projects, add requirements, or prepare stories, " +
                "use the appropriate tool to help them.\n\n" +
                "Available tools:\n" +
                "- tool-one: Description for tool one\n" +
                "  Parameters: paramA, paramB\n" +
                "- tool-no-params: Description for tool with no params\n" + // No parameters line
                "\nWhen the user asks to perform an action, respond with a JSON object containing the tool name and parameters.\n" +
                "Example: {\"tool\": \"create-project\", \"parameters\": {\"name\": \"MyProject\", \"description\": \"A sample project\"}}\n\n";
        assertEquals(expectedSystemContent.trim(), systemMsg.getContent().trim());

        Message userMsg = prompt.getMessages().get(1);
        assertTrue(userMsg instanceof UserMessage);
        assertEquals(userMessage, userMsg.getContent());
    }

    @Test
    void createMcpPrompt_WithNoTools_GeneratesCorrectPromptObject() {
        String userMessage = "Hi assistant";
        when(mockMcpClient.getTools()).thenReturn(toolsEmpty); // Ensure empty list is returned

        Prompt prompt = mcpPromptTemplate.createMcpPrompt(userMessage, mockMcpClient);

        assertNotNull(prompt);
        assertEquals(2, prompt.getMessages().size());

        Message systemMsg = prompt.getMessages().get(0);
        assertTrue(systemMsg instanceof SystemMessage);
        String expectedSystemContent = "You are an AI assistant for project management. " +
                "If the user is asking to create, list, or show projects, add requirements, or prepare stories, " +
                "use the appropriate tool to help them.\n\n" +
                "Available tools:\n" + // Still includes header
                "\nWhen the user asks to perform an action, respond with a JSON object containing the tool name and parameters.\n" +
                "Example: {\"tool\": \"create-project\", \"parameters\": {\"name\": \"MyProject\", \"description\": \"A sample project\"}}\n\n";
        assertEquals(expectedSystemContent.trim(), systemMsg.getContent().trim());

        Message userMsg = prompt.getMessages().get(1);
        assertTrue(userMsg instanceof UserMessage);
        assertEquals(userMessage, userMsg.getContent());
    }


    @Test
    void getToolNamesList_WithMultipleTools_ReturnsCorrectString() {
        String expectedNames = "tool-one, tool-two";
        String actualNames = mcpPromptTemplate.getToolNamesList(toolsWithParams);
        assertEquals(expectedNames, actualNames);
    }

    @Test
    void getToolNamesList_WithSingleTool_ReturnsCorrectString() {
        String expectedNames = "tool-one";
        String actualNames = mcpPromptTemplate.getToolNamesList(Collections.singletonList(mockTool1));
        assertEquals(expectedNames, actualNames);
    }

    @Test
    void getToolNamesList_WithNoTools_ReturnsEmptyString() {
        String expectedNames = "";
        String actualNames = mcpPromptTemplate.getToolNamesList(toolsEmpty);
        assertEquals(expectedNames, actualNames);
    }
}
