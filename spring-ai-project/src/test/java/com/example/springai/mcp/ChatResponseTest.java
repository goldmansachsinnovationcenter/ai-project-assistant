package com.example.springai.mcp;

import org.junit.jupiter.api.Test;
import com.example.springai.mcp.Generation; // Use local Generation

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ChatResponseTest {

    @Test
    void constructor_WithGenerations_InitializesCorrectly() {
        Generation gen1 = mock(com.example.springai.mcp.Generation.class);
        Generation gen2 = mock(com.example.springai.mcp.Generation.class);
        List<Generation> generations = Arrays.asList(gen1, gen2);
        
        ChatResponse response = new ChatResponse(generations);
        
        assertEquals(generations, response.getGenerations());
        assertEquals(2, response.getGenerations().size());
    }

    @Test
    void getResult_ReturnsFirstGeneration() {
        Generation gen1 = mock(com.example.springai.mcp.Generation.class);
        Generation gen2 = mock(com.example.springai.mcp.Generation.class);
        List<Generation> generations = Arrays.asList(gen1, gen2);
        
        ChatResponse response = new ChatResponse(generations);
        
        assertSame(gen1, response.getResult());
    }

    @Test
    void getResult_WhenNoGenerations_ReturnsNull() {
        ChatResponse response = new ChatResponse(Collections.emptyList());
        
        assertNull(response.getResult());
    }

    @Test
    void getResults_ReturnsCorrectList() {
        Generation gen1 = mock(com.example.springai.mcp.Generation.class);
        Generation gen2 = mock(com.example.springai.mcp.Generation.class);
        List<Generation> generations = Arrays.asList(gen1, gen2);
        
        ChatResponse response = new ChatResponse(generations);
        
        assertEquals(generations, response.getGenerations());
    }

    @Test
    void toString_ReturnsNonEmptyString() {
         Generation gen1 = mock(com.example.springai.mcp.Generation.class);
         List<Generation> generations = Collections.singletonList(gen1);
         ChatResponse response = new ChatResponse(generations);
         
         assertNotNull(response.toString());
         assertFalse(response.toString().isEmpty());
    }
}
