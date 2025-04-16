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
        Long id1 = 1L;
        Long id2 = 2L;

        Requirement requirement1 = new Requirement();
        requirement1.setId(id1);
        requirement1.setText("Test Requirement");

        Requirement requirement1Copy = new Requirement(); // Same ID, different instance
        requirement1Copy.setId(id1);
        requirement1Copy.setText("Different Text");

        Requirement requirement2 = new Requirement(); // Different ID
        requirement2.setId(id2);
        requirement2.setText("Test Requirement");

        Requirement requirementNullId1 = new Requirement(); // Null ID
        Requirement requirementNullId2 = new Requirement(); // Null ID

        assertTrue(requirement1.equals(requirement1), "Equals: Same instance");
        assertTrue(requirement1.equals(requirement1Copy), "Equals: Same ID, different instance");
        assertTrue(requirement1Copy.equals(requirement1), "Equals: Symmetric");
        assertFalse(requirement1.equals(requirement2), "Equals: Different ID");
        assertFalse(requirement2.equals(requirement1), "Equals: Different ID symmetric");
        assertFalse(requirement1.equals(null), "Equals: Null object");
        assertFalse(requirement1.equals(new Object()), "Equals: Different type");
        assertFalse(requirement1.equals(requirementNullId1), "Equals: One null ID");
        assertFalse(requirementNullId1.equals(requirement1), "Equals: One null ID symmetric");
        assertTrue(requirementNullId1.equals(requirementNullId2), "Equals: Both null IDs");

        assertEquals(requirement1.hashCode(), requirement1Copy.hashCode(), "HashCode: Same ID");
        assertNotEquals(requirement1.hashCode(), requirement2.hashCode(), "HashCode: Different ID");
        assertNotEquals(requirement1.hashCode(), requirementNullId1.hashCode(), "HashCode: One null ID");
        assertEquals(requirementNullId1.hashCode(), requirementNullId2.hashCode(), "HashCode: Both null IDs");
    }

    @Test
    public void testToString() {
        Requirement requirement = new Requirement();
        requirement.setId(1L);
        requirement.setText("Test Requirement");

        String expected = "Requirement{id=1, text='Test Requirement'}";
        assertEquals(expected, requirement.toString());

        Requirement requirementNullFields = new Requirement();
        requirementNullFields.setId(2L);
        String expectedNull = "Requirement{id=2, text='null'}";
        assertEquals(expectedNull, requirementNullFields.toString());
    }
}
