package com.example.springai.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequirementTest {

    @Test
    public void testRequirementConstructorAndGetters() {
        Requirement requirement = new Requirement();
        requirement.setId(1L);
        requirement.setText("User authentication");
        
        assertEquals(1L, requirement.getId());
        assertEquals("User authentication", requirement.getText());
    }
    
    @Test
    public void testRequirementSetters() {
        Requirement requirement = new Requirement();
        
        requirement.setId(2L);
        assertEquals(2L, requirement.getId());
        
        requirement.setText("Data encryption");
        assertEquals("Data encryption", requirement.getText());
    }
    
    @Test
    public void testRequirementProjectRelationship() {
        Requirement requirement = new Requirement();
        Project project = new Project();
        project.setId(3L);
        project.setName("Test Project");
        
        requirement.setProject(project);
        
        assertNotNull(requirement.getProject());
        assertEquals(3L, requirement.getProject().getId());
        assertEquals("Test Project", requirement.getProject().getName());
    }
    
    @Test
    public void testRequirementEqualsAndHashCode() {
        Requirement requirement1 = new Requirement();
        requirement1.setId(1L);
        requirement1.setText("User authentication");
        
        Requirement requirement2 = new Requirement();
        requirement2.setId(1L);
        requirement2.setText("Different text");
        
        Requirement requirement3 = new Requirement();
        requirement3.setId(2L);
        requirement3.setText("User authentication");
        
        assertEquals(requirement1, requirement1); // Same object
        assertNotEquals(requirement1, null); // Null comparison
        assertNotEquals(requirement1, new Object()); // Different class
        assertEquals(requirement1, requirement2); // Same ID, different fields
        assertNotEquals(requirement1, requirement3); // Different ID
        
        assertEquals(requirement1.hashCode(), requirement2.hashCode()); // Same ID should have same hashCode
        assertNotEquals(requirement1.hashCode(), requirement3.hashCode()); // Different ID should have different hashCode
    }
    
    @Test
    public void testRequirementToString() {
        Requirement requirement = new Requirement();
        requirement.setId(1L);
        requirement.setText("User authentication");
        
        String toString = requirement.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("User authentication"));
    }
}
