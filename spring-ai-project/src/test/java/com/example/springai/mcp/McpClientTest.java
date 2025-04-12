package com.example.springai.mcp;

import com.example.springai.tool.Tool; // Corrected import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;


@ExtendWith(MockitoExtension.class)
class McpClientTest {

    @Mock
    private Tool mockTool1;

    @Mock
    private Tool mockTool2;


    @Mock
    private Tool mockTool3;

    private McpClient mcpClientWithTools;
    private McpClient mcpClientWithoutTools;

    @BeforeEach
    void setUp() {
        lenient().when(mockTool1.getName()).thenReturn("tool-one");
        lenient().when(mockTool1.getDescription()).thenReturn("Description for tool one");
        lenient().when(mockTool1.getParameterNames()).thenReturn(new String[]{"paramA", "paramB"});

        lenient().when(mockTool2.getName()).thenReturn("tool-two");
        lenient().when(mockTool2.getDescription()).thenReturn("Description for tool two");
        lenient().when(mockTool2.getParameterNames()).thenReturn(new String[]{"paramC"});

        lenient().when(mockTool3.getName()).thenReturn("tool-three");
        lenient().when(mockTool3.getDescription()).thenReturn("Description for tool three");
        lenient().when(mockTool3.getParameterNames()).thenReturn(new String[]{}); // No parameters

        List<Tool> tools = Arrays.asList(mockTool1, mockTool2, mockTool3);
        mcpClientWithTools = new McpClient(tools);

        mcpClientWithoutTools = new McpClient(Collections.emptyList());
    }

    @Test
    void constructor_InitializesToolsCorrectly() {
        assertNotNull(mcpClientWithTools);
        assertEquals(3, mcpClientWithTools.getTools().size());
        assertTrue(mcpClientWithTools.getTools().contains(mockTool1));
        assertTrue(mcpClientWithTools.getTools().contains(mockTool2));
        assertTrue(mcpClientWithTools.getTools().contains(mockTool3));

        assertNotNull(mcpClientWithoutTools);
        assertTrue(mcpClientWithoutTools.getTools().isEmpty());
    }

    @Test
    void getTools_ReturnsCorrectList() {
        List<Tool> tools = mcpClientWithTools.getTools();
        assertEquals(3, tools.size());
        assertTrue(tools.contains(mockTool1));
        assertTrue(tools.contains(mockTool2));
        assertTrue(tools.contains(mockTool3));

        List<Tool> noTools = mcpClientWithoutTools.getTools();
        assertTrue(noTools.isEmpty());
    }

    @Test
    void createPrompt_WithTools_GeneratesCorrectPrompt() {
        String userMessage = "Use tool one please";
        Prompt prompt = mcpClientWithTools.createPrompt(userMessage);

        assertNotNull(prompt);
        assertEquals(2, prompt.getMessages().size());

        Message systemMsg = prompt.getMessages().get(0);
        assertTrue(systemMsg instanceof SystemMessage);
        assertEquals("system", systemMsg.getRole());
        String expectedSystemContent = "You are an AI assistant for project management. " +
                "You can use the following tools:\n\n" +
                "- tool-one: Description for tool one\n" +
                "  Parameters: paramA, paramB\n\n" + // Double newline is important
                "- tool-two: Description for tool two\n" +
                "  Parameters: paramC\n\n" + // Double newline is important
                "- tool-three: Description for tool three\n" +
                "  Parameters: \n\n" + // Empty parameters
                "When the user asks to perform an action, respond with a JSON object containing the tool name and parameters.\n" +
                "Example: {\"tool\": \"create-project\", \"parameters\": {\"name\": \"MyProject\", \"description\": \"A sample project\"}}\n\n";
        assertEquals(expectedSystemContent.trim(), systemMsg.getContent().trim()); // Use trim for comparison robustness

        Message userMsg = prompt.getMessages().get(1);
        assertTrue(userMsg instanceof UserMessage);
        assertEquals("user", userMsg.getRole());
        assertEquals(userMessage, userMsg.getContent());
    }

    @Test
    void createPrompt_WithoutTools_GeneratesCorrectPrompt() {
        String userMessage = "Hello there";
        Prompt prompt = mcpClientWithoutTools.createPrompt(userMessage);

        assertNotNull(prompt);
        assertEquals(2, prompt.getMessages().size());

        Message systemMsg = prompt.getMessages().get(0);
        assertTrue(systemMsg instanceof SystemMessage);
        assertEquals("system", systemMsg.getRole());
        String expectedSystemContent = "You are an AI assistant for project management. " +
                "You can use the following tools:\n\n" + // Header should still be present
                "When the user asks to perform an action, respond with a JSON object containing the tool name and parameters.\n" +
                "Example: {\"tool\": \"create-project\", \"parameters\": {\"name\": \"MyProject\", \"description\": \"A sample project\"}}\n\n";
        assertEquals(expectedSystemContent, systemMsg.getContent());

        Message userMsg = prompt.getMessages().get(1);
        assertTrue(userMsg instanceof UserMessage);
        assertEquals("user", userMsg.getRole());
        assertEquals(userMessage, userMsg.getContent());
    }
}
