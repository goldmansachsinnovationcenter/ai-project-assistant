package com.example.springai.entity;

import com.github.ksuid.Ksuid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NFRTest {

    @Test
    public void testNFRCreation() {
        String category = "Performance";
        String description = "Test NFR Description";
        
        NFR nfr = new NFR();
        nfr.setCategory(category);
        nfr.setDescription(description);
        
        assertEquals(category, nfr.getCategory());
        assertEquals(description, nfr.getDescription());
    }
    
    @Test
    public void testNFRProjectAssociation() {
        Project project = new Project();
        project.setId(Ksuid.newKsuid().toString());
        project.setName("Test Project");
        
        NFR nfr = new NFR();
        nfr.setCategory("Security");
        nfr.setDescription("Test NFR Description");
        
        nfr.setProject(project);
        
        assertEquals(project, nfr.getProject());
        assertEquals("Test Project", nfr.getProject().getName());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Long id = 1L;
        
        NFR nfr1 = new NFR();
        nfr1.setId(id);
        nfr1.setCategory("Performance");
        nfr1.setDescription("Test NFR Description");
        
        NFR nfr2 = new NFR();
        nfr2.setId(id);
        nfr2.setCategory("Security");
        nfr2.setDescription("Different Description");
        
        NFR nfr3 = new NFR();
        nfr3.setId(2L);
        nfr3.setCategory("Performance");
        nfr3.setDescription("Test NFR Description");
        
        assertEquals(nfr1, nfr2, "NFRs with same ID should be equal");
        assertNotEquals(nfr1, nfr3, "NFRs with different IDs should not be equal");
        assertEquals(nfr1.hashCode(), nfr2.hashCode(), "Hash codes should be equal for equal NFRs");
        assertNotEquals(nfr1.hashCode(), nfr3.hashCode(), "Hash codes should differ for different NFRs");
    }
    
    @Test
    public void testToString() {
        NFR nfr = new NFR();
        nfr.setId(1L);
        nfr.setCategory("Performance");
        nfr.setDescription("Test NFR Description");
        
        String toString = nfr.toString();
        
        assertTrue(toString.contains("Performance"), "toString should contain the NFR category");
        assertTrue(toString.contains("Test NFR Description"), "toString should contain the NFR description");
    }
}
