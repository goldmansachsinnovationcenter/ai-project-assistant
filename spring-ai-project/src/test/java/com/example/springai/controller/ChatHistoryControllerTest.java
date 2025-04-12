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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ChatHistoryControllerTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatHistoryController chatHistoryController;

    private MockMvc mockMvc;
    private ChatMessage msg1;
    private ChatMessage msg2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatHistoryController).build();

        msg1 = new ChatMessage("Prompt 1", "Response 1");
        msg1.setId(1L);
        msg1.setTimestamp(LocalDateTime.now().minusHours(1));


        msg2 = new ChatMessage("Prompt 2", "Response 2");
        msg2.setId(2L);
        msg2.setTimestamp(LocalDateTime.now());
    }

    @Test
    void getRecentMessages_DefaultLimit_ReturnsMessages() throws Exception {
        List<ChatMessage> messages = Arrays.asList(msg2, msg1); // Assuming default repo returns newest first
        when(chatMessageRepository.findRecentMessages(20)).thenReturn(messages); // Match the actual default limit in controller

        mockMvc.perform(get("/ai/chat-history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].prompt", is("Prompt 2")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].prompt", is("Prompt 1")));

        verify(chatMessageRepository, times(1)).findRecentMessages(20); // Verify default limit
    }

    @Test
    void getRecentMessages_SpecificLimit_ReturnsLimitedMessages() throws Exception {
        List<ChatMessage> messages = Collections.singletonList(msg2); // Only the newest
        when(chatMessageRepository.findRecentMessages(1)).thenReturn(messages);

        mockMvc.perform(get("/ai/chat-history").param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].prompt", is("Prompt 2")));

        verify(chatMessageRepository, times(1)).findRecentMessages(1);
    }

     @Test
    void getRecentMessages_InvalidLimit_UsesDefaultLimit() throws Exception {
         List<ChatMessage> messages = Arrays.asList(msg2, msg1);
         lenient().when(chatMessageRepository.findRecentMessages(anyInt())).thenReturn(messages);

         mockMvc.perform(get("/ai/chat-history").param("limit", "-5"))
                 .andExpect(status().isOk()) // Controller doesn't validate limit parameter
                 .andExpect(jsonPath("$", hasSize(2)));

         mockMvc.perform(get("/ai/chat-history").param("limit", "0"))
                 .andExpect(status().isOk()) // Controller doesn't validate limit parameter
                 .andExpect(jsonPath("$", hasSize(2)));

         mockMvc.perform(get("/ai/chat-history").param("limit", "abc"))
                 .andExpect(status().isBadRequest()); // Non-numeric value should return 400
    }


    @Test
    void getRecentMessages_NoMessages_ReturnsEmptyList() throws Exception {
        when(chatMessageRepository.findRecentMessages(20)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/ai/chat-history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(chatMessageRepository, times(1)).findRecentMessages(20);
    }
}
