package com.example.springai.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoryTest {

    @Test
    public void testStoryConstructorAndGetters() {
        Story story = new Story();
        story.setId(1L);
        story.setTitle("Login Feature");
        story.setDescription("As a user, I want to log in to access my account");
        
        assertEquals(1L, story.getId());
        assertEquals("Login Feature", story.getTitle());
        assertEquals("As a user, I want to log in to access my account", story.getDescription());
    }
    
    @Test
    public void testStorySetters() {
        Story story = new Story();
        
        story.setId(2L);
        assertEquals(2L, story.getId());
        
        story.setTitle("Password Reset");
        assertEquals("Password Reset", story.getTitle());
        
        story.setDescription("As a user, I want to reset my password if I forget it");
        assertEquals("As a user, I want to reset my password if I forget it", story.getDescription());
    }
    
    @Test
    public void testStoryProjectRelationship() {
        Story story = new Story();
        Project project = new Project();
        project.setId(3L);
        project.setName("Test Project");
        
        story.setProject(project);
        
        assertNotNull(story.getProject());
        assertEquals(3L, story.getProject().getId());
        assertEquals("Test Project", story.getProject().getName());
    }
    
    @Test
    public void testStoryEqualsAndHashCode() {
        Story story1 = new Story();
        story1.setId(1L);
        story1.setTitle("Login Feature");
        story1.setDescription("As a user, I want to log in to access my account");
        
        Story story2 = new Story();
        story2.setId(1L);
        story2.setTitle("Different Title");
        story2.setDescription("Different Description");
        
        Story story3 = new Story();
        story3.setId(2L);
        story3.setTitle("Login Feature");
        story3.setDescription("As a user, I want to log in to access my account");
        
        assertEquals(story1, story1); // Same object
        assertNotEquals(story1, null); // Null comparison
        assertNotEquals(story1, new Object()); // Different class
        assertEquals(story1, story2); // Same ID, different fields
        assertNotEquals(story1, story3); // Different ID
        
        assertEquals(story1.hashCode(), story2.hashCode()); // Same ID should have same hashCode
        assertNotEquals(story1.hashCode(), story3.hashCode()); // Different ID should have different hashCode
    }
    
    @Test
    public void testStoryToString() {
        Story story = new Story();
        story.setId(1L);
        story.setTitle("Login Feature");
        story.setDescription("As a user, I want to log in to access my account");
        
        String toString = story.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Login Feature"));
        assertTrue(toString.contains("As a user, I want to log in to access my account"));
    }
}
