package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.server.tool.McpTool;
import org.springframework.ai.ollama.OllamaChatClient;
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
    private OllamaChatClient chatClient;

    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @MockBean
    private McpClient mcpClient;

    private List<McpTool> mockTools;

    @BeforeEach
    public void setup() {
        mockTools = Arrays.asList(
                mock(McpTool.class),
                mock(McpTool.class)
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
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        mockMvc.perform(get("/api/ai/mcp-client-chat")
                .param("message", "create project TestProject"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Project 'TestProject' has been created")));

        verify(chatClient).call(argThat(prompt -> 
                prompt.getOptions() != null && 
                prompt.getOptions().containsKey("tools")));
        
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
