package com.example.springai.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTest {

    @Test
    public void testQueryConstructorAndGetters() {
        Query query = new Query();
        query.setId(1L);
        query.setQuestion("What is the project timeline?");
        query.setContext("Planning phase");
        
        assertEquals(1L, query.getId());
        assertEquals("What is the project timeline?", query.getQuestion());
        assertEquals("Planning phase", query.getContext());
    }
    
    @Test
    public void testQuerySetters() {
        Query query = new Query();
        
        query.setId(2L);
        assertEquals(2L, query.getId());
        
        query.setQuestion("What are the project risks?");
        assertEquals("What are the project risks?", query.getQuestion());
        
        query.setContext("Risk assessment");
        assertEquals("Risk assessment", query.getContext());
    }
    
    @Test
    public void testQueryProjectRelationship() {
        Query query = new Query();
        Project project = new Project();
        project.setId(3L);
        project.setName("Test Project");
        
        query.setProject(project);
        
        assertNotNull(query.getProject());
        assertEquals(3L, query.getProject().getId());
        assertEquals("Test Project", query.getProject().getName());
    }
    
    @Test
    public void testQueryEqualsAndHashCode() {
        Query query1 = new Query();
        query1.setId(1L);
        query1.setQuestion("What is the project timeline?");
        query1.setContext("Planning phase");
        
        Query query2 = new Query();
        query2.setId(1L);
        query2.setQuestion("Different question");
        query2.setContext("Different context");
        
        Query query3 = new Query();
        query3.setId(2L);
        query3.setQuestion("What is the project timeline?");
        query3.setContext("Planning phase");
        
        assertEquals(query1, query2);
        assertNotEquals(query1, query3);
        
        assertEquals(query1.hashCode(), query2.hashCode());
        assertNotEquals(query1.hashCode(), query3.hashCode());
    }
    
    @Test
    public void testQueryToString() {
        Query query = new Query();
        query.setId(1L);
        query.setQuestion("What is the project timeline?");
        query.setContext("Planning phase");
        
        String toString = query.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("What is the project timeline?"));
        assertTrue(toString.contains("Planning phase"));
    }
}
