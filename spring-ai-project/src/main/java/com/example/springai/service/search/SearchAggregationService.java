package com.example.springai.service.search;

import com.example.springai.model.search.SearchParameters;
import com.example.springai.model.search.SearchResult;

import java.util.List;

/**
 * Service interface for aggregating and reranking search results from multiple sources
 */
public interface SearchAggregationService {
    
    /**
     * Search across all applicable services and aggregate results
     * 
     * @param parameters Search parameters
     * @return Aggregated and reranked search results
     */
    List<SearchResult> searchAcrossServices(SearchParameters parameters);
    
    /**
     * Rerank search results based on relevance to the original query
     * 
     * @param results List of search results to rerank
     * @param originalQuery The original user query
     * @return Reranked search results
     */
    List<SearchResult> rerank(List<SearchResult> results, String originalQuery);
}
