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
        Long id = 1L;
        
        Query query1 = new Query();
        query1.setId(id);
        query1.setQuestion("Test Question");
        query1.setContext("Test Context");
        
        Query query2 = new Query();
        query2.setId(id);
        query2.setQuestion("Different Question");
        query2.setContext("Different Context");
        
        Query query3 = new Query();
        query3.setId(2L);
        query3.setQuestion("Test Question");
        query3.setContext("Test Context");
        
        assertEquals(query1, query2, "Queries with same ID should be equal");
        assertNotEquals(query1, query3, "Queries with different IDs should not be equal");
        assertEquals(query1.hashCode(), query2.hashCode(), "Hash codes should be equal for equal queries");
        assertNotEquals(query1.hashCode(), query3.hashCode(), "Hash codes should differ for different queries");
    }
    
    @Test
    public void testToString() {
        Query query = new Query();
        query.setId(1L);
        query.setQuestion("Test Question");
        query.setContext("Test Context");
        
        String toString = query.toString();
        
        assertTrue(toString.contains("Test Question"), "toString should contain the query question");
        assertTrue(toString.contains("Test Context"), "toString should contain the query context");
    }
}
