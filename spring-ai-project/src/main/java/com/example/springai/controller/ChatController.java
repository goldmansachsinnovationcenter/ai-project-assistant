package com.example.springai.controller;

import com.example.springai.entity.ChatMessage;
import com.example.springai.entity.Conversation;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.service.ConversationService;
import com.example.springai.tools.ProjectManagementTools;
import com.siva.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final OllamaChatModel chatModel;
    private final ProjectManagementTools projectManagementTools;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ConversationService conversationService;

    public ChatController(OllamaChatModel chatModel, ProjectManagementTools projectManagementTools) {
        this.chatModel = chatModel;
        this.projectManagementTools = projectManagementTools;
    }

    @GetMapping("/ai/chat")
    public Map<String, String> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        String response = ChatClient.create(chatModel)
                .prompt(message)
                .tools(new DateTimeTools(), projectManagementTools)
                .call()
                .content();

        System.out.println(response);

        return Map.of("generation", response != null? response : "No Response");
    }

    @PostMapping("/api/ai/chat/conversation")
    public ResponseEntity<Conversation> startConversation(@RequestBody Map<String, String> request) {
        String userId = request.getOrDefault("userId", "default-user");
        String personality = request.getOrDefault("personality", "helpful");
        
        Conversation conversation = conversationService.startConversation(userId, personality);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/api/ai/chat/conversation/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable String id) {
        return conversationService.findConversationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/ai/chat/conversation/{id}/summary")
    public ResponseEntity<Map<String, Object>> getConversationSummary(@PathVariable String id) {
        var conversationOpt = conversationService.findConversationById(id);
        if (conversationOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String summary = conversationService.generateConversationSummary(id);
        var analytics = conversationService.getConversationAnalytics(id);
        
        Map<String, Object> response = Map.of(
            "summary", summary,
            "analyticsCount", analytics.size(),
            "conversation", conversationOpt.get()
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/ai/chat/personality")
    public ResponseEntity<Map<String, String>> setChatbotPersonality(@RequestBody Map<String, String> request) {
        String personality = request.get("personality");
        String responseStyle = request.getOrDefault("responseStyle", "balanced");
        
        if (personality == null || personality.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Personality is required"));
        }
        
        String validPersonalities = "helpful,professional,friendly,technical,creative";
        if (!validPersonalities.contains(personality.toLowerCase())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid personality. Available: " + validPersonalities));
        }
        
        Map<String, String> response = Map.of(
            "message", String.format("Chatbot personality set to '%s' with response style '%s'", personality, responseStyle),
            "personality", personality,
            "responseStyle", responseStyle
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/ai/chat/conversations")
    public ResponseEntity<List<Conversation>> getUserConversations(@RequestParam(defaultValue = "default-user") String userId) {
        List<Conversation> conversations = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/api/ai/chat/conversation/{id}/context")
    public ResponseEntity<Map<String, String>> updateConversationContext(
            @PathVariable String id, 
            @RequestBody Map<String, String> request) {
        String context = request.get("context");
        if (context == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Context is required"));
        }
        
        conversationService.updateConversationContext(id, context);
        return ResponseEntity.ok(Map.of("message", "Context updated successfully"));
    }

    @GetMapping("/api/ai/chat/analytics")
    public ResponseEntity<Map<String, Object>> getChatbotAnalytics(@RequestParam(defaultValue = "day") String timeRange) {
        try {
            LocalDateTime since;
            switch (timeRange.toLowerCase()) {
                case "hour":
                    since = LocalDateTime.now().minusHours(1);
                    break;
                case "week":
                    since = LocalDateTime.now().minusWeeks(1);
                    break;
                case "month":
                    since = LocalDateTime.now().minusMonths(1);
                    break;
                default:
                    since = LocalDateTime.now().minusDays(1);
            }
            
            var topIntents = conversationService.getTopIntents(since);
            var avgResponseTime = conversationService.getAverageResponseTime(since);
            var sentimentDist = conversationService.getSentimentDistribution(since);
            
            Map<String, Object> analytics = Map.of(
                "timeRange", timeRange,
                "topIntents", topIntents,
                "averageResponseTime", avgResponseTime != null ? avgResponseTime : 0.0,
                "sentimentDistribution", sentimentDist
            );
            
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to get analytics: " + e.getMessage()));
        }
    }
}
