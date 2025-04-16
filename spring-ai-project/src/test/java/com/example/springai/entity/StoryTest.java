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
        Long id1 = 1L;
        Long id2 = 2L;

        Story story1 = new Story();
        story1.setId(id1);
        story1.setDescription("Test Description");

        Story story1Copy = new Story(); // Same ID, different instance
        story1Copy.setId(id1);
        story1Copy.setDescription("Different Description");

        Story story2 = new Story(); // Different ID
        story2.setId(id2);
        story2.setDescription("Test Description");

        Story storyNullId1 = new Story(); // Null ID
        Story storyNullId2 = new Story(); // Null ID

        assertTrue(story1.equals(story1), "Equals: Same instance");
        assertTrue(story1.equals(story1Copy), "Equals: Same ID, different instance");
        assertTrue(story1Copy.equals(story1), "Equals: Symmetric");
        assertFalse(story1.equals(story2), "Equals: Different ID");
        assertFalse(story2.equals(story1), "Equals: Different ID symmetric");
        assertFalse(story1.equals(null), "Equals: Null object");
        assertFalse(story1.equals(new Object()), "Equals: Different type");
        assertFalse(story1.equals(storyNullId1), "Equals: One null ID");
        assertFalse(storyNullId1.equals(story1), "Equals: One null ID symmetric");
        assertTrue(storyNullId1.equals(storyNullId2), "Equals: Both null IDs");

        assertEquals(story1.hashCode(), story1Copy.hashCode(), "HashCode: Same ID");
        assertNotEquals(story1.hashCode(), story2.hashCode(), "HashCode: Different ID");
        assertNotEquals(story1.hashCode(), storyNullId1.hashCode(), "HashCode: One null ID");
        assertEquals(storyNullId1.hashCode(), storyNullId2.hashCode(), "HashCode: Both null IDs");
    }

    @Test
    public void testToString() {
        Story story = new Story();
        story.setId(1L);
        story.setTitle("Test Story");
        story.setDescription("Test Description");

        String expected = "Story{id=1, title='Test Story', description='Test Description'}";
        assertEquals(expected, story.toString());

        Story storyNullFields = new Story();
        storyNullFields.setId(2L);
        String expectedNull = "Story{id=2, title='null', description='null'}";
        assertEquals(expectedNull, storyNullFields.toString());
    }
}
