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
        Long id1 = 1L;
        Long id2 = 2L;

        Risk risk1 = new Risk();
        risk1.setId(id1);
        risk1.setDescription("Test Risk");

        Risk risk1Copy = new Risk(); // Same ID, different instance
        risk1Copy.setId(id1);
        risk1Copy.setDescription("Different Description");

        Risk risk2 = new Risk(); // Different ID
        risk2.setId(id2);
        risk2.setDescription("Test Risk");

        Risk riskNullId1 = new Risk(); // Null ID
        Risk riskNullId2 = new Risk(); // Null ID

        assertTrue(risk1.equals(risk1), "Equals: Same instance");
        assertTrue(risk1.equals(risk1Copy), "Equals: Same ID, different instance");
        assertTrue(risk1Copy.equals(risk1), "Equals: Symmetric");
        assertFalse(risk1.equals(risk2), "Equals: Different ID");
        assertFalse(risk2.equals(risk1), "Equals: Different ID symmetric");
        assertFalse(risk1.equals(null), "Equals: Null object");
        assertFalse(risk1.equals(new Object()), "Equals: Different type");
        assertFalse(risk1.equals(riskNullId1), "Equals: One null ID");
        assertFalse(riskNullId1.equals(risk1), "Equals: One null ID symmetric");
        assertTrue(riskNullId1.equals(riskNullId2), "Equals: Both null IDs");

        assertEquals(risk1.hashCode(), risk1Copy.hashCode(), "HashCode: Same ID");
        assertNotEquals(risk1.hashCode(), risk2.hashCode(), "HashCode: Different ID");
        assertNotEquals(risk1.hashCode(), riskNullId1.hashCode(), "HashCode: One null ID");
        assertEquals(riskNullId1.hashCode(), riskNullId2.hashCode(), "HashCode: Both null IDs");
    }

    @Test
    public void testToString() {
        Risk risk = new Risk();
        risk.setId(1L);
        risk.setDescription("Test Risk");
        risk.setMitigation("Test Mitigation");

        String expected = "Risk{id=1, description='Test Risk', mitigation='Test Mitigation'}";
        assertEquals(expected, risk.toString());

        Risk riskNullFields = new Risk();
        riskNullFields.setId(2L);
        String expectedNull = "Risk{id=2, description='null', mitigation='null'}";
        assertEquals(expectedNull, riskNullFields.toString());
    }
}
