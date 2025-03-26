package com.example.springai.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
