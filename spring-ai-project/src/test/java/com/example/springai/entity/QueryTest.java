package com.example.springai.entity;

import com.github.ksuid.Ksuid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTest {

    @Test
    public void testQueryCreation() {
        String question = "Test Question";
        String context = "Test Context";
        
        Query query = new Query();
        query.setQuestion(question);
        query.setContext(context);
        
        assertEquals(question, query.getQuestion());
        assertEquals(context, query.getContext());
    }
    
    @Test
    public void testQueryProjectAssociation() {
        Project project = new Project();
        project.setId(Ksuid.newKsuid().toString());
        project.setName("Test Project");
        
        Query query = new Query();
        query.setQuestion("Test Question");
        query.setContext("Test Context");
        
        query.setProject(project);
        
        assertEquals(project, query.getProject());
        assertEquals("Test Project", query.getProject().getName());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        Long id1 = 1L;
        Long id2 = 2L;

        Query query1 = new Query();
        query1.setId(id1);
        query1.setQuestion("Test Question");

        Query query1Copy = new Query(); // Same ID, different instance
        query1Copy.setId(id1);
        query1Copy.setQuestion("Different Question");

        Query query2 = new Query(); // Different ID
        query2.setId(id2);
        query2.setQuestion("Test Question");

        Query queryNullId1 = new Query(); // Null ID
        Query queryNullId2 = new Query(); // Null ID

        assertTrue(query1.equals(query1), "Equals: Same instance");
        assertTrue(query1.equals(query1Copy), "Equals: Same ID, different instance");
        assertTrue(query1Copy.equals(query1), "Equals: Symmetric");
        assertFalse(query1.equals(query2), "Equals: Different ID");
        assertFalse(query2.equals(query1), "Equals: Different ID symmetric");
        assertFalse(query1.equals(null), "Equals: Null object");
        assertFalse(query1.equals(new Object()), "Equals: Different type");
        assertFalse(query1.equals(queryNullId1), "Equals: One null ID");
        assertFalse(queryNullId1.equals(query1), "Equals: One null ID symmetric");
        assertTrue(queryNullId1.equals(queryNullId2), "Equals: Both null IDs");

        assertEquals(query1.hashCode(), query1Copy.hashCode(), "HashCode: Same ID");
        assertNotEquals(query1.hashCode(), query2.hashCode(), "HashCode: Different ID");
        assertNotEquals(query1.hashCode(), queryNullId1.hashCode(), "HashCode: One null ID");
        assertEquals(queryNullId1.hashCode(), queryNullId2.hashCode(), "HashCode: Both null IDs");
    }

    @Test
    public void testToString() {
        Query query = new Query();
        query.setId(1L);
        query.setQuestion("Test Question");
        query.setContext("Test Context");

        String expected = "Query{id=1, question='Test Question', context='Test Context'}";
        assertEquals(expected, query.toString());

        Query queryNullFields = new Query();
        queryNullFields.setId(2L);
        String expectedNull = "Query{id=2, question='null', context='null'}";
        assertEquals(expectedNull, queryNullFields.toString());
    }
}
