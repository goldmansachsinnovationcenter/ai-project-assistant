package com.example.springai.controller;

import com.example.springai.entity.*;
import com.example.springai.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatabaseVerificationController.class)
public class DatabaseVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectRepository projectRepository;
    
    @MockBean
    private RequirementRepository requirementRepository;
    
    @MockBean
    private StoryRepository storyRepository;
    
    @MockBean
    private RiskRepository riskRepository;
    
    @MockBean
    private QueryRepository queryRepository;
    
    @MockBean
    private NFRRepository nfrRepository;
    
    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @Test
    public void testGetAllProjects() throws Exception {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");
        
        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");
        
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project1, project2));

        mockMvc.perform(get("/api/db/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.projects[0].name").value("Project 1"))
                .andExpect(jsonPath("$.projects[1].name").value("Project 2"));
    }

    @Test
    public void testGetProjectByName() throws Exception {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");

        when(projectRepository.findByName(anyString())).thenReturn(Optional.of(project));

        mockMvc.perform(get("/api/db/projects/Test Project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    public void testGetProjectByNameNotFound() throws Exception {
        when(projectRepository.findByName(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/db/projects/NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRequirements() throws Exception {
        Requirement req1 = new Requirement();
        req1.setId(1L);
        req1.setText("Requirement 1");
        
        Requirement req2 = new Requirement();
        req2.setId(2L);
        req2.setText("Requirement 2");
        
        when(requirementRepository.findAll()).thenReturn(Arrays.asList(req1, req2));

        mockMvc.perform(get("/api/db/requirements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.requirements[0].text").value("Requirement 1"))
                .andExpect(jsonPath("$.requirements[1].text").value("Requirement 2"));
    }

    @Test
    public void testGetAllStories() throws Exception {
        Story story1 = new Story();
        story1.setId(1L);
        story1.setTitle("Story 1");
        
        Story story2 = new Story();
        story2.setId(2L);
        story2.setTitle("Story 2");
        
        when(storyRepository.findAll()).thenReturn(Arrays.asList(story1, story2));

        mockMvc.perform(get("/api/db/stories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.stories[0].title").value("Story 1"))
                .andExpect(jsonPath("$.stories[1].title").value("Story 2"));
    }

    @Test
    public void testGetAllRisks() throws Exception {
        Risk risk1 = new Risk();
        risk1.setId(1L);
        risk1.setDescription("Risk 1");
        
        Risk risk2 = new Risk();
        risk2.setId(2L);
        risk2.setDescription("Risk 2");
        
        when(riskRepository.findAll()).thenReturn(Arrays.asList(risk1, risk2));

        mockMvc.perform(get("/api/db/risks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.risks[0].description").value("Risk 1"))
                .andExpect(jsonPath("$.risks[1].description").value("Risk 2"));
    }

    @Test
    public void testGetAllQueries() throws Exception {
        Query query1 = new Query();
        query1.setId(1L);
        query1.setQuestion("Query 1");
        
        Query query2 = new Query();
        query2.setId(2L);
        query2.setQuestion("Query 2");
        
        when(queryRepository.findAll()).thenReturn(Arrays.asList(query1, query2));

        mockMvc.perform(get("/api/db/queries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.queries[0].question").value("Query 1"))
                .andExpect(jsonPath("$.queries[1].question").value("Query 2"));
    }

    @Test
    public void testGetAllNFRs() throws Exception {
        NFR nfr1 = new NFR();
        nfr1.setId(1L);
        nfr1.setCategory("Performance");
        
        NFR nfr2 = new NFR();
        nfr2.setId(2L);
        nfr2.setCategory("Security");
        
        when(nfrRepository.findAll()).thenReturn(Arrays.asList(nfr1, nfr2));

        mockMvc.perform(get("/api/db/nfrs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.nfrs[0].category").value("Performance"))
                .andExpect(jsonPath("$.nfrs[1].category").value("Security"));
    }

    @Test
    public void testGetAllChatMessages() throws Exception {
        ChatMessage msg1 = new ChatMessage("Hello", "Hi there");
        msg1.setId(1L);
        
        ChatMessage msg2 = new ChatMessage("How are you?", "I'm good");
        msg2.setId(2L);
        
        when(chatMessageRepository.findAll()).thenReturn(Arrays.asList(msg1, msg2));

        mockMvc.perform(get("/api/db/chat-messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.messages[0].prompt").value("Hello"))
                .andExpect(jsonPath("$.messages[1].prompt").value("How are you?"));
    }

    @Test
    public void testGetDatabaseStatus() throws Exception {
        when(projectRepository.count()).thenReturn(2L);
        when(requirementRepository.count()).thenReturn(5L);
        when(storyRepository.count()).thenReturn(10L);
        when(riskRepository.count()).thenReturn(3L);
        when(queryRepository.count()).thenReturn(4L);
        when(nfrRepository.count()).thenReturn(6L);
        when(chatMessageRepository.count()).thenReturn(15L);

        mockMvc.perform(get("/api/db/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").value(2))
                .andExpect(jsonPath("$.requirements").value(5))
                .andExpect(jsonPath("$.stories").value(10))
                .andExpect(jsonPath("$.risks").value(3))
                .andExpect(jsonPath("$.queries").value(4))
                .andExpect(jsonPath("$.nfrs").value(6))
                .andExpect(jsonPath("$.chatMessages").value(15));
    }
}
