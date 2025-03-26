package com.example.springai.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StoryAnalysisResponseTest {

    @Test
    public void testStoryAnalysisResponse() {
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        
        List<String> stories = Arrays.asList("Story 1", "Story 2");
        List<String> queries = Arrays.asList("Query 1", "Query 2");
        List<String> risks = Arrays.asList("Risk 1", "Risk 2");
        List<String> nfrs = Arrays.asList("NFR 1", "NFR 2");
        String summary = "Test Summary";
        
        response.setStories(stories);
        response.setQueries(queries);
        response.setRisks(risks);
        response.setNfrs(nfrs);
        response.setSummary(summary);
        
        assertNotNull(response.getStories());
        assertNotNull(response.getQueries());
        assertNotNull(response.getRisks());
        assertNotNull(response.getNfrs());
        assertNotNull(response.getSummary());
        
        assertEquals(stories, response.getStories());
        assertEquals(queries, response.getQueries());
        assertEquals(risks, response.getRisks());
        assertEquals(nfrs, response.getNfrs());
        assertEquals(summary, response.getSummary());
    }
}
