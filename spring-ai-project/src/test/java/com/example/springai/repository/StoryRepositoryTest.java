package com.example.springai.repository;

import com.example.springai.entity.Project;
import com.example.springai.entity.Story;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class StoryRepositoryTest {

    @Autowired
    private StoryRepository storyRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Test
    public void testSaveStory() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        projectRepository.save(project);
        
        Story story = new Story();
        story.setTitle("Login Feature");
        story.setDescription("As a user, I want to log in to access my account");
        story.setProject(project);
        
        Story savedStory = storyRepository.save(story);
        
        assertNotNull(savedStory.getId());
        assertEquals("Login Feature", savedStory.getTitle());
        assertEquals("As a user, I want to log in to access my account", savedStory.getDescription());
        assertEquals(project.getId(), savedStory.getProject().getId());
    }
    
    @Test
    public void testFindById() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Story story = new Story();
        story.setTitle("Login Feature");
        story.setDescription("As a user, I want to log in to access my account");
        story.setProject(project);
        storyRepository.save(story);
        
        Optional<Story> foundStory = storyRepository.findById(story.getId());
        
        assertTrue(foundStory.isPresent());
        assertEquals("Login Feature", foundStory.get().getTitle());
        assertEquals("As a user, I want to log in to access my account", foundStory.get().getDescription());
    }
    
    @Test
    public void testFindAll() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Story story1 = new Story();
        story1.setTitle("Login Feature");
        story1.setDescription("As a user, I want to log in to access my account");
        story1.setProject(project);
        
        Story story2 = new Story();
        story2.setTitle("Password Reset");
        story2.setDescription("As a user, I want to reset my password if I forget it");
        story2.setProject(project);
        
        storyRepository.save(story1);
        storyRepository.save(story2);
        
        List<Story> stories = storyRepository.findAll();
        
        assertFalse(stories.isEmpty());
        assertTrue(stories.size() >= 2);
    }
    
    @Test
    public void testDeleteStory() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Story story = new Story();
        story.setTitle("Login Feature");
        story.setDescription("As a user, I want to log in to access my account");
        story.setProject(project);
        storyRepository.save(story);
        
        storyRepository.delete(story);
        
        Optional<Story> deletedStory = storyRepository.findById(story.getId());
        assertFalse(deletedStory.isPresent());
    }
}
