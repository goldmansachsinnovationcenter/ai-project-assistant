package com.example.springai.entity;

import com.github.ksuid.Ksuid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoryTest {

    @Test
    public void testStoryCreation() {
        String title = "Test Story Title";
        String description = "Test Story Description";
        
        Story story = new Story();
        story.setTitle(title);
        story.setDescription(description);
        
        assertEquals(title, story.getTitle());
        assertEquals(description, story.getDescription());
    }
    
    @Test
    public void testStoryProjectAssociation() {
        Project project = new Project();
        project.setId(Ksuid.newKsuid().toString());
        project.setName("Test Project");
        
        Story story = new Story();
        story.setTitle("Test Story");
        story.setDescription("Test Description");
        
        story.setProject(project);
        
        assertEquals(project, story.getProject());
        assertEquals("Test Project", story.getProject().getName());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Long id = 1L;
        
        Story story1 = new Story();
        story1.setId(id);
        story1.setTitle("Test Story");
        story1.setDescription("Test Description");
        
        Story story2 = new Story();
        story2.setId(id);
        story2.setTitle("Different Title");
        story2.setDescription("Different Description");
        
        Story story3 = new Story();
        story3.setId(2L);
        story3.setTitle("Test Story");
        story3.setDescription("Test Description");
        
        assertEquals(story1, story2, "Stories with same ID should be equal");
        assertNotEquals(story1, story3, "Stories with different IDs should not be equal");
        assertEquals(story1.hashCode(), story2.hashCode(), "Hash codes should be equal for equal stories");
        assertNotEquals(story1.hashCode(), story3.hashCode(), "Hash codes should differ for different stories");
    }
    
    @Test
    public void testToString() {
        Story story = new Story();
        story.setId(1L);
        story.setTitle("Test Story");
        story.setDescription("Test Description");
        
        String toString = story.toString();
        
        assertTrue(toString.contains("Test Story"), "toString should contain the story title");
        assertTrue(toString.contains("Test Description"), "toString should contain the story description");
    }
}
