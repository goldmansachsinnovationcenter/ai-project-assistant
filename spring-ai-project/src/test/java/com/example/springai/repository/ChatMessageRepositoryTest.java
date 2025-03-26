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
}
