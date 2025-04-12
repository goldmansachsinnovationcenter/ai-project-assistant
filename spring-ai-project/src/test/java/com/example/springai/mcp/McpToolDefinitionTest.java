package com.example.springai.mcp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class McpToolDefinitionTest {

    @Test
    void builder_InitializesFieldsCorrectly() {
        McpToolParameter param1 = McpToolParameter.builder()
                .withName("param1")
                .withType("string")
                .withDescription("Parameter 1")
                .withRequired(true)
                .build();
                
        McpToolParameter param2 = McpToolParameter.builder()
                .withName("param2")
                .withType("number")
                .withDescription("Parameter 2")
                .withRequired(false)
                .build();
        
        McpToolDefinition definition = McpToolDefinition.builder()
                .withName("test_tool")
                .withDescription("Tool for testing")
                .withParameter(param1)
                .withParameter(param2)
                .build();
        
        assertEquals("test_tool", definition.getName());
        assertEquals("Tool for testing", definition.getDescription());
        assertEquals(2, definition.getParameters().size());
        assertEquals("param1", definition.getParameters().get(0).getName());
        assertEquals("param2", definition.getParameters().get(1).getName());
    }
    
    @Test
    void builder_WithNoParameters_InitializesCorrectly() {
        McpToolDefinition definition = McpToolDefinition.builder()
                .withName("empty_tool")
                .withDescription("Tool with no parameters")
                .build();
        
        assertEquals("empty_tool", definition.getName());
        assertEquals("Tool with no parameters", definition.getDescription());
        assertNotNull(definition.getParameters());
        assertTrue(definition.getParameters().isEmpty());
    }
    
    @Test
    void getName_ReturnsCorrectValue() {
        McpToolDefinition definition = McpToolDefinition.builder()
                .withName("get_name_test")
                .withDescription("Test getName()")
                .build();
        
        assertEquals("get_name_test", definition.getName());
    }
    
    @Test
    void getDescription_ReturnsCorrectValue() {
        McpToolDefinition definition = McpToolDefinition.builder()
                .withName("desc_test")
                .withDescription("This is a test description")
                .build();
        
        assertEquals("This is a test description", definition.getDescription());
    }
    
    @Test
    void getParameters_ReturnsCorrectList() {
        McpToolParameter param = McpToolParameter.builder()
                .withName("test_param")
                .withType("string")
                .withDescription("Test Parameter")
                .withRequired(true)
                .build();
        
        McpToolDefinition definition = McpToolDefinition.builder()
                .withName("param_test")
                .withDescription("Test getParameters()")
                .withParameter(param)
                .build();
        
        List<McpToolParameter> parameters = definition.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());
        assertEquals("test_param", parameters.get(0).getName());
    }
    
    @Test
    void builder_CanAddMultipleParameters() {
        McpToolDefinition definition = McpToolDefinition.builder()
                .withName("multi_param_test")
                .withDescription("Test multiple parameters")
                .withParameter(McpToolParameter.builder()
                        .withName("param1")
                        .withDescription("First parameter")
                        .build())
                .withParameter(McpToolParameter.builder()
                        .withName("param2")
                        .withDescription("Second parameter")
                        .build())
                .withParameter(McpToolParameter.builder()
                        .withName("param3")
                        .withDescription("Third parameter")
                        .build())
                .build();
        
        assertEquals(3, definition.getParameters().size());
        assertEquals("param1", definition.getParameters().get(0).getName());
        assertEquals("param2", definition.getParameters().get(1).getName());
        assertEquals("param3", definition.getParameters().get(2).getName());
    }
}
