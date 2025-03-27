package com.example.springai.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NFRTest {

    @Test
    public void testNFRConstructorAndGetters() {
        NFR nfr = new NFR();
        nfr.setId(1L);
        nfr.setCategory("Performance");
        nfr.setDescription("System must respond within 2 seconds");
        
        assertEquals(1L, nfr.getId());
        assertEquals("Performance", nfr.getCategory());
        assertEquals("System must respond within 2 seconds", nfr.getDescription());
    }
    
    @Test
    public void testNFRSetters() {
        NFR nfr = new NFR();
        
        nfr.setId(2L);
        assertEquals(2L, nfr.getId());
        
        nfr.setCategory("Security");
        assertEquals("Security", nfr.getCategory());
        
        nfr.setDescription("All data must be encrypted");
        assertEquals("All data must be encrypted", nfr.getDescription());
    }
    
    @Test
    public void testNFRProjectRelationship() {
        NFR nfr = new NFR();
        Project project = new Project();
        project.setId(3L);
        project.setName("Test Project");
        
        nfr.setProject(project);
        
        assertNotNull(nfr.getProject());
        assertEquals(3L, nfr.getProject().getId());
        assertEquals("Test Project", nfr.getProject().getName());
    }
    
    @Test
    public void testNFREqualsAndHashCode() {
        NFR nfr1 = new NFR();
        nfr1.setId(1L);
        nfr1.setCategory("Performance");
        nfr1.setDescription("System must respond within 2 seconds");
        
        NFR nfr2 = new NFR();
        nfr2.setId(1L);
        nfr2.setCategory("Different Category");
        nfr2.setDescription("Different Description");
        
        NFR nfr3 = new NFR();
        nfr3.setId(2L);
        nfr3.setCategory("Performance");
        nfr3.setDescription("System must respond within 2 seconds");
        
        assertEquals(nfr1, nfr2);
        assertNotEquals(nfr1, nfr3);
        
        assertEquals(nfr1.hashCode(), nfr2.hashCode());
        assertNotEquals(nfr1.hashCode(), nfr3.hashCode());
    }
    
    @Test
    public void testNFRToString() {
        NFR nfr = new NFR();
        nfr.setId(1L);
        nfr.setCategory("Performance");
        nfr.setDescription("System must respond within 2 seconds");
        
        String toString = nfr.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Performance"));
        assertTrue(toString.contains("System must respond within 2 seconds"));
    }
}
