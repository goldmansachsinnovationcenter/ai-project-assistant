package com.example.springai.entity;

import com.github.ksuid.Ksuid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequirementTest {

    @Test
    public void testRequirementCreation() {
        String text = "Test Requirement Text";
        
        Requirement requirement = new Requirement();
        requirement.setText(text);
        
        assertEquals(text, requirement.getText());
    }
    
    @Test
    public void testRequirementProjectAssociation() {
        Project project = new Project();
        project.setId(Ksuid.newKsuid().toString());
        project.setName("Test Project");
        
        Requirement requirement = new Requirement();
        requirement.setText("Test Requirement");
        
        requirement.setProject(project);
        
        assertEquals(project, requirement.getProject());
        assertEquals("Test Project", requirement.getProject().getName());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Long id = 1L;
        
        Requirement requirement1 = new Requirement();
        requirement1.setId(id);
        requirement1.setText("Test Requirement");
        
        Requirement requirement2 = new Requirement();
        requirement2.setId(id);
        requirement2.setText("Different Text");
        
        Requirement requirement3 = new Requirement();
        requirement3.setId(2L);
        requirement3.setText("Test Requirement");
        
        assertEquals(requirement1, requirement2, "Requirements with same ID should be equal");
        assertNotEquals(requirement1, requirement3, "Requirements with different IDs should not be equal");
        assertEquals(requirement1.hashCode(), requirement2.hashCode(), "Hash codes should be equal for equal requirements");
        assertNotEquals(requirement1.hashCode(), requirement3.hashCode(), "Hash codes should differ for different requirements");
    }
    
    @Test
    public void testToString() {
        Requirement requirement = new Requirement();
        requirement.setId(1L);
        requirement.setText("Test Requirement");
        
        String toString = requirement.toString();
        
        assertTrue(toString.contains("Test Requirement"), "toString should contain the requirement text");
    }
}
