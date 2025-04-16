package com.example.springai.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParametersTest {

    @Test
    void settersAndGetters_WorkCorrectly() {
        Parameters parameters = new Parameters();
        parameters.setProjectName("project_name");
        parameters.setDescription("description");
        
        assertEquals("project_name", parameters.getProjectName());
        assertEquals("description", parameters.getDescription());
    }

    @Test
    void getProjectName_ReturnsCorrectValue() {
        Parameters parameters = new Parameters();
        parameters.setProjectName("Test Project");
        
        assertEquals("Test Project", parameters.getProjectName());
    }

    @Test
    void getDescription_ReturnsCorrectValue() {
        Parameters parameters = new Parameters();
        parameters.setDescription("This is a test project");
        
        assertEquals("This is a test project", parameters.getDescription());
    }

    @Test
    void defaultConstructor_InitializesWithNulls() {
        Parameters parameters = new Parameters();
        
        assertNull(parameters.getProjectName());
        assertNull(parameters.getDescription());
    }
}
