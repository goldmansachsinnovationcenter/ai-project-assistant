package com.example.springai.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandExtractionTest {

    @Test
    void settersAndGetters_WorkCorrectly() {
        CommandExtraction extraction = new CommandExtraction();
        extraction.setCommand("create_project");
        Parameters params = new Parameters();
        params.setProjectName("Test Project");
        extraction.setParameters(params);
        
        assertEquals("create_project", extraction.getCommand());
        assertEquals("Test Project", extraction.getParameters().getProjectName());
    }

    @Test
    void getCommand_ReturnsCorrectValue() {
        CommandExtraction extraction = new CommandExtraction();
        extraction.setCommand("list_projects");
        
        assertEquals("list_projects", extraction.getCommand());
    }

    @Test
    void getParameters_ReturnsCorrectValue() {
        CommandExtraction extraction = new CommandExtraction();
        Parameters params = new Parameters();
        params.setProjectName("Test Project");
        params.setDescription("Test Description");
        extraction.setParameters(params);
        
        assertNotNull(extraction.getParameters());
        assertEquals("Test Project", extraction.getParameters().getProjectName());
        assertEquals("Test Description", extraction.getParameters().getDescription());
    }

    @Test
    void defaultConstructor_InitializesWithNulls() {
        CommandExtraction extraction = new CommandExtraction();
        
        assertNull(extraction.getCommand());
        assertNull(extraction.getParameters());
    }
}
