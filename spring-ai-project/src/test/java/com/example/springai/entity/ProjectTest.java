package com.example.springai.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {

    @Test
    public void testProjectGettersAndSetters() {
        Project project = new Project();
        
        Long id = 1L;
        String name = "Test Project";
        String description = "Test Description";
        
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        
        assertEquals(id, project.getId());
        assertEquals(name, project.getName());
        assertEquals(description, project.getDescription());
    }
    
    @Test
    public void testProjectRelationships() {
        Project project = new Project();
        project.setName("Test Project");
        
        // Test requirements
        List<Requirement> requirements = new ArrayList<>();
        Requirement req = new Requirement();
        req.setText("Test Requirement");
        req.setProject(project);
        requirements.add(req);
        project.setRequirements(requirements);
        
        // Test stories
        List<Story> stories = new ArrayList<>();
        Story story = new Story();
        story.setTitle("Test Story");
        story.setDescription("Test Story Description");
        story.setProject(project);
        stories.add(story);
        project.setStories(stories);
        
        // Test risks
        List<Risk> risks = new ArrayList<>();
        Risk risk = new Risk();
        risk.setDescription("Test Risk");
        risk.setProject(project);
        risks.add(risk);
        project.setRisks(risks);
        
        // Test NFRs
        List<NFR> nfrs = new ArrayList<>();
        NFR nfr = new NFR();
        nfr.setCategory("Performance");
        nfr.setDescription("Test NFR");
        nfr.setProject(project);
        nfrs.add(nfr);
        project.setNfrs(nfrs);
        
        // Test queries
        List<Query> queries = new ArrayList<>();
        Query query = new Query();
        query.setQuestion("Test Query");
        query.setProject(project);
        queries.add(query);
        project.setQueries(queries);
        
        assertNotNull(project.getRequirements());
        assertNotNull(project.getStories());
        assertNotNull(project.getRisks());
        assertNotNull(project.getNfrs());
        assertNotNull(project.getQueries());
        
        assertEquals(1, project.getRequirements().size());
        assertEquals(1, project.getStories().size());
        assertEquals(1, project.getRisks().size());
        assertEquals(1, project.getNfrs().size());
        assertEquals(1, project.getQueries().size());
    }
    
    @Test
    public void testProjectEqualsAndHashCode() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Test Project");
        project1.setDescription("Test Description");
        
        Project project2 = new Project();
        project2.setId(1L);
        project2.setName("Different Name");
        project2.setDescription("Different Description");
        
        Project project3 = new Project();
        project3.setId(2L);
        project3.setName("Test Project");
        project3.setDescription("Test Description");
        
        assertEquals(project1, project1); // Same object
        assertNotEquals(project1, null); // Null comparison
        assertNotEquals(project1, new Object()); // Different class
        assertEquals(project1, project2); // Same ID, different fields
        assertNotEquals(project1, project3); // Different ID
        
        assertEquals(project1.hashCode(), project2.hashCode()); // Same ID should have same hashCode
        assertNotEquals(project1.hashCode(), project3.hashCode()); // Different ID should have different hashCode
    }
    
    @Test
    public void testProjectToString() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        
        String toString = project.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Test Project"));
        assertTrue(toString.contains("Test Description"));
    }
}
