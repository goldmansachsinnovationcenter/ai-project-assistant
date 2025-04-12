package com.example.springai.mcp;

import org.junit.jupiter.api.Test;
import com.example.springai.mcp.AssistantMessage; // Use local AssistantMessage
import com.example.springai.mcp.Message; // Import local Message

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GenerationTest {

    @Test
    void constructor_WithMessage_InitializesCorrectly() {
        Message message = new AssistantMessage("Test content"); // Use local Message interface
        Generation generation = new Generation(message);
        
        assertEquals(message, generation.getOutput());
    }

    @Test
    void getOutput_ReturnsCorrectMessage() {
        Message message = new AssistantMessage("Another test"); // Use local Message interface
        Generation generation = new Generation(message);
        
        assertEquals(message, generation.getOutput());
    }

    @Test
    void toString_ReturnsNonEmptyString() {
        Message message = new AssistantMessage("ToString test"); // Use local Message interface
        Generation generation = new Generation(message);
        
        assertNotNull(generation.toString());
        assertFalse(generation.toString().isEmpty());
    }
}
