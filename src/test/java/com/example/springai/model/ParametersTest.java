package com.example.springai.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParametersTest {

    @Test
    public void testParameters() {
        Parameters parameters = new Parameters();
        
        parameters.setProjectName("Test Project");
        parameters.setDescription("Test Description");
        
        assertEquals("Test Project", parameters.getProjectName());
        assertEquals("Test Description", parameters.getDescription());
    }
}
