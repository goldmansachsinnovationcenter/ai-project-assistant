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
        Long id1 = 1L;
        Long id2 = 2L;

        NFR nfr1 = new NFR();
        nfr1.setId(id1);
        nfr1.setDescription("Test NFR Description");

        NFR nfr1Copy = new NFR(); // Same ID, different instance
        nfr1Copy.setId(id1);
        nfr1Copy.setDescription("Different Description");

        NFR nfr2 = new NFR(); // Different ID
        nfr2.setId(id2);
        nfr2.setDescription("Test NFR Description");

        NFR nfrNullId1 = new NFR(); // Null ID
        NFR nfrNullId2 = new NFR(); // Null ID

        assertTrue(nfr1.equals(nfr1), "Equals: Same instance");
        assertTrue(nfr1.equals(nfr1Copy), "Equals: Same ID, different instance");
        assertTrue(nfr1Copy.equals(nfr1), "Equals: Symmetric");
        assertFalse(nfr1.equals(nfr2), "Equals: Different ID");
        assertFalse(nfr2.equals(nfr1), "Equals: Different ID symmetric");
        assertFalse(nfr1.equals(null), "Equals: Null object");
        assertFalse(nfr1.equals(new Object()), "Equals: Different type");
        assertFalse(nfr1.equals(nfrNullId1), "Equals: One null ID");
        assertFalse(nfrNullId1.equals(nfr1), "Equals: One null ID symmetric");
        assertTrue(nfrNullId1.equals(nfrNullId2), "Equals: Both null IDs");

        assertEquals(nfr1.hashCode(), nfr1Copy.hashCode(), "HashCode: Same ID");
        assertNotEquals(nfr1.hashCode(), nfr2.hashCode(), "HashCode: Different ID");
        assertNotEquals(nfr1.hashCode(), nfrNullId1.hashCode(), "HashCode: One null ID");
        assertEquals(nfrNullId1.hashCode(), nfrNullId2.hashCode(), "HashCode: Both null IDs");
    }

    @Test
    public void testToString() {
        NFR nfr = new NFR();
        nfr.setId(1L);
        nfr.setCategory("Performance");
        nfr.setDescription("Test NFR Description");

        String expected = "NFR{id=1, category='Performance', description='Test NFR Description'}";
        assertEquals(expected, nfr.toString());

        NFR nfrNullFields = new NFR();
        nfrNullFields.setId(2L);
        String expectedNull = "NFR{id=2, category='null', description='null'}";
        assertEquals(expectedNull, nfrNullFields.toString());
    }
}
