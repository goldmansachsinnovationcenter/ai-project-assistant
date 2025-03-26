package com.example.springai.config;

import com.example.springai.entity.Project;
import com.example.springai.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TestDataInitializerTest {

    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private RequirementRepository requirementRepository;
    
    @Mock
    private StoryRepository storyRepository;
    
    @Mock
    private RiskRepository riskRepository;
    
    @Mock
    private QueryRepository queryRepository;
    
    @Mock
    private NFRRepository nfrRepository;

    @InjectMocks
    private TestDataInitializer testDataInitializer;

    @Test
    public void testInitTestData() throws Exception {
        // Setup
        when(projectRepository.count()).thenReturn(0L);
        when(projectRepository.save(any(Project.class))).thenReturn(new Project());

        // Execute
        CommandLineRunner runner = testDataInitializer.initTestData(
                projectRepository, 
                requirementRepository, 
                storyRepository, 
                riskRepository, 
                queryRepository, 
                nfrRepository);
        
        assertNotNull(runner);
        runner.run();

        // Verify
        verify(projectRepository, times(1)).count();
        verify(projectRepository, atLeastOnce()).save(any(Project.class));
    }

    @Test
    public void testInitTestDataWhenProjectsExist() throws Exception {
        // Setup
        when(projectRepository.count()).thenReturn(5L);

        // Execute
        CommandLineRunner runner = testDataInitializer.initTestData(
                projectRepository, 
                requirementRepository, 
                storyRepository, 
                riskRepository, 
                queryRepository, 
                nfrRepository);
        
        assertNotNull(runner);
        runner.run();

        // Verify
        verify(projectRepository, times(1)).count();
        verify(projectRepository, never()).save(any(Project.class));
    }
}
