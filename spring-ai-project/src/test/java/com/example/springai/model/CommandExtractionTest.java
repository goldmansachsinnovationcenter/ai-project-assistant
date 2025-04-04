package com.example.springai.model;

import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.server.tool.McpToolDefinition;
import org.springframework.ai.mcp.server.tool.McpToolParameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandExtractionTest {

    @Test
    public void testCommandExtraction() {
        CommandExtraction extraction = new CommandExtraction();
        Parameters parameters = new Parameters();
        
        parameters.setProjectName("Test Project");
        parameters.setDescription("Test Description");
        
        extraction.setCommand("create_project");
        extraction.setParameters(parameters);
        
        assertNotNull(extraction.getCommand());
        assertNotNull(extraction.getParameters());
        
        assertEquals("create_project", extraction.getCommand());
        assertEquals(parameters, extraction.getParameters());
        assertEquals("Test Project", extraction.getParameters().getProjectName());
        assertEquals("Test Description", extraction.getParameters().getDescription());
    }
    
    @Test
    public void testMcpToolDefinitionCreation() {
        String toolName = "create-project";
        String toolDescription = "Creates a new project";
        
        McpToolDefinition toolDefinition = McpToolDefinition.builder()
                .withName(toolName)
                .withDescription(toolDescription)
                .withParameter(McpToolParameter.builder()
                        .withName("name")
                        .withDescription("Project name")
                        .withRequired(true)
                        .withType("string")
                        .build())
                .withParameter(McpToolParameter.builder()
                        .withName("description")
                        .withDescription("Project description")
                        .withRequired(false)
                        .withType("string")
                        .build())
                .build();
        
        assertNotNull(toolDefinition);
        assertEquals(toolName, toolDefinition.getName());
        assertEquals(toolDescription, toolDefinition.getDescription());
        assertEquals(2, toolDefinition.getParameters().size());
        
        assertTrue(toolDefinition.getParameters().stream()
                .anyMatch(p -> p.getName().equals("name") && p.isRequired()));
        assertTrue(toolDefinition.getParameters().stream()
                .anyMatch(p -> p.getName().equals("description") && !p.isRequired()));
    }
}
