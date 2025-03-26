package com.example.springai.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ChatMessageTest {

    @Test
    public void testChatMessageConstructorAndGetters() {
        String prompt = "Test prompt";
        String response = "Test response";
        
        ChatMessage message = new ChatMessage(prompt, response);
        
        assertEquals(prompt, message.getPrompt());
        assertEquals(response, message.getResponse());
        assertNotNull(message.getTimestamp());
    }
    
    @Test
    public void testChatMessageSetters() {
        ChatMessage message = new ChatMessage();
        
        Long id = 1L;
        String prompt = "Updated prompt";
        String response = "Updated response";
        LocalDateTime timestamp = LocalDateTime.now();
        
        message.setId(id);
        message.setPrompt(prompt);
        message.setResponse(response);
        message.setTimestamp(timestamp);
        
        assertEquals(id, message.getId());
        assertEquals(prompt, message.getPrompt());
        assertEquals(response, message.getResponse());
        assertEquals(timestamp, message.getTimestamp());
    }
}
