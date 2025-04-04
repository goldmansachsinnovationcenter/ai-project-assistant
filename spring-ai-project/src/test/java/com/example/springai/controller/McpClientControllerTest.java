package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.mcp.McpClient;
import com.example.springai.mcp.Tool;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
    private McpClient mcpClient;

    private List<Tool> mockTools;

    @BeforeEach
    public void setup() {
        mockTools = Arrays.asList(
                mock(Tool.class),
                mock(Tool.class)
        );
        
        when(mcpClient.getTools()).thenReturn(mockTools);
    }

    @Test
    public void testMcpClientChat() throws Exception {
        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        AssistantMessage output = mock(AssistantMessage.class);
        when(output.getContent()).thenReturn("Project 'TestProject' has been created");
        when(generation.getOutput()).thenReturn(output);
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
