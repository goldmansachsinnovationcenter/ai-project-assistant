package com.example.springai.repository;

import com.example.springai.entity.Project;
import com.example.springai.entity.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class QueryRepositoryTest {

    @Autowired
    private QueryRepository queryRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Test
    public void testSaveQuery() {
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        projectRepository.save(project);
        
        Query query = new Query();
        query.setQuestion("What is the project timeline?");
        query.setContext("Planning phase");
        query.setProject(project);
        
        Query savedQuery = queryRepository.save(query);
        
        assertNotNull(savedQuery.getId());
        assertEquals("What is the project timeline?", savedQuery.getQuestion());
        assertEquals("Planning phase", savedQuery.getContext());
        assertEquals(project.getId(), savedQuery.getProject().getId());
    }
    
    @Test
    public void testFindById() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Query query = new Query();
        query.setQuestion("What is the project timeline?");
        query.setContext("Planning phase");
        query.setProject(project);
        queryRepository.save(query);
        
        Optional<Query> foundQuery = queryRepository.findById(query.getId());
        
        assertTrue(foundQuery.isPresent());
        assertEquals("What is the project timeline?", foundQuery.get().getQuestion());
        assertEquals("Planning phase", foundQuery.get().getContext());
    }
    
    @Test
    public void testFindAll() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Query query1 = new Query();
        query1.setQuestion("What is the project timeline?");
        query1.setContext("Planning phase");
        query1.setProject(project);
        
        Query query2 = new Query();
        query2.setQuestion("What are the project risks?");
        query2.setContext("Risk assessment");
        query2.setProject(project);
        
        queryRepository.save(query1);
        queryRepository.save(query2);
        
        List<Query> queries = queryRepository.findAll();
        
        assertFalse(queries.isEmpty());
        assertTrue(queries.size() >= 2);
    }
    
    @Test
    public void testDeleteQuery() {
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        
        Query query = new Query();
        query.setQuestion("What is the project timeline?");
        query.setContext("Planning phase");
        query.setProject(project);
        queryRepository.save(query);
        
        queryRepository.delete(query);
        
        Optional<Query> deletedQuery = queryRepository.findById(query.getId());
        assertFalse(deletedQuery.isPresent());
    }
}
