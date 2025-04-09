package com.example.springai.controller;

import com.example.springai.entity.*;
import com.example.springai.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/db")
@Tag(name = "Database Verification", description = "API for database operations and verification")
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

    @Operation(summary = "Get all projects", description = "Retrieve all projects from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/projects")
    public ResponseEntity<?> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", projects.size());
        response.put("projects", projects);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get project by name", description = "Retrieve a specific project by its name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project found", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/projects/{name}")
    public ResponseEntity<?> getProjectByName(
            @Parameter(description = "Name of the project to retrieve", required = true)
            @PathVariable String name) {
        Optional<Project> project = projectRepository.findByName(name);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Get all requirements", description = "Retrieve all requirements from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/requirements")
    public ResponseEntity<?> getAllRequirements() {
        List<Requirement> requirements = requirementRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", requirements.size());
        response.put("requirements", requirements);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get all stories", description = "Retrieve all user stories from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/stories")
    public ResponseEntity<?> getAllStories() {
        List<Story> stories = storyRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", stories.size());
        response.put("stories", stories);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get all risks", description = "Retrieve all project risks from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/risks")
    public ResponseEntity<?> getAllRisks() {
        List<Risk> risks = riskRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", risks.size());
        response.put("risks", risks);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get all queries", description = "Retrieve all project queries from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/queries")
    public ResponseEntity<?> getAllQueries() {
        List<Query> queries = queryRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", queries.size());
        response.put("queries", queries);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get all NFRs", description = "Retrieve all non-functional requirements from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/nfrs")
    public ResponseEntity<?> getAllNFRs() {
        List<NFR> nfrs = nfrRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", nfrs.size());
        response.put("nfrs", nfrs);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get all chat messages", description = "Retrieve all chat messages from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/chat-messages")
    public ResponseEntity<?> getAllChatMessages() {
        List<ChatMessage> messages = chatMessageRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", messages.size());
        response.put("messages", messages);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get database status", description = "Retrieve counts of all entities in the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation", 
                     content = @Content(mediaType = "application/json"))
    })
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
