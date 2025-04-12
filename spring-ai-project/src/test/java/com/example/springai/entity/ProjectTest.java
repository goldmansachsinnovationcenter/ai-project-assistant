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
        String id1 = Ksuid.newKsuid().toString();
        String id2 = Ksuid.newKsuid().toString();

        Project project1 = new Project();
        project1.setId(id1);
        project1.setName("Test Project");
        project1.setDescription("Test Description");

        Project project1Copy = new Project(); // Same ID, different object instance
        project1Copy.setId(id1);
        project1Copy.setName("Different Name");

        Project project2 = new Project(); // Different ID
        project2.setId(id2);
        project2.setName("Test Project");

        Project projectNullId1 = new Project(); // Null ID
        Project projectNullId2 = new Project(); // Null ID

        assertTrue(project1.equals(project1), "Equals: Same instance");
        assertTrue(project1.equals(project1Copy), "Equals: Same ID, different instance");
        assertTrue(project1Copy.equals(project1), "Equals: Symmetric");
        assertFalse(project1.equals(project2), "Equals: Different ID");
        assertFalse(project2.equals(project1), "Equals: Different ID symmetric");
        assertFalse(project1.equals(null), "Equals: Null object");
        assertFalse(project1.equals(new Object()), "Equals: Different type");
        assertFalse(project1.equals(projectNullId1), "Equals: One null ID");
        assertFalse(projectNullId1.equals(project1), "Equals: One null ID symmetric");
        assertTrue(projectNullId1.equals(projectNullId2), "Equals: Both null IDs");

        assertEquals(project1.hashCode(), project1Copy.hashCode(), "HashCode: Same ID");
        assertNotEquals(project1.hashCode(), project2.hashCode(), "HashCode: Different ID");
        assertNotEquals(project1.hashCode(), projectNullId1.hashCode(), "HashCode: One null ID");
        assertEquals(projectNullId1.hashCode(), projectNullId2.hashCode(), "HashCode: Both null IDs");
    }

    @Test
    public void testToString() {
        Project project = new Project();
        project.setId("proj-tostring");
        project.setName("ToString Project");
        project.setDescription("ToString Desc");

        String expected = "Project{id=proj-tostring, name='ToString Project', description='ToString Desc'}";
        assertEquals(expected, project.toString());

        Project projectNullFields = new Project();
        projectNullFields.setId("proj-null");
        String expectedNull = "Project{id=proj-null, name='null', description='null'}";
        assertEquals(expectedNull, projectNullFields.toString());
    }
}
