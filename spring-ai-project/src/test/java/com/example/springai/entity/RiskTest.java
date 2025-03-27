package com.example.springai.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RiskTest {

    @Test
    public void testRiskConstructorAndGetters() {
        Risk risk = new Risk();
        risk.setId(1L);
        risk.setDescription("Security vulnerability");
        risk.setMitigation("Implement encryption");
        
        assertEquals(1L, risk.getId());
        assertEquals("Security vulnerability", risk.getDescription());
        assertEquals("Implement encryption", risk.getMitigation());
    }
    
    @Test
    public void testRiskSetters() {
        Risk risk = new Risk();
        
        risk.setId(2L);
        assertEquals(2L, risk.getId());
        
        risk.setDescription("Performance bottleneck");
        assertEquals("Performance bottleneck", risk.getDescription());
        
        risk.setMitigation("Optimize database queries");
        assertEquals("Optimize database queries", risk.getMitigation());
    }
    
    @Test
    public void testRiskProjectRelationship() {
        Risk risk = new Risk();
        Project project = new Project();
        project.setId(3L);
        project.setName("Test Project");
        
        risk.setProject(project);
        
        assertNotNull(risk.getProject());
        assertEquals(3L, risk.getProject().getId());
        assertEquals("Test Project", risk.getProject().getName());
    }
    
    @Test
    public void testRiskEqualsAndHashCode() {
        Risk risk1 = new Risk();
        risk1.setId(1L);
        risk1.setDescription("Security vulnerability");
        risk1.setMitigation("Implement encryption");
        
        Risk risk2 = new Risk();
        risk2.setId(1L);
        risk2.setDescription("Different description");
        risk2.setMitigation("Different mitigation");
        
        Risk risk3 = new Risk();
        risk3.setId(2L);
        risk3.setDescription("Security vulnerability");
        risk3.setMitigation("Implement encryption");
        
        assertEquals(risk1, risk2);
        assertNotEquals(risk1, risk3);
        
        assertEquals(risk1.hashCode(), risk2.hashCode());
        assertNotEquals(risk1.hashCode(), risk3.hashCode());
    }
    
    @Test
    public void testRiskToString() {
        Risk risk = new Risk();
        risk.setId(1L);
        risk.setDescription("Security vulnerability");
        risk.setMitigation("Implement encryption");
        
        String toString = risk.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Security vulnerability"));
        assertTrue(toString.contains("Implement encryption"));
    }
}
