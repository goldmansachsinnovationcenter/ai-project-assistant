package com.example.springai.mcp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class McpToolParameterTest {

    @Test
    void builder_InitializesFieldsCorrectly() {
        McpToolParameter parameter = McpToolParameter.builder()
                .withName("test_param")
                .withType("string")
                .withDescription("Test parameter")
                .withRequired(true)
                .build();
        
        assertEquals("test_param", parameter.getName());
        assertEquals("string", parameter.getType());
        assertEquals("Test parameter", parameter.getDescription());
        assertTrue(parameter.isRequired());
    }
    
    @Test
    void builder_WithOptionalParameter_InitializesCorrectly() {
        McpToolParameter parameter = McpToolParameter.builder()
                .withName("optional_param")
                .withType("number")
                .withDescription("Optional parameter")
                .withRequired(false)
                .build();
        
        assertEquals("optional_param", parameter.getName());
        assertEquals("number", parameter.getType());
        assertEquals("Optional parameter", parameter.getDescription());
        assertFalse(parameter.isRequired());
    }
    
    @Test
    void getName_ReturnsCorrectValue() {
        McpToolParameter parameter = McpToolParameter.builder()
                .withName("name_test")
                .withDescription("Test getName()")
                .withRequired(true)
                .build();
        
        assertEquals("name_test", parameter.getName());
    }
    
    @Test
    void getType_ReturnsCorrectValue() {
        McpToolParameter parameter = McpToolParameter.builder()
                .withName("type_test")
                .withType("boolean")
                .withDescription("Test getType()")
                .withRequired(true)
                .build();
        
        assertEquals("boolean", parameter.getType());
    }
    
    @Test
    void getDescription_ReturnsCorrectValue() {
        McpToolParameter parameter = McpToolParameter.builder()
                .withName("desc_test")
                .withDescription("This is a test description")
                .withRequired(true)
                .build();
        
        assertEquals("This is a test description", parameter.getDescription());
    }
    
    @Test
    void isRequired_ReturnsTrueForRequiredParameter() {
        McpToolParameter parameter = McpToolParameter.builder()
                .withName("required_test")
                .withDescription("Required parameter")
                .withRequired(true)
                .build();
        
        assertTrue(parameter.isRequired());
    }
    
    @Test
    void isRequired_ReturnsFalseForOptionalParameter() {
        McpToolParameter parameter = McpToolParameter.builder()
                .withName("optional_test")
                .withDescription("Optional parameter")
                .withRequired(false)
                .build();
        
        assertFalse(parameter.isRequired());
    }
    
    @Test
    void defaultType_IsString() {
        McpToolParameter parameter = McpToolParameter.builder()
                .withName("default_type_test")
                .withDescription("Test default type")
                .withRequired(true)
                .build();
        
        assertEquals("string", parameter.getType());
    }
}
