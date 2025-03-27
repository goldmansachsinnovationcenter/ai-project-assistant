package com.example.springai.repository;

import com.example.springai.entity.NFR;
import com.example.springai.entity.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class NFRRepositoryTest {

    @Autowired
    private NFRRepository nfrRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Test
    public void testSaveNFR() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        projectRepository.save(project);
        
        NFR nfr = new NFR();
        nfr.setCategory("Security");
        nfr.setDescription("The system must encrypt all sensitive data");
        nfr.setProject(project);
        
        NFR savedNFR = nfrRepository.save(nfr);
        
        assertNotNull(savedNFR.getId());
        assertEquals("Security", savedNFR.getCategory());
        assertEquals("The system must encrypt all sensitive data", savedNFR.getDescription());
        assertEquals(project.getId(), savedNFR.getProject().getId());
    }
    
    @Test
    public void testFindById() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        NFR nfr = new NFR();
        nfr.setCategory("Performance");
        nfr.setDescription("The system must respond within 2 seconds");
        nfr.setProject(project);
        nfrRepository.save(nfr);
        
        Optional<NFR> foundNFR = nfrRepository.findById(nfr.getId());
        
        assertTrue(foundNFR.isPresent());
        assertEquals("Performance", foundNFR.get().getCategory());
        assertEquals("The system must respond within 2 seconds", foundNFR.get().getDescription());
    }
    
    @Test
    public void testFindAll() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        NFR nfr1 = new NFR();
        nfr1.setCategory("Security");
        nfr1.setDescription("The system must encrypt all sensitive data");
        nfr1.setProject(project);
        
        NFR nfr2 = new NFR();
        nfr2.setCategory("Performance");
        nfr2.setDescription("The system must respond within 2 seconds");
        nfr2.setProject(project);
        
        nfrRepository.save(nfr1);
        nfrRepository.save(nfr2);
        
        List<NFR> nfrs = nfrRepository.findAll();
        
        assertFalse(nfrs.isEmpty());
        assertTrue(nfrs.size() >= 2);
    }
    
    @Test
    public void testDeleteNFR() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        NFR nfr = new NFR();
        nfr.setCategory("Security");
        nfr.setDescription("The system must encrypt all sensitive data");
        nfr.setProject(project);
        nfrRepository.save(nfr);
        
        nfrRepository.delete(nfr);
        
        Optional<NFR> deletedNFR = nfrRepository.findById(nfr.getId());
        assertFalse(deletedNFR.isPresent());
    }
}
