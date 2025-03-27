package com.example.springai.repository;

import com.example.springai.Application;
import com.example.springai.entity.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    public void testSaveAndFindChatMessage() {
        ChatMessage message = new ChatMessage();
        message.setPrompt("Test prompt");
        message.setResponse("Test response");
        message.setTimestamp(LocalDateTime.now());
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        assertNotNull(savedMessage.getId());
        assertEquals("Test prompt", savedMessage.getPrompt());
        assertEquals("Test response", savedMessage.getResponse());
    }
    
    @Test
    public void testFindRecentMessages() {
        ChatMessage message1 = new ChatMessage();
        message1.setPrompt("prompt1");
        message1.setResponse("response1");
        message1.setTimestamp(LocalDateTime.now().minusHours(2));
        
        ChatMessage message2 = new ChatMessage();
        message2.setPrompt("prompt2");
        message2.setResponse("response2");
        message2.setTimestamp(LocalDateTime.now().minusHours(1));
        
        ChatMessage message3 = new ChatMessage();
        message3.setPrompt("prompt3");
        message3.setResponse("response3");
        message3.setTimestamp(LocalDateTime.now());
        
        chatMessageRepository.save(message1);
        chatMessageRepository.save(message2);
        chatMessageRepository.save(message3);
        
        List<ChatMessage> messages = chatMessageRepository.findRecentMessages();
        
        assertEquals(3, messages.size());
        assertEquals("prompt3", messages.get(0).getPrompt());
        assertEquals("prompt2", messages.get(1).getPrompt());
        assertEquals("prompt1", messages.get(2).getPrompt());
    }
    
    @Test
    public void testFindRecentMessagesWithLimit() {
        ChatMessage message1 = new ChatMessage();
        message1.setPrompt("prompt1");
        message1.setResponse("response1");
        message1.setTimestamp(LocalDateTime.now().minusHours(2));
        
        ChatMessage message2 = new ChatMessage();
        message2.setPrompt("prompt2");
        message2.setResponse("response2");
        message2.setTimestamp(LocalDateTime.now().minusHours(1));
        
        ChatMessage message3 = new ChatMessage();
        message3.setPrompt("prompt3");
        message3.setResponse("response3");
        message3.setTimestamp(LocalDateTime.now());
        
        chatMessageRepository.save(message1);
        chatMessageRepository.save(message2);
        chatMessageRepository.save(message3);
        
        List<ChatMessage> messages = chatMessageRepository.findRecentMessages(2);
        
        assertEquals(2, messages.size());
        assertEquals("prompt3", messages.get(0).getPrompt());
        assertEquals("prompt2", messages.get(1).getPrompt());
    }
    
    @Test
    public void testFindRecentMessagesWithZeroLimit() {
        ChatMessage message = new ChatMessage();
        message.setPrompt("prompt");
        message.setResponse("response");
        chatMessageRepository.save(message);
        
        List<ChatMessage> messages = chatMessageRepository.findRecentMessages(0);
        
        assertTrue(messages.isEmpty());
    }
    
    @Test
    public void testFindRecentMessagesWithEmptyRepository() {
        chatMessageRepository.deleteAll();
        
        List<ChatMessage> messages = chatMessageRepository.findRecentMessages();
        
        assertTrue(messages.isEmpty());
        
        List<ChatMessage> messagesWithLimit = chatMessageRepository.findRecentMessages(10);
        
        assertTrue(messagesWithLimit.isEmpty());
    }
}
