package com.example.springai.repository;

import com.example.springai.entity.Project;
import com.example.springai.entity.Risk;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RiskRepositoryTest {

    @Autowired
    private RiskRepository riskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Test
    public void testSaveRisk() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        projectRepository.save(project);
        
        Risk risk = new Risk();
        risk.setDescription("Security vulnerability");
        risk.setMitigation("Implement encryption");
        risk.setProject(project);
        
        Risk savedRisk = riskRepository.save(risk);
        
        assertNotNull(savedRisk.getId());
        assertEquals("Security vulnerability", savedRisk.getDescription());
        assertEquals("Implement encryption", savedRisk.getMitigation());
        assertEquals(project.getId(), savedRisk.getProject().getId());
    }
    
    @Test
    public void testFindById() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Risk risk = new Risk();
        risk.setDescription("Security vulnerability");
        risk.setMitigation("Implement encryption");
        risk.setProject(project);
        riskRepository.save(risk);
        
        Optional<Risk> foundRisk = riskRepository.findById(risk.getId());
        
        assertTrue(foundRisk.isPresent());
        assertEquals("Security vulnerability", foundRisk.get().getDescription());
        assertEquals("Implement encryption", foundRisk.get().getMitigation());
    }
    
    @Test
    public void testFindAll() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Risk risk1 = new Risk();
        risk1.setDescription("Security vulnerability");
        risk1.setMitigation("Implement encryption");
        risk1.setProject(project);
        
        Risk risk2 = new Risk();
        risk2.setDescription("Performance bottleneck");
        risk2.setMitigation("Optimize database queries");
        risk2.setProject(project);
        
        riskRepository.save(risk1);
        riskRepository.save(risk2);
        
        List<Risk> risks = riskRepository.findAll();
        
        assertFalse(risks.isEmpty());
        assertTrue(risks.size() >= 2);
    }
    
    @Test
    public void testDeleteRisk() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Risk risk = new Risk();
        risk.setDescription("Security vulnerability");
        risk.setMitigation("Implement encryption");
        risk.setProject(project);
        riskRepository.save(risk);
        
        riskRepository.delete(risk);
        
        Optional<Risk> deletedRisk = riskRepository.findById(risk.getId());
        assertFalse(deletedRisk.isPresent());
    }
}
