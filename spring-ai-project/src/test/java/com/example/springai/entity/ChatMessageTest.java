package com.example.springai.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ChatMessageTest {

    @Test
    public void testChatMessageCreation() {
        String prompt = "Test Prompt";
        String response = "Test Response";
        LocalDateTime timestamp = LocalDateTime.now();
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setPrompt(prompt);
        chatMessage.setResponse(response);
        chatMessage.setTimestamp(timestamp);
        
        assertEquals(prompt, chatMessage.getPrompt());
        assertEquals(response, chatMessage.getResponse());
        assertEquals(timestamp, chatMessage.getTimestamp());
    }
    
    @Test
    public void testChatMessageConstructor() {
        String prompt = "Test Prompt";
        String response = "Test Response";
        
        ChatMessage chatMessage = new ChatMessage(prompt, response);
        
        assertEquals(prompt, chatMessage.getPrompt());
        assertEquals(response, chatMessage.getResponse());
        assertNotNull(chatMessage.getTimestamp(), "Timestamp should be set automatically");
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Long id = 1L;
        
        ChatMessage message1 = new ChatMessage();
        message1.setId(id);
        message1.setPrompt("Test Prompt");
        message1.setResponse("Test Response");
        
        ChatMessage message2 = new ChatMessage();
        message2.setId(id);
        message2.setPrompt("Different Prompt");
        message2.setResponse("Different Response");
        
        assertNotEquals(message1, message2, "Messages with same ID but different content should not be equal by default");
    }
}
