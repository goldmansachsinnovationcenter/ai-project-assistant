package com.example.springai.entity;

import com.github.ksuid.Ksuid;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {

    @Test
    public void testProjectCreation() {
        String id = Ksuid.newKsuid().toString();
        String name = "Test Project";
        String description = "Test Description";

        Project project = new Project();
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
        project.setId(Ksuid.newKsuid().toString());
        project.setName("Test Project");
        project.setDescription("Test Description");

        Requirement requirement = new Requirement();
        requirement.setText("Test Requirement");
        requirement.setProject(project);

        Story story = new Story();
        story.setTitle("Test Story");
        story.setDescription("Test Story Description");
        story.setProject(project);

        Risk risk = new Risk();
        risk.setDescription("Test Risk");
        risk.setMitigation("Test Mitigation");
        risk.setProject(project);

        NFR nfr = new NFR();
        nfr.setCategory("Test Category");
        nfr.setDescription("Test NFR Description");
        nfr.setProject(project);

        Query query = new Query();
        query.setQuestion("Test Question");
        query.setContext("Test Context");
        query.setProject(project);

        List<Requirement> requirements = new ArrayList<>();
        requirements.add(requirement);
        project.setRequirements(requirements);

        List<Story> stories = new ArrayList<>();
        stories.add(story);
        project.setStories(stories);

        List<Risk> risks = new ArrayList<>();
        risks.add(risk);
        project.setRisks(risks);

        List<NFR> nfrs = new ArrayList<>();
        nfrs.add(nfr);
        project.setNfrs(nfrs);

        List<Query> queries = new ArrayList<>();
        queries.add(query);
        project.setQueries(queries);

        assertEquals(1, project.getRequirements().size());
        assertEquals("Test Requirement", project.getRequirements().get(0).getText());
        
        assertEquals(1, project.getStories().size());
        assertEquals("Test Story", project.getStories().get(0).getTitle());
        
        assertEquals(1, project.getRisks().size());
        assertEquals("Test Risk", project.getRisks().get(0).getDescription());
        
        assertEquals(1, project.getNfrs().size());
        assertEquals("Test Category", project.getNfrs().get(0).getCategory());
        
        assertEquals(1, project.getQueries().size());
        assertEquals("Test Question", project.getQueries().get(0).getQuestion());
    }

    @Test
    public void testEqualsAndHashCode() {
        String id = Ksuid.newKsuid().toString();
        
        Project project1 = new Project();
        project1.setId(id);
        project1.setName("Test Project");
        project1.setDescription("Test Description");
        
        Project project2 = new Project();
        project2.setId(id);
        project2.setName("Different Name");
        project2.setDescription("Different Description");
        
        Project project3 = new Project();
        project3.setId(Ksuid.newKsuid().toString());
        project3.setName("Test Project");
        project3.setDescription("Test Description");

        assertEquals(project1, project2, "Projects with same ID should be equal");
        assertNotEquals(project1, project3, "Projects with different IDs should not be equal");
        assertEquals(project1.hashCode(), project2.hashCode(), "Hash codes should be equal for equal projects");
        assertNotEquals(project1.hashCode(), project3.hashCode(), "Hash codes should differ for different projects");
    }
}
