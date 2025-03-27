package com.example.springai.repository;

import com.example.springai.entity.Project;
import com.example.springai.entity.Requirement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RequirementRepositoryTest {

    @Autowired
    private RequirementRepository requirementRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Test
    public void testSaveRequirement() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        projectRepository.save(project);
        
        Requirement requirement = new Requirement();
        requirement.setText("User authentication");
        requirement.setProject(project);
        
        Requirement savedRequirement = requirementRepository.save(requirement);
        
        assertNotNull(savedRequirement.getId());
        assertEquals("User authentication", savedRequirement.getText());
        assertEquals(project.getId(), savedRequirement.getProject().getId());
    }
    
    @Test
    public void testFindById() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Requirement requirement = new Requirement();
        requirement.setText("User authentication");
        requirement.setProject(project);
        requirementRepository.save(requirement);
        
        Optional<Requirement> foundRequirement = requirementRepository.findById(requirement.getId());
        
        assertTrue(foundRequirement.isPresent());
        assertEquals("User authentication", foundRequirement.get().getText());
    }
    
    @Test
    public void testFindAll() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Requirement requirement1 = new Requirement();
        requirement1.setText("User authentication");
        requirement1.setProject(project);
        
        Requirement requirement2 = new Requirement();
        requirement2.setText("Data encryption");
        requirement2.setProject(project);
        
        requirementRepository.save(requirement1);
        requirementRepository.save(requirement2);
        
        List<Requirement> requirements = requirementRepository.findAll();
        
        assertFalse(requirements.isEmpty());
        assertTrue(requirements.size() >= 2);
    }
    
    @Test
    public void testDeleteRequirement() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Requirement requirement = new Requirement();
        requirement.setText("User authentication");
        requirement.setProject(project);
        requirementRepository.save(requirement);
        
        requirementRepository.delete(requirement);
        
        Optional<Requirement> deletedRequirement = requirementRepository.findById(requirement.getId());
        assertFalse(deletedRequirement.isPresent());
    }
}
