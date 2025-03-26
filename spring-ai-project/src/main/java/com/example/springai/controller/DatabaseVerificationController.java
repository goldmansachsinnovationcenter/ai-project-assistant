package com.example.springai.controller;

import com.example.springai.entity.*;
import com.example.springai.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/db")
public class DatabaseVerificationController {

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private RequirementRepository requirementRepository;
    
    @Autowired
    private StoryRepository storyRepository;
    
    @Autowired
    private RiskRepository riskRepository;
    
    @Autowired
    private QueryRepository queryRepository;
    
    @Autowired
    private NFRRepository nfrRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/projects")
    public ResponseEntity<?> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", projects.size());
        response.put("projects", projects);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/projects/{name}")
    public ResponseEntity<?> getProjectByName(@PathVariable String name) {
        Optional<Project> project = projectRepository.findByName(name);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/requirements")
    public ResponseEntity<?> getAllRequirements() {
        List<Requirement> requirements = requirementRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", requirements.size());
        response.put("requirements", requirements);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stories")
    public ResponseEntity<?> getAllStories() {
        List<Story> stories = storyRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", stories.size());
        response.put("stories", stories);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/risks")
    public ResponseEntity<?> getAllRisks() {
        List<Risk> risks = riskRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", risks.size());
        response.put("risks", risks);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/queries")
    public ResponseEntity<?> getAllQueries() {
        List<Query> queries = queryRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", queries.size());
        response.put("queries", queries);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/nfrs")
    public ResponseEntity<?> getAllNFRs() {
        List<NFR> nfrs = nfrRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", nfrs.size());
        response.put("nfrs", nfrs);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/chat-messages")
    public ResponseEntity<?> getAllChatMessages() {
        List<ChatMessage> messages = chatMessageRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", messages.size());
        response.put("messages", messages);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("projects", projectRepository.count());
        status.put("requirements", requirementRepository.count());
        status.put("stories", storyRepository.count());
        status.put("risks", riskRepository.count());
        status.put("queries", queryRepository.count());
        status.put("nfrs", nfrRepository.count());
        status.put("chatMessages", chatMessageRepository.count());
        
        return ResponseEntity.ok(status);
    }
}
