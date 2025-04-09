package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ChatHistoryControllerTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatHistoryController chatHistoryController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatHistoryController).build();
    }

    @Test
    public void testGetChatHistory() throws Exception {
        ChatMessage message1 = new ChatMessage("Test prompt 1", "Test response 1");
        ChatMessage message2 = new ChatMessage("Test prompt 2", "Test response 2");
        List<ChatMessage> messages = Arrays.asList(message1, message2);

        when(chatMessageRepository.findRecentMessages(20)).thenReturn(messages);

        mockMvc.perform(get("/ai/chat-history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(chatMessageRepository, times(1)).findRecentMessages(20);
    }

    @Test
    public void testGetChatHistoryWithCustomLimit() throws Exception {
        ChatMessage message1 = new ChatMessage("Test prompt 1", "Test response 1");
        List<ChatMessage> messages = Arrays.asList(message1);

        when(chatMessageRepository.findRecentMessages(5)).thenReturn(messages);

        mockMvc.perform(get("/ai/chat-history?limit=5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(chatMessageRepository, times(1)).findRecentMessages(5);
    }

    @Test
    public void testGetChatHistoryAlternativeEndpoint() throws Exception {
        ChatMessage message1 = new ChatMessage("Test prompt 1", "Test response 1");
        ChatMessage message2 = new ChatMessage("Test prompt 2", "Test response 2");
        List<ChatMessage> messages = Arrays.asList(message1, message2);

        when(chatMessageRepository.findRecentMessages(20)).thenReturn(messages);

        mockMvc.perform(get("/api/ai/chat-history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(chatMessageRepository, times(1)).findRecentMessages(20);
    }
}
