package com.example.springai.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoryAnalysisResponseTest {

    @Test
    void settersAndGetters_WorkCorrectly() {
        List<String> stories = Arrays.asList("Story1", "Story2");
        List<String> queries = Arrays.asList("Query1", "Query2");
        List<String> risks = Arrays.asList("Risk1", "Risk2");
        List<String> nfrs = Arrays.asList("NFR1", "NFR2");
        String summary = "Test Summary";
        
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setStories(stories);
        response.setQueries(queries);
        response.setRisks(risks);
        response.setNfrs(nfrs);
        response.setSummary(summary);
        
        assertEquals(stories, response.getStories());
        assertEquals(queries, response.getQueries());
        assertEquals(risks, response.getRisks());
        assertEquals(nfrs, response.getNfrs());
        assertEquals(summary, response.getSummary());
    }

    @Test
    void getStories_ReturnsCorrectValue() {
        List<String> stories = Arrays.asList("Story1", "Story2", "Story3");
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setStories(stories);
        
        assertEquals(stories, response.getStories());
        assertEquals(3, response.getStories().size());
    }

    @Test
    void getQueries_ReturnsCorrectValue() {
        List<String> queries = Arrays.asList("Query1", "Query2");
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setQueries(queries);
        
        assertEquals(queries, response.getQueries());
        assertEquals(2, response.getQueries().size());
    }

    @Test
    void getRisks_ReturnsCorrectValue() {
        List<String> risks = Arrays.asList("Risk1", "Risk2");
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setRisks(risks);
        
        assertEquals(risks, response.getRisks());
        assertEquals(2, response.getRisks().size());
    }

    @Test
    void getNfrs_ReturnsCorrectValue() {
        List<String> nfrs = Arrays.asList("NFR1", "NFR2", "NFR3", "NFR4");
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setNfrs(nfrs);
        
        assertEquals(nfrs, response.getNfrs());
        assertEquals(4, response.getNfrs().size());
    }

    @Test
    void getSummary_ReturnsCorrectValue() {
        String summary = "This is a test summary";
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        response.setSummary(summary);
        
        assertEquals(summary, response.getSummary());
    }

    @Test
    void defaultConstructor_InitializesWithNulls() {
        StoryAnalysisResponse response = new StoryAnalysisResponse();
        
        assertNull(response.getStories());
        assertNull(response.getQueries());
        assertNull(response.getRisks());
        assertNull(response.getNfrs());
        assertNull(response.getSummary());
    }
}
