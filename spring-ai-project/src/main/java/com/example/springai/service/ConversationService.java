package com.example.springai.service;

import com.example.springai.entity.Conversation;
import com.example.springai.entity.ChatMessage;
import com.example.springai.entity.ChatbotAnalytics;
import com.example.springai.repository.ConversationRepository;
import com.example.springai.repository.ChatMessageRepository;
import com.example.springai.repository.ChatbotAnalyticsRepository;
import com.github.ksuid.Ksuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private ChatbotAnalyticsRepository chatbotAnalyticsRepository;

    public Conversation startConversation(String userId, String personality) {
        Conversation conversation = new Conversation();
        conversation.setId(Ksuid.newKsuid().toString());
        conversation.setUserId(userId);
        conversation.setPersonality(personality != null ? personality : "helpful");
        conversation.setStartTime(LocalDateTime.now());
        conversation.setLastActivity(LocalDateTime.now());
        conversation.setContext("");
        return conversationRepository.save(conversation);
    }

    public Optional<Conversation> findConversationById(String conversationId) {
        return conversationRepository.findById(conversationId);
    }

    public List<Conversation> getUserConversations(String userId) {
        return conversationRepository.findByUserIdOrderByLastActivityDesc(userId);
    }

    public void updateConversationContext(String conversationId, String context) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            conversation.setContext(context);
            conversation.setLastActivity(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
    }

    public void updateConversationActivity(String conversationId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            conversation.setLastActivity(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
    }

    public ChatMessage addMessageToConversation(String conversationId, String prompt, String response) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            ChatMessage message = new ChatMessage(prompt, response);
            message.setConversation(conversation);
            conversation.setLastActivity(LocalDateTime.now());
            conversationRepository.save(conversation);
            return chatMessageRepository.save(message);
        }
        return null;
    }

    public void recordAnalytics(String conversationId, String intent, String sentiment, Integer responseTime) {
        ChatbotAnalytics analytics = new ChatbotAnalytics();
        analytics.setId(Ksuid.newKsuid().toString());
        analytics.setConversationId(conversationId);
        analytics.setIntent(intent);
        analytics.setSentiment(sentiment);
        analytics.setResponseTime(responseTime);
        analytics.setTimestamp(LocalDateTime.now());
        chatbotAnalyticsRepository.save(analytics);
    }

    public List<ChatbotAnalytics> getConversationAnalytics(String conversationId) {
        return chatbotAnalyticsRepository.findByConversationIdOrderByTimestampDesc(conversationId);
    }

    public List<Object[]> getTopIntents(LocalDateTime since) {
        return chatbotAnalyticsRepository.findTopIntentsSince(since);
    }

    public Double getAverageResponseTime(LocalDateTime since) {
        return chatbotAnalyticsRepository.findAverageResponseTimeSince(since);
    }

    public List<Object[]> getSentimentDistribution(LocalDateTime since) {
        return chatbotAnalyticsRepository.findSentimentDistributionSince(since);
    }

    public String generateConversationSummary(String conversationId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isEmpty()) {
            return "Conversation not found";
        }

        Conversation conversation = conversationOpt.get();
        StringBuilder summary = new StringBuilder();
        summary.append("Conversation Summary:\n");
        summary.append("Started: ").append(conversation.getStartTime()).append("\n");
        summary.append("Last Activity: ").append(conversation.getLastActivity()).append("\n");
        summary.append("Personality: ").append(conversation.getPersonality()).append("\n");
        summary.append("Total Messages: ").append(conversation.getMessages().size()).append("\n");
        
        if (!conversation.getContext().isEmpty()) {
            summary.append("Context: ").append(conversation.getContext()).append("\n");
        }

        return summary.toString();
    }
}
