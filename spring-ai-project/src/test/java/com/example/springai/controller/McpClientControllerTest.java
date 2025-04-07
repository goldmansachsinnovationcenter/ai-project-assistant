package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(McpClientController.class)
public class McpClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @MockBean
    private McpServer mcpServer;

    private List<McpTool> mockTools;

    @BeforeEach
    public void setup() {
        McpTool mockTool1 = mock(McpTool.class);
        when(mockTool1.getName()).thenReturn("create-project");
        when(mockTool1.getDescription()).thenReturn("Create a new project");
        when(mockTool1.getParameters()).thenReturn(Collections.emptyMap());
        
        McpTool mockTool2 = mock(McpTool.class);
        when(mockTool2.getName()).thenReturn("list-projects");
        when(mockTool2.getDescription()).thenReturn("List all projects");
        when(mockTool2.getParameters()).thenReturn(Collections.emptyMap());
        
        mockTools = Arrays.asList(mockTool1, mockTool2);
        
        when(mcpServer.getTools()).thenReturn(mockTools);
    }

    @Test
    public void testMcpClientChat() throws Exception {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        when(generation.getContent()).thenReturn("Project 'TestProject' has been created");
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        mockMvc.perform(get("/api/ai/mcp-client-chat")
                .param("message", "create project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'TestProject' has been created")));

        verify(chatClient).call(any(Prompt.class));
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    public void testMcpClientChatError() throws Exception {
        when(chatClient.call(any(Prompt.class))).thenThrow(new RuntimeException("Test error"));

        mockMvc.perform(get("/api/ai/mcp-client-chat")
                .param("message", "create project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("I'm sorry, I encountered an error")));

        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }
}
