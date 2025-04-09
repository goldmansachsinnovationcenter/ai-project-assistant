package com.example.springai.config;

import com.example.springai.entity.Project;
import com.example.springai.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    private NFRRepository nfrRepository;

    @Mock
    private QueryRepository queryRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private TestDataInitializer testDataInitializer;

    @Test
    public void testInitTestData() {
        when(projectRepository.count()).thenReturn(0L);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = testDataInitializer.initTestData(
                projectRepository, requirementRepository, storyRepository,
                riskRepository, queryRepository, nfrRepository);
        
        assertNotNull(runner);
        
        assertDoesNotThrow(() -> runner.run("test"));

        verify(projectRepository, times(5)).save(any(Project.class));
        verify(requirementRepository, atLeast(5)).save(any());
        verify(storyRepository, atLeast(5)).save(any());
        verify(riskRepository, atLeast(5)).save(any());
        verify(nfrRepository, atLeast(5)).save(any());
        verify(queryRepository, atLeast(5)).save(any());
    }

    @Test
    public void testInitTestDataWhenDataExists() {
        when(projectRepository.count()).thenReturn(5L);

        CommandLineRunner runner = testDataInitializer.initTestData(
                projectRepository, requirementRepository, storyRepository,
                riskRepository, queryRepository, nfrRepository);
        
        assertNotNull(runner);
        
        assertDoesNotThrow(() -> runner.run("test"));

        verify(projectRepository, never()).save(any());
        verify(requirementRepository, never()).save(any());
        verify(storyRepository, never()).save(any());
        verify(riskRepository, never()).save(any());
        verify(nfrRepository, never()).save(any());
        verify(queryRepository, never()).save(any());
    }
}
