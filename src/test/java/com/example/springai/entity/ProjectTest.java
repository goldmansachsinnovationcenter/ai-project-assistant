package com.example.springai.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
