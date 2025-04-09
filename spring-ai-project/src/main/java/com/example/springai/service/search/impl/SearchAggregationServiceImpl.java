package com.example.springai.service.search.impl;

import com.example.springai.model.search.SearchParameters;
import com.example.springai.model.search.SearchResult;
import com.example.springai.service.search.SearchAggregationService;
import com.example.springai.service.search.SearchService;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SearchAggregationService that aggregates and reranks search results
 */
@Service
public class SearchAggregationServiceImpl implements SearchAggregationService {

    private final List<SearchService> searchServices;
    private final ChatClient chatClient;

    @Autowired
    public SearchAggregationServiceImpl(List<SearchService> searchServices, ChatClient chatClient) {
        this.searchServices = searchServices;
        this.chatClient = chatClient;
    }

    @Override
    public List<SearchResult> searchAcrossServices(SearchParameters parameters) {
        List<SearchResult> allResults = new ArrayList<>();
        
        List<SearchService> applicableServices = searchServices.stream()
                .filter(service -> service.isApplicable(parameters))
                .collect(Collectors.toList());
        
        if (applicableServices.isEmpty()) {
            applicableServices = searchServices;
        }
        
        for (SearchService service : applicableServices) {
            try {
                List<SearchResult> serviceResults = service.search(parameters);
                allResults.addAll(serviceResults);
            } catch (Exception e) {
                System.err.println("Error searching with service " + service.getServiceName() + ": " + e.getMessage());
            }
        }
        
        if (parameters.getSearchTerm() != null && !parameters.getSearchTerm().isEmpty()) {
            return rerank(allResults, parameters.getSearchTerm());
        }
        
        return allResults.stream()
                .sorted(Comparator.comparing(SearchResult::getRelevanceScore).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchResult> rerank(List<SearchResult> results, String originalQuery) {
        if (results.isEmpty()) {
            return results;
        }
        
        if (results.size() <= 10) {
            return rerankWithLlm(results, originalQuery);
        }
        
        return results.stream()
                .sorted(Comparator
                        .comparing(SearchResult::getRelevanceScore).reversed()
                        .thenComparing(SearchResult::getPublishDate).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Rerank results using LLM to evaluate relevance to the original query
     */
    private List<SearchResult> rerankWithLlm(List<SearchResult> results, String originalQuery) {
        List<SearchResult> rerankedResults = new ArrayList<>(results);
        
        try {
            String systemPrompt = "You are a financial research assistant that evaluates the relevance of search results to a user's query. " +
                    "For each search result, assign a relevance score between 0.0 and 1.0, where 1.0 is perfectly relevant and 0.0 is completely irrelevant. " +
                    "Consider factors like content relevance, recency, source credibility, and specificity to the query.";
            
            StringBuilder userMessage = new StringBuilder();
            userMessage.append("Original query: ").append(originalQuery).append("\n\n");
            userMessage.append("Search results to evaluate:\n\n");
            
            for (int i = 0; i < results.size(); i++) {
                SearchResult result = results.get(i);
                userMessage.append("Result ").append(i + 1).append(":\n");
                userMessage.append("Title: ").append(result.getTitle()).append("\n");
                userMessage.append("Content: ").append(result.getContent()).append("\n");
                userMessage.append("Source: ").append(result.getSource()).append("\n");
                userMessage.append("Publish Date: ").append(result.getPublishDate()).append("\n\n");
            }
            
            userMessage.append("For each result, provide a relevance score between 0.0 and 1.0 in this format:\n");
            userMessage.append("Result 1: 0.95\nResult 2: 0.82\n...");
            
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemPrompt));
            messages.add(new UserMessage(userMessage.toString()));
            
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatClient.call(prompt);
            String content = response.getResult().getOutput().getContent();
            
            parseAndUpdateRelevanceScores(content, rerankedResults);
            
            rerankedResults.sort(Comparator.comparing(SearchResult::getRelevanceScore).reversed());
            
        } catch (Exception e) {
            System.err.println("Error reranking with LLM: " + e.getMessage());
            rerankedResults.sort(Comparator.comparing(SearchResult::getRelevanceScore).reversed());
        }
        
        return rerankedResults;
    }
    
    /**
     * Parse LLM response and update relevance scores in the results
     */
    private void parseAndUpdateRelevanceScores(String llmResponse, List<SearchResult> results) {
        String[] lines = llmResponse.split("\n");
        
        for (String line : lines) {
            if (line.matches("Result \\d+: \\d+\\.\\d+")) {
                try {
                    String[] parts = line.split(":");
                    int resultIndex = Integer.parseInt(parts[0].replace("Result ", "").trim()) - 1;
                    double score = Double.parseDouble(parts[1].trim());
                    
                    if (resultIndex >= 0 && resultIndex < results.size()) {
                        results.get(resultIndex).setRelevanceScore(score);
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    continue;
                }
            }
        }
    }
}
