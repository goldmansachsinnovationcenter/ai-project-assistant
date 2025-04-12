package com.example.springai.mcp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void getContent_ReturnsCorrectValue() {
        Message message = new UserMessage("Test content");
        assertEquals("Test content", message.getContent());
    }

    @Test
    void userMessage_Constructor_SetsContentCorrectly() {
        UserMessage message = new UserMessage("User message");
        assertEquals("User message", message.getContent());
    }

    @Test
    void systemMessage_Constructor_SetsContentCorrectly() {
        SystemMessage message = new SystemMessage("System message");
        assertEquals("System message", message.getContent());
    }

    @Test
    void assistantMessage_Constructor_SetsContentCorrectly() {
        AssistantMessage message = new AssistantMessage("Assistant message");
        assertEquals("Assistant message", message.getContent());
    }

    @Test
    void userMessage_GetRole_ReturnsUser() {
        UserMessage message = new UserMessage("User message");
        assertEquals("user", message.getRole());
    }

    @Test
    void systemMessage_GetRole_ReturnsSystem() {
        SystemMessage message = new SystemMessage("System message");
        assertEquals("system", message.getRole());
    }

    @Test
    void assistantMessage_GetRole_ReturnsAssistant() {
        AssistantMessage message = new AssistantMessage("Assistant message");
        assertEquals("assistant", message.getRole());
    }
}
