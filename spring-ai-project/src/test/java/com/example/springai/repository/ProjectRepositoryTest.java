package com.example.springai.repository;

import com.example.springai.Application;
import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import com.example.springai.entity.Story;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void testSaveAndFindProject() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        
        Project savedProject = projectRepository.save(project);
        
        assertNotNull(savedProject.getId());
        assertEquals("Test Project", savedProject.getName());
        assertEquals("Test Description", savedProject.getDescription());
    }
    
    @Test
    public void testFindByName() {
        Project project1 = new Project();
        project1.setName("Project One");
        project1.setDescription("Description One");
        
        Project project2 = new Project();
        project2.setName("Project Two");
        project2.setDescription("Description Two");
        
        projectRepository.save(project1);
        projectRepository.save(project2);
        
        Optional<Project> foundProject = projectRepository.findByName("Project One");
        
        assertTrue(foundProject.isPresent());
        assertEquals("Project One", foundProject.get().getName());
        assertEquals("Description One", foundProject.get().getDescription());
    }
    
    @Test
    public void testFindByNameNotFound() {
        Optional<Project> foundProject = projectRepository.findByName("Non-existent Project");
        
        assertFalse(foundProject.isPresent());
    }
    
    @Test
    public void testProjectWithRequirements() {
        Project project = new Project();
        project.setName("Project With Requirements");
        project.setDescription("Project with requirements description");
        
        List<Requirement> requirements = new ArrayList<>();
        Requirement req1 = new Requirement();
        req1.setText("Requirement 1");
        req1.setProject(project);
        requirements.add(req1);
        
        Requirement req2 = new Requirement();
        req2.setText("Requirement 2");
        req2.setProject(project);
        requirements.add(req2);
        
        project.setRequirements(requirements);
        
        Project savedProject = projectRepository.save(project);
        Optional<Project> foundProject = projectRepository.findByName("Project With Requirements");
        
        assertTrue(foundProject.isPresent());
        assertEquals(2, foundProject.get().getRequirements().size());
        assertEquals("Requirement 1", foundProject.get().getRequirements().get(0).getText());
        assertEquals("Requirement 2", foundProject.get().getRequirements().get(1).getText());
    }
    
    @Test
    public void testProjectWithStories() {
        Project project = new Project();
        project.setName("Project With Stories");
        project.setDescription("Project with stories description");
        
        List<Story> stories = new ArrayList<>();
        Story story1 = new Story();
        story1.setTitle("Story 1");
        story1.setDescription("Story 1 description");
        story1.setProject(project);
        stories.add(story1);
        
        Story story2 = new Story();
        story2.setTitle("Story 2");
        story2.setDescription("Story 2 description");
        story2.setProject(project);
        stories.add(story2);
        
        project.setStories(stories);
        
        Project savedProject = projectRepository.save(project);
        Optional<Project> foundProject = projectRepository.findByName("Project With Stories");
        
        assertTrue(foundProject.isPresent());
        assertEquals(2, foundProject.get().getStories().size());
        assertEquals("Story 1", foundProject.get().getStories().get(0).getTitle());
        assertEquals("Story 2", foundProject.get().getStories().get(1).getTitle());
    }
    
    @Test
    public void testDeleteProject() {
        Project project = new Project();
        project.setName("Project To Delete");
        project.setDescription("This project will be deleted");
        
        Project savedProject = projectRepository.save(project);
        Long projectId = savedProject.getId();
        
        projectRepository.deleteById(projectId);
        
        Optional<Project> deletedProject = projectRepository.findById(projectId);
        assertFalse(deletedProject.isPresent());
    }
    
    @Test
    public void testFindAllProjects() {
        projectRepository.deleteAll();
        
        Project project1 = new Project();
        project1.setName("Project A");
        project1.setDescription("Description A");
        
        Project project2 = new Project();
        project2.setName("Project B");
        project2.setDescription("Description B");
        
        Project project3 = new Project();
        project3.setName("Project C");
        project3.setDescription("Description C");
        
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);
        
        List<Project> allProjects = projectRepository.findAll();
        
        assertEquals(3, allProjects.size());
        assertTrue(allProjects.stream().anyMatch(p -> p.getName().equals("Project A")));
        assertTrue(allProjects.stream().anyMatch(p -> p.getName().equals("Project B")));
        assertTrue(allProjects.stream().anyMatch(p -> p.getName().equals("Project C")));
    }
}
