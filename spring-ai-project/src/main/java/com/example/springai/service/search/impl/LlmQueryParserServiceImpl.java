package com.example.springai.service.search.impl;

import com.example.springai.entity.FinancialQuery;
import com.example.springai.model.search.SearchParameters;
import com.example.springai.model.search.SearchQuery;
import com.example.springai.service.search.QueryParserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of QueryParserService that uses LLM to parse financial queries
 */
@Service
public class LlmQueryParserServiceImpl implements QueryParserService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public LlmQueryParserServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public SearchQuery parseQuery(String query) {
        List<Message> messages = new ArrayList<>();
        
        messages.add(new SystemMessage("""
            You are a financial query parser. Extract structured information from the user's query.
            Analyze the query to identify:
            1. Query intent (MARKET_DATA, ECONOMIC_INDICATOR, ANALYST_REPORT, EARNINGS_PROJECTION, GENERAL_INFORMATION)
            2. Entities mentioned (companies, indexes, countries, etc.)
            3. Time frames (specific dates, quarters, years)
            4. Report types
            5. Keywords
            6. Analyst names
            7. Company names
            8. Economic indicators
            
            Respond with a JSON object containing the extracted information in this format:
            {
              "intent": "MARKET_DATA|ECONOMIC_INDICATOR|ANALYST_REPORT|EARNINGS_PROJECTION|GENERAL_INFORMATION",
              "entities": ["entity1", "entity2"],
              "timeFrame": {
                "startDate": "YYYY-MM-DD",
                "endDate": "YYYY-MM-DD",
                "period": "Q1|Q2|Q3|Q4|2023|last 6 months"
              },
              "reportType": "earnings|market analysis|economic outlook",
              "keywords": ["keyword1", "keyword2"],
              "analystName": "Analyst Name",
              "companyName": "Company Name",
              "indicatorName": "GDP|CPI|Unemployment Rate"
            }
            
            Only include fields that are relevant to the query. Leave fields empty if they're not mentioned.
            """));
        
        messages.add(new UserMessage(query));
        
        Prompt prompt = new Prompt(messages);
        ChatResponse response = chatClient.call(prompt);
        String content = response.getResult().getOutput().getContent();
        
        try {
            return parseJsonResponse(content, query);
        } catch (Exception e) {
            return createBasicSearchQuery(query);
        }
    }

    private SearchQuery parseJsonResponse(String jsonContent, String originalQuery) throws JsonProcessingException {
        String jsonStr = extractJsonFromString(jsonContent);
        
        @SuppressWarnings("unchecked")
        var responseMap = objectMapper.readValue(jsonStr, java.util.Map.class);
        
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setOriginalQuery(originalQuery);
        
        if (responseMap.containsKey("intent")) {
            try {
                searchQuery.setIntent(SearchQuery.QueryIntent.valueOf((String) responseMap.get("intent")));
            } catch (IllegalArgumentException e) {
                searchQuery.setIntent(SearchQuery.QueryIntent.GENERAL_INFORMATION);
            }
        }
        
        if (responseMap.containsKey("entities") && responseMap.get("entities") instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> entities = (List<String>) responseMap.get("entities");
            searchQuery.setEntities(entities);
        }
        
        if (responseMap.containsKey("timeFrame") && responseMap.get("timeFrame") instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, String> timeFrameMap = (java.util.Map<String, String>) responseMap.get("timeFrame");
            
            SearchQuery.TimeFrame timeFrame = new SearchQuery.TimeFrame();
            timeFrame.setStartDate(timeFrameMap.get("startDate"));
            timeFrame.setEndDate(timeFrameMap.get("endDate"));
            timeFrame.setPeriod(timeFrameMap.get("period"));
            
            searchQuery.setTimeFrame(timeFrame);
        }
        
        if (responseMap.containsKey("reportType")) {
            searchQuery.setReportType((String) responseMap.get("reportType"));
        }
        
        if (responseMap.containsKey("keywords") && responseMap.get("keywords") instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> keywords = (List<String>) responseMap.get("keywords");
            searchQuery.setKeywords(keywords);
        }
        
        return searchQuery;
    }

    private String extractJsonFromString(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        
        if (start >= 0 && end >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        
        return "{}";
    }

    private SearchQuery createBasicSearchQuery(String query) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setOriginalQuery(query);
        searchQuery.setIntent(SearchQuery.QueryIntent.GENERAL_INFORMATION);
        searchQuery.setKeywords(extractKeywords(query));
        return searchQuery;
    }

    private List<String> extractKeywords(String query) {
        List<String> stopWords = Arrays.asList("a", "an", "the", "is", "are", "was", "were", 
                "what", "when", "where", "who", "how", "why", "which", "and", "or", "but", 
                "for", "with", "about", "against", "between", "into", "through", "during", 
                "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", 
                "on", "off", "over", "under", "again", "further", "then", "once");
        
        return Arrays.stream(query.toLowerCase().split("\\W+"))
                .filter(word -> !stopWords.contains(word) && word.length() > 2)
                .toList();
    }

    @Override
    public FinancialQuery convertToEntity(SearchQuery searchQuery) {
        FinancialQuery financialQuery = new FinancialQuery();
        
        financialQuery.setQuestion(searchQuery.getOriginalQuery());
        financialQuery.setContext("Financial research query");
        
        financialQuery.setIntent(searchQuery.getIntent());
        financialQuery.setEntities(searchQuery.getEntities());
        financialQuery.setKeywords(searchQuery.getKeywords());
        financialQuery.setReportType(searchQuery.getReportType());
        
        if (searchQuery.getTimeFrame() != null) {
            financialQuery.setTimePeriod(searchQuery.getTimeFrame().getPeriod());
            financialQuery.setStartDate(searchQuery.getTimeFrame().getStartDate());
            financialQuery.setEndDate(searchQuery.getTimeFrame().getEndDate());
        }
        
        try {
            financialQuery.setParsedQuery(objectMapper.writeValueAsString(searchQuery));
        } catch (JsonProcessingException e) {
            financialQuery.setParsedQuery("Error serializing parsed query");
        }
        
        return financialQuery;
    }

    @Override
    public SearchParameters extractSearchParameters(SearchQuery searchQuery) {
        SearchParameters parameters = new SearchParameters();
        
        parameters.setSearchTerm(searchQuery.getOriginalQuery());
        
        parameters.setEntities(searchQuery.getEntities());
        
        if (searchQuery.getTimeFrame() != null) {
            parameters.setTimeStart(searchQuery.getTimeFrame().getStartDate());
            parameters.setTimeEnd(searchQuery.getTimeFrame().getEndDate());
            parameters.setTimePeriod(searchQuery.getTimeFrame().getPeriod());
        }
        
        parameters.setReportType(searchQuery.getReportType());
        
        parameters.setKeywords(searchQuery.getKeywords());
        
        parameters.setLimit(10);
        
        parameters.setIncludeHistorical(true);
        
        return parameters;
    }
}
