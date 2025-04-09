package com.example.springai.entity;

import com.github.ksuid.Ksuid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RiskTest {

    @Test
    public void testRiskCreation() {
        String description = "Test Risk Description";
        String mitigation = "Test Risk Mitigation";
        
        Risk risk = new Risk();
        risk.setDescription(description);
        risk.setMitigation(mitigation);
        
        assertEquals(description, risk.getDescription());
        assertEquals(mitigation, risk.getMitigation());
    }
    
    @Test
    public void testRiskProjectAssociation() {
        Project project = new Project();
        project.setId(Ksuid.newKsuid().toString());
        project.setName("Test Project");
        
        Risk risk = new Risk();
        risk.setDescription("Test Risk");
        risk.setMitigation("Test Mitigation");
        
        risk.setProject(project);
        
        assertEquals(project, risk.getProject());
        assertEquals("Test Project", risk.getProject().getName());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Long id = 1L;
        
        Risk risk1 = new Risk();
        risk1.setId(id);
        risk1.setDescription("Test Risk");
        risk1.setMitigation("Test Mitigation");
        
        Risk risk2 = new Risk();
        risk2.setId(id);
        risk2.setDescription("Different Description");
        risk2.setMitigation("Different Mitigation");
        
        Risk risk3 = new Risk();
        risk3.setId(2L);
        risk3.setDescription("Test Risk");
        risk3.setMitigation("Test Mitigation");
        
        assertEquals(risk1, risk2, "Risks with same ID should be equal");
        assertNotEquals(risk1, risk3, "Risks with different IDs should not be equal");
        assertEquals(risk1.hashCode(), risk2.hashCode(), "Hash codes should be equal for equal risks");
        assertNotEquals(risk1.hashCode(), risk3.hashCode(), "Hash codes should differ for different risks");
    }
    
    @Test
    public void testToString() {
        Risk risk = new Risk();
        risk.setId(1L);
        risk.setDescription("Test Risk");
        risk.setMitigation("Test Mitigation");
        
        String toString = risk.toString();
        
        assertTrue(toString.contains("Test Risk"), "toString should contain the risk description");
        assertTrue(toString.contains("Test Mitigation"), "toString should contain the risk mitigation");
    }
}
