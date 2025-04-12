package com.example.springai.mcp;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PromptTest {

    @Test
    void constructor_WithMessages_InitializesCorrectly() {
        Message sysMsg = new SystemMessage("System instruction");
        Message userMsg = new UserMessage("User query");
        List<Message> messages = Arrays.asList(sysMsg, userMsg);

        Prompt prompt = new Prompt(messages);

        assertNotNull(prompt.getMessages());
        assertEquals(2, prompt.getMessages().size());
        assertSame(sysMsg, prompt.getMessages().get(0));
        assertSame(userMsg, prompt.getMessages().get(1));
    }

    @Test
    void constructor_WithEmptyList_InitializesCorrectly() {
        List<Message> messages = Collections.emptyList();
        Prompt prompt = new Prompt(messages);

        assertNotNull(prompt.getMessages());
        assertTrue(prompt.getMessages().isEmpty());
    }

    @Test
    void constructor_WithSingleMessage_InitializesCorrectly() {
        Message userMsg = new UserMessage("Single message");
        Prompt prompt = new Prompt(Collections.singletonList(userMsg));

        assertNotNull(prompt.getMessages());
        assertEquals(1, prompt.getMessages().size());
        assertEquals("Single message", prompt.getMessages().get(0).getContent());
    }

    @Test
    void getMessages_ReturnsCopyOfList() {
        Message sysMsg = new SystemMessage("System instruction");
        List<Message> originalMessages = new java.util.ArrayList<>();
        originalMessages.add(sysMsg);

        Prompt prompt = new Prompt(originalMessages);
        List<Message> retrievedMessages = prompt.getMessages();

        originalMessages.add(new UserMessage("New message"));

        assertEquals(1, retrievedMessages.size());
        assertSame(sysMsg, retrievedMessages.get(0));
    }

    @Test
    void constructor_WithNullList_ThrowsException() {
        assertThrows(NullPointerException.class, () -> new Prompt((List<Message>) null));
    }
}
