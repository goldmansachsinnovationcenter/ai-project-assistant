package com.example.springai.service.search;

import com.example.springai.model.search.SearchParameters;
import com.example.springai.model.search.SearchResult;

import java.util.List;

/**
 * Service interface for searching across different data sources
 */
public interface SearchService {
    
    /**
     * Search for results using the provided parameters
     * 
     * @param parameters Search parameters extracted from user query
     * @return List of search results
     */
    List<SearchResult> search(SearchParameters parameters);
    
    /**
     * Get the name of the search service
     * 
     * @return Service name
     */
    String getServiceName();
    
    /**
     * Check if this search service is applicable for the given parameters
     * 
     * @param parameters Search parameters
     * @return true if this service should be used for the given parameters
     */
    boolean isApplicable(SearchParameters parameters);
}
